package com.shouqiev.mirror.launcher.fragment.map.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.fragment.map.entity.ResultInfo;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.CODE_OK;
import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.DESC_UNKNOW;
import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.HEARDER_TOKEN;
import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.PARAMETER_APP_VERSION;
import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.PARAMETER_DEVICE_ID;
import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.PARAMETER_OS_VERSION;
import static com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant.PARAMETER_TIMESTAMP;
import static java.lang.String.valueOf;

public class RequestUtil implements Callback {
  public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
  private static final String TAG = RequestUtil.class.getSimpleName();
  private static final long timeout = 30 * 1000;
  private IHttpResponse onNetCallback;
  private Handler handler;

  public void setiHttpResponse(IHttpResponse iHttpResponse) {
    this.onNetCallback = iHttpResponse;
  }

  public void request(String url, Map<String, String> paramater, IHttpResponse iHttpResponse) {

  }

  private OkHttpClient okHttpClient;
  private Call call;

  public RequestUtil() {
    buildClient();
  }

  public RequestUtil(IHttpResponse onNetCallback) {
    buildClient();
    this.onNetCallback = onNetCallback;
  }

  private void buildClient() {
    handler = new Handler(Looper.getMainLooper());
    okHttpClient = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
        .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
        .writeTimeout(60 * 1000, TimeUnit.MILLISECONDS)
        .build();
  }

  public void requestPost(String url, Map<String, Object> parameters, IHttpResponse iHttpResponse) {
    this.onNetCallback = iHttpResponse;
    requestPost(url, parameters);
  }

  public void requestPost(String url, Map<String, Object> parameters) {
    Request request;
    if (parameters != null && !parameters.isEmpty()) {
      RequestBody jsonBody = RequestBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(parameters));
      request = addHeaders().url(url).post(jsonBody).build();
    } else {
      request = addHeaders().url(url).build();
    }
    call = okHttpClient.newCall(request);
    call.enqueue(this);
  }

  public void uploadFile(File file, String fileType, Map<String, Object> map) {
    MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
    if (file != null) {
      RequestBody body = RequestBody.create(MediaType.parse(fileType), file);
      requestBody.addFormDataPart("file", file.getName(), body);
    }
    if (map != null) {
      // map 里面是请求中所需要的 key 和 value
      for (Map.Entry entry : map.entrySet()) {
        requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
      }
    }
    RequestBody rb = requestBody.build();
    Request request = addHeaders().url(HttpConstant.FILE_UPLOAD_SERVICE).post(rb).build();
    call = okHttpClient.newCall(request);
    call.enqueue(this);
  }

  private void sendTestJson(String responseStr) {
    if (onNetCallback != null) {
      ResultInfo resultInfo = JSONObject.parseObject(responseStr, ResultInfo.class);
      Log.e(TAG, "code:" + resultInfo.getCode() + " desc:" + resultInfo.getDesc());
      if (resultInfo.getCode() != CODE_OK) {
        onNetCallback.response(false, resultInfo.getDesc());
      } else {
        Log.e(TAG, "result:" + resultInfo.getModelData());
        onNetCallback.response(true, resultInfo.getModelData());
      }
    }
  }

  /**
   * 统一为请求添加头信息
   */
  private Request.Builder addHeaders() {
    Request.Builder builder = new Request.Builder().addHeader("Connection", "keep-alive")
        .addHeader(HEARDER_TOKEN, GoFunMirrorApplication.getToken())
        .addHeader(PARAMETER_DEVICE_ID, CommonUtils.getDeviceId())
        .addHeader(PARAMETER_APP_VERSION, CommonUtils.getAppVersion(GoFunMirrorApplication.getInstance()))
        .addHeader(PARAMETER_OS_VERSION, CommonUtils.getOsVersion())
        .addHeader(PARAMETER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    return builder;
  }

  /**
   * 添加通用参数
   * deviceId         设备唯一编号
   * appVersion       软件版本
   * osVersion        操作系统版本
   * timestamp        时间戳
   */
  private Map<String, Object> addCommonHeader(Map<String, Object> parameters) {
    parameters.put(PARAMETER_DEVICE_ID, CommonUtils.getDeviceId());
    parameters.put(PARAMETER_APP_VERSION, CommonUtils.getAppVersion(GoFunMirrorApplication.getInstance()));
    parameters.put(PARAMETER_OS_VERSION, CommonUtils.getOsVersion());
    parameters.put(PARAMETER_TIMESTAMP, System.currentTimeMillis());
    return parameters;
  }

  public void interrupt() {
    if (call != null) {
      call.cancel();
    }
  }

  @Override public void onFailure(Call call, IOException e) {
    Log.e("IOException", "IOException");
    postResult(false, DESC_UNKNOW);
  }

  @Override public void onResponse(Call call, Response response) throws IOException {
    String responseStr = response.body().string();
    ResultInfo resultInfo = JSONObject.parseObject(responseStr, ResultInfo.class);
    Log.e(TAG, call.request().url().host() + " code:" + resultInfo.getCode() + " desc:" + resultInfo.getDesc());
    if (resultInfo.getCode() != CODE_OK) {
      postResult(false, resultInfo.getDesc());
    } else {
      Log.e(TAG, "result:" + resultInfo.getModelData());
      postResult(true, resultInfo.getModelData());
    }
  }

  private void postResult(final boolean success, final String result) {
    handler.post(new Runnable() {
      @Override public void run() {
        if (onNetCallback != null) {
          onNetCallback.response(success, result);
        }
      }
    });
  }
}

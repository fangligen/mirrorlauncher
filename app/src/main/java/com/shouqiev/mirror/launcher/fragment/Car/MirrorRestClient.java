package com.shouqiev.mirror.launcher.fragment.Car;

import android.util.Log;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Gao on 2014/12/5.
 */
public class MirrorRestClient implements Callback{
  public static final String BASE_URL = "http://172.32.255.3/zyh/dupload.php";
  public static final String IMAGE_URL = "http://172.32.255.3/zyh/";
  public static int timeout = 60 * 1000;

  public static final String TAG = "UploadHelper";
  private static final MediaType MEDIA_TYPE_IMG = MediaType.parse("image/*");
  private  OkHttpClient client=null;
  OnMirrorClientCallBack mirrorClientCallBack;



  public MirrorRestClient(){
    client=new OkHttpClient.Builder().writeTimeout(timeout, TimeUnit.MILLISECONDS).readTimeout(timeout,TimeUnit.MILLISECONDS).connectTimeout(timeout,TimeUnit.MILLISECONDS).build();
  }

  public void setMirrorClientCallBack(OnMirrorClientCallBack mirrorClientCallBack) {
    this.mirrorClientCallBack = mirrorClientCallBack;
  }

  public void upload(File file) {
    RequestBody fileBody = RequestBody.create(MEDIA_TYPE_IMG, file);
    RequestBody requestBody =
        new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), fileBody).build();
    Request request = new Request.Builder().url(BASE_URL).post(requestBody).build();
      Call call=client.newCall(request);
      call.enqueue(this);
  }

  @Override public void onFailure(Call call, IOException e) {
    if(mirrorClientCallBack!=null){
      mirrorClientCallBack.onFailure("IOException");
    }
  }

  @Override public void onResponse(Call call, Response response) throws IOException {
    int code=response.code();
    if(code==200){
      String jsonString = response.body().string();
      mirrorClientCallBack.onSuccess(jsonString);
    }else{
      mirrorClientCallBack.onSuccess("网络错误,错误码："+String.valueOf(code));
    }
  }

  public interface OnMirrorClientCallBack {
    void onSuccess(String data);

    void onFailure(String msg);
  }

}

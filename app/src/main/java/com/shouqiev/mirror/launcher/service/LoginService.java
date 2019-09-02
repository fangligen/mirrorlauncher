package com.shouqiev.mirror.launcher.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.gofun.voice.manager.GFVoiceManager;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.fragment.map.MapEvent;
import com.shouqiev.mirror.launcher.fragment.map.entity.LoginInfo;
import com.shouqiev.mirror.launcher.fragment.map.util.CommonUtils;
import com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant;
import com.shouqiev.mirror.launcher.fragment.map.util.IHttpResponse;
import com.shouqiev.mirror.launcher.fragment.map.util.RSAUtils;
import com.shouqiev.mirror.launcher.fragment.map.util.RequestUtil;
import com.shouqiev.mirror.launcher.utils.PreferencesUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * @author gaoqian
 */
public class LoginService extends Service implements IHttpResponse {

  static final String TAG = LoginService.class.getCanonicalName();
  public static final String LOGIN_PREFERENCE_NAME = "gofun_login_info";
  public static final String LOGIN_PERFERENCE_KEY_TOKEN = "gofun_login_info_token";
  public static final String LOGIN_PERFERENCE_KEY_ORDER_ID = "gofun_login_info_order_id";

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    login();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private void login() {
    String sign = RSAUtils.getInstance().getSign(getApplicationContext());
    if (TextUtils.isEmpty(sign)) {
      return;
    }
    RequestUtil requestUtil = new RequestUtil();
    Map<String, Object> paras = new HashMap<>();
    paras.put("deviceID", CommonUtils.getDeviceId());
    paras.put("terminalID", CommonUtils.getDeviceId());
    paras.put("sign", sign);
    paras.put("checkSign", String.valueOf(1));
    paras.put("timestamp", System.currentTimeMillis());
    requestUtil.requestPost(HttpConstant.LOGIN_URL, paras, this);
  }

  @Override public void response(boolean success, String result) {
    if (!success) {
      Log.e(TAG, "login fail:" + result);
      startLocationService();
      GFVoiceManager.getInstance(getApplicationContext()).doPlayText("订单查询失败，" + result, false);
      handler.postDelayed(new Runnable() {
        @Override public void run() {
          stopLocationService();
        }
      }, 30 * 1000);
      return;
    }

    String txt = getResources().getString(R.string.welcome_str);
    GFVoiceManager.getInstance(getApplicationContext()).doPlayText(txt, false);
    LoginInfo loginInfo = JSONObject.parseObject(result, LoginInfo.class);
    String token = loginInfo.getToken();
    if (!TextUtils.isEmpty(token)) {
      PreferencesUtils.getInstance(getApplicationContext(), LOGIN_PREFERENCE_NAME).putString(LOGIN_PERFERENCE_KEY_TOKEN, token);
      PreferencesUtils.getInstance(getApplicationContext(), LOGIN_PREFERENCE_NAME)
          .putString(LOGIN_PERFERENCE_KEY_ORDER_ID, loginInfo.getOrderId());
      Log.e(TAG, token);
      EventBus.getDefault().post(new MapEvent(MapEvent.EVENT_REFRESH_ORDER));
    }
  }

  String uuid = "";

  private void startLocationService() {
    String PACKAGE_NAME = "com.shouqi.mirror.gofunlocationservice";
    String SERVICE_NAME = "com.shouqi.mirror.gofunlocationservice.GoFunAlarmService";
    String ACTION_START_LOCATION = "com.shouqi.mirror.location.service.start";
    uuid = UUID.randomUUID().toString();
    Intent i = new Intent();
    i.setComponent(new ComponentName(PACKAGE_NAME, SERVICE_NAME));
    i.setAction(ACTION_START_LOCATION);
    i.putExtra("orderId", uuid);
    startService(i);
  }

  private void stopLocationService() {
    String PACKAGE_NAME = "com.shouqi.mirror.gofunlocationservice";
    String SERVICE_NAME = "com.shouqi.mirror.gofunlocationservice.GoFunAlarmService";
    String ACTION_STOP_LOCATION = "com.shouqi.mirror.location.service.stop";
    Intent i = new Intent();
    i.setComponent(new ComponentName(PACKAGE_NAME, SERVICE_NAME));
    i.setAction(ACTION_STOP_LOCATION);
    i.putExtra("orderId", uuid);
    startService(i);
  }

  Handler handler = new Handler();
}

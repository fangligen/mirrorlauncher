package com.shouqiev.mirror.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.shouqiev.mirror.launcher.fragment.map.util.CommonUtils;
import com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant;
import com.shouqiev.mirror.launcher.fragment.map.util.IHttpResponse;
import com.shouqiev.mirror.launcher.fragment.map.util.RSAUtils;
import com.shouqiev.mirror.launcher.fragment.map.util.RequestUtil;
import com.shouqiev.mirror.launcher.utils.PreferencesUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoqian
 */
public class RregisterService extends Service {

  public static final String PREFERENCE_REGISTER_NAME = "preference_register_mirror";
  public static final String PREFERENCE_REGISTER_KEY = "preference_register_key";

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    register();
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private void register() {
    String sign = RSAUtils.getInstance().getSign(getApplicationContext());
    RequestUtil requestUtil = new RequestUtil();
    Map<String, Object> map = new HashMap<>();
    map.put("deviceID", CommonUtils.getDeviceId());
    map.put("terminalID", CommonUtils.getDeviceId());
    map.put("sign", sign);
    map.put("timestamp", System.currentTimeMillis());
    requestUtil.requestPost(HttpConstant.REGISTER_URL, map, new IHttpResponse() {
      @Override public void response(boolean success, String result) {
        if (success) {
          PreferencesUtils preferencesUtils = PreferencesUtils.getInstance(getApplicationContext(), PREFERENCE_REGISTER_NAME);
          preferencesUtils.getBoolean(PREFERENCE_REGISTER_KEY, true);
        }
      }
    });
  }
}

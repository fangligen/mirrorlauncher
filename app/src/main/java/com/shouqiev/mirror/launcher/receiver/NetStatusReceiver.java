package com.shouqiev.mirror.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.shouqiev.mirror.launcher.service.LoginService;

/**
 * @author gaoqian
 */
public class NetStatusReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    if (intent == null) {
      return;
    }
    String action = intent.getAction();
    // 网络状态变化
    if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
      updateNetChange(context, isNetAvailable(context));
    }
  }

  private void updateNetChange(Context context, boolean connected) {
    if (connected) {
      context.startService(new Intent(context, LoginService.class));
    }
  }

  public static boolean isNetAvailable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isAvailable()) {
      return true;
    } else {
      return false;
    }
  }
}

package com.shouqi.mirror.gofunlocationservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

public class GLReceiver extends BroadcastReceiver {
  @Override public void onReceive(Context context, Intent intent) {
    if (intent == null) {
      return;
    }
    String action = intent.getAction();
    if (TextUtils.isEmpty(action)) {
      return;
    }
    switch (action){
      case Intent.ACTION_BOOT_COMPLETED:
        break;
      case ConnectivityManager.CONNECTIVITY_ACTION:
        break;
        default:
    }
  }
}

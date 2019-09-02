package com.shouqi.mirror.gofunlocationservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author gaoqian
 * @date 2017/7/26
 */

public class AlarmReceiver extends BroadcastReceiver {
  @Override public void onReceive(Context context, Intent intent) {
    Intent i = new Intent(context, GoFunAlarmService.class);
    i.setAction(intent.getAction());
    String orderId = intent.getStringExtra("orderId");
    i.putExtra("orderId", orderId);
    Log.e("location", "orderId is " + orderId);
    context.startService(i);
  }
}

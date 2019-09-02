package com.shouqi.mirror.gofunlocationservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author gaoqian
 * @date 2017/7/26
 */

public class GoFunAlarmService extends Service {

  private static final int INTERVAL = 1000 * 10;

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
      String action = intent.getAction();
      Log.e("alarmService", "action is:" + action);
      AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
      Intent i = new Intent(this, AlarmReceiver.class);
      i.setAction(GoFunLocationService.ACTION_START_LOCATION);
      i.putExtra("orderId", intent.getStringExtra("orderId"));
      PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
      if (GoFunLocationService.ACTION_STOP_LOCATION.equals(action)) {
        manager.cancel(pi);
      } else {
        long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
      }
      Intent locationService = new Intent(getApplicationContext(), GoFunLocationService.class);
      locationService.putExtra("orderId", intent.getStringExtra("orderId"));
      locationService.setAction(action);
      startService(locationService);
    }

    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}

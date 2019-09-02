package com.shouqiev.mirror.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shouqiev.mirror.launcher.service.GoFunAlarmService;

/**
 *
 * @author gaoqian
 * @date 2017/7/26
 */

public class AlarmReceiver extends BroadcastReceiver {
  @Override public void onReceive(Context context, Intent intent) {
    Intent i = new Intent(context, GoFunAlarmService.class);
    context.startService(i);
    AlarmObservable.getInstance().notifyObservers();
  }
}

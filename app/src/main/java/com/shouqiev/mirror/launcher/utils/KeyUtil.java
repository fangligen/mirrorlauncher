package com.shouqiev.mirror.launcher.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

public class KeyUtil {
  public static void hideBottomUIMenu(Activity mContext) {
    if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
      View v = mContext.getWindow().getDecorView();
      v.setSystemUiVisibility(View.GONE);
    } else if (Build.VERSION.SDK_INT >= 19) {
      WindowManager.LayoutParams params = mContext.getWindow().getAttributes();
      params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
      mContext.getWindow().setAttributes(params);
    }
  }
}

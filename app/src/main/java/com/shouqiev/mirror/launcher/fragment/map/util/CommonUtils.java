package com.shouqiev.mirror.launcher.fragment.map.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

public class CommonUtils {

  /**
   * Build.BOARD // 主板
   * Build.BRAND // android系统定制商
   * Build.CPU_ABI // cpu指令集
   * Build.DEVICE // 设备参数
   * Build.DISPLAY // 显示屏参数
   * Build.FINGERPRINT // 硬件名称
   * Build.HOST
   * Build.ID // 修订版本列表
   * Build.MANUFACTURER // 硬件制造商
   * Build.MODEL // 版本
   * Build.PRODUCT // 手机制造商
   * Build.TAGS // 描述build的标签
   * Build.TIME
   * Build.TYPE // builder类型
   * Build.USER
   * Build.VERSION.CODENAME// 当前开发代号
   * Build.VERSION.INCREMENTAL// 源码控制版本号
   * Build.VERSION.RELEASE// 版本字符串
   * Build.VERSION.SDK// 版本号
   * Build.VERSION.SDK_INT// 版本号
   */
  public static final int getSDKCode() {
    return Build.VERSION.SDK_INT;
  }

  public static String getAppVersion(Context context) {
    //获取包管理者对象
    PackageManager pm = context.getPackageManager();
    try {
      PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
      return String.valueOf(info.versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getOsVersion() {
    return Build.VERSION.RELEASE;
  }

  public static String getDeviceId() {
    String SerialNumber = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      SerialNumber = Build.getSerial();
    } else {
      SerialNumber = android.os.Build.SERIAL;
    }
    return "001007100999";
  }

  public static String getSDpath() {
    return Environment.getExternalStorageDirectory().getPath();
  }
}

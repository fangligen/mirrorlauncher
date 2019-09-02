package com.shouqiev.mirror.launcher;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.gofun.voice.manager.GFVoiceManager;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.shouqiev.mirror.launcher.location.GoFunLocationListener;
import com.shouqiev.mirror.launcher.location.GoFunLocationManager;
import com.shouqiev.mirror.launcher.receiver.NetStatusReceiver;
import com.shouqiev.mirror.launcher.service.LoginService;
import com.shouqiev.mirror.launcher.utils.AppInfoProvider;
import com.shouqiev.mirror.launcher.utils.LauncherUtil;
import com.shouqiev.mirror.launcher.utils.PreferencesUtils;
import com.tencent.bugly.crashreport.CrashReport;

import static com.shouqiev.mirror.launcher.service.LoginService.LOGIN_PERFERENCE_KEY_ORDER_ID;
import static com.shouqiev.mirror.launcher.service.LoginService.LOGIN_PERFERENCE_KEY_TOKEN;
import static com.shouqiev.mirror.launcher.service.LoginService.LOGIN_PREFERENCE_NAME;
import static com.shouqiev.mirror.launcher.service.RregisterService.PREFERENCE_REGISTER_KEY;
import static com.shouqiev.mirror.launcher.service.RregisterService.PREFERENCE_REGISTER_NAME;

public class GoFunMirrorApplication extends Application implements GoFunLocationListener {
  private static GoFunMirrorApplication mInstance = null;

  public static AMapLocation myLocation;
  public BMapManager mBMapManager = null;

  public static GoFunMirrorApplication getInstance() {
    return mInstance;
  }

  private String getCurProcessName(Context context) {
    try {
      int pid = android.os.Process.myPid();
      ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
        if (appProcess != null && appProcess.pid == pid) {
          return appProcess.processName;
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    mInstance = this;
    initSpeech();
    GoFunLocationManager goFunLocationManager = GoFunLocationManager.getGoFunLocationManager();
    goFunLocationManager.init(getApplicationContext());
    goFunLocationManager.addGoFunLocationListener(this);
    goFunLocationManager.startLocation(getApplicationContext());
    String curProcessName = getCurProcessName(this);
    String packageName = getPackageName();
    if (curProcessName != null && !curProcessName.equals(packageName)) {
      return;
    }
    LauncherUtil.init(getApplicationContext());
    AppInfoProvider.getInstance(getApplicationContext()).initData();
    initTypeface();
    CrashReport.initCrashReport(getApplicationContext(), "12c3d60415", false);
    register();
    initLogger();
    initEngineManager(this);
  }

  public void initEngineManager(Context context) {
    if (mBMapManager == null) {
      mBMapManager = new BMapManager(context);
    }

    if (!mBMapManager.init(new MyGeneralListener())) {

    }
  }

  /**
   * 常用事件监听，用来处理通常的网络错误，授权验证错误等
   */
  static class MyGeneralListener implements MKGeneralListener {

    @Override public void onGetPermissionState(int iError) {
      // 非零值表示key验证未通过
      if (iError != 0) {
        // 授权Key错误：
        Toast.makeText(GoFunMirrorApplication.getInstance().getApplicationContext(), "请输入正确的Key,并检查您的网络连接是否正常！error: " + iError,
            Toast.LENGTH_LONG).show();
      }
    }
  }

  private void initLogger() {
    Logger.addLogAdapter(new AndroidLogAdapter() {
      @Override public boolean isLoggable(int priority, @Nullable String tag) {
        return BuildConfig.DEBUG;
      }
    });
  }

  public static String getOrderId() {
    return PreferencesUtils.getString(getInstance(), LOGIN_PREFERENCE_NAME, LOGIN_PERFERENCE_KEY_ORDER_ID);
  }

  public static String getToken() {
    return PreferencesUtils.getString(getInstance(), LOGIN_PREFERENCE_NAME, LOGIN_PERFERENCE_KEY_TOKEN);
  }

  private void requestLogin() {
    if (NetStatusReceiver.isNetAvailable(getApplicationContext())) {
      startService(new Intent(getApplicationContext(), LoginService.class));
    }
  }

  private void initSpeech() {
    GFVoiceManager.getInstance(this).initIflytek();
    Intent service = new Intent(this, VoiceServices.class);
    startService(service);
  }

  private void initTypeface() {
    TypefaceCollection typeface =
        new TypefaceCollection.Builder().set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/bebas.ttf")).create();
    TypefaceHelper.init(typeface);
  }

  private void register() {
    boolean regist = PreferencesUtils.getBoolean(getApplicationContext(), PREFERENCE_REGISTER_NAME, PREFERENCE_REGISTER_KEY);
    //if (!regist) {
    //  startService(new Intent(getApplicationContext(), RregisterService.class));
    //} else {
    requestLogin();
    //}
  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    myLocation = aMapLocation;
  }

  @Override public void onTerminate() {
    super.onTerminate();
    GoFunLocationManager.getGoFunLocationManager().removeListener(this);
  }
}

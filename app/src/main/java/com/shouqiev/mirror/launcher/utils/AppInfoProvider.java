package com.shouqiev.mirror.launcher.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.model.AppInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * app info
 */
public class AppInfoProvider implements Handler.Callback {

  private final static String CONFIG_APP_PATH = "app_config.json";
  private static AppInfoProvider mInstance;
  private Handler mHandler;
  private boolean isInit = false;

  public static AppInfoProvider getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new AppInfoProvider(context);
    }
    return mInstance;
  }

  private QueryAppListener queryAppListener = null;
  private PackageManager mPackageManager;
  private Context mContext;
  private List<AppInfo> mAllAppList;
  List<AppInfo> mlistAppInfoSystem = new ArrayList<AppInfo>();
  List<AppInfo> mlistAppInfoThrid = new ArrayList<AppInfo>();

  public AppInfoProvider(Context context) {
    this.mPackageManager = context.getPackageManager();
    this.mContext = context;
    this.mHandler = new Handler(this);
  }

  public synchronized void initData() {
    if (isInit) {
      return;
    }
    isInit = true;
    mAllAppList = null;
    new Thread() {
      @Override public void run() {
        super.run();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos =
            mPackageManager.queryIntentActivities(mainIntent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(mPackageManager));

        List<AppInfo> configAppList = new ArrayList<AppInfo>();
        try {
          String configJfon = getJson(CONFIG_APP_PATH, mContext);
          if (!TextUtils.isEmpty(configJfon)) {
            JSONObject obj = JSON.parseObject(configJfon);
            configAppList = JSON.parseArray(obj.getString("apps"), AppInfo.class);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        mAllAppList = new ArrayList<AppInfo>();
        if (mAllAppList != null) {
          mAllAppList.clear();
          mlistAppInfoSystem.clear();
          mlistAppInfoThrid.clear();
          for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name;
            String pkgName = reInfo.activityInfo.packageName;
            String appLabel = (String) reInfo.loadLabel(mPackageManager);
            Drawable icon = reInfo.loadIcon(mPackageManager);
            if (GoFunMirrorApplication.getInstance().getPackageName().equals(pkgName)) {
              continue;
            }
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(pkgName, activityName));

            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(pkgName);
            appInfo.setAppName(appLabel);
            appInfo.setIcon(icon);
            appInfo.setActivityName(activityName);
            appInfo.setIntent(launchIntent);

            try {
              PackageInfo mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
              if (configAppList != null && configAppList.contains(appInfo)) {
                if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                  appInfo.setSystem(false);
                  mlistAppInfoThrid.add(appInfo);
                } else {
                  appInfo.setSystem(true);
                  mlistAppInfoSystem.add(appInfo);
                }
              }
            } catch (PackageManager.NameNotFoundException e) {
              LogUtil.e("AppInfoProvider", e.getMessage().toString());
            }
          }
          mAllAppList.addAll(mlistAppInfoSystem);
          mAllAppList.addAll(mlistAppInfoThrid);
          mHandler.sendEmptyMessage(1);
        }
        isInit = false;
      }
    }.start();
  }

  @Override public boolean handleMessage(Message msg) {
    switch (msg.what) {
      case 1:
        if (queryAppListener != null) {
          queryAppListener.onQuery(mAllAppList);
        }
        break;
    }
    return false;
  }

  public synchronized void queryAppInfo(QueryAppListener listener) {
    if (mAllAppList == null || mAllAppList.size() == 0) {
      queryAppListener = listener;
      initData();
    } else if (listener != null) {
      listener.onQuery(mAllAppList);
    }
  }

  public interface QueryAppListener {
    public void onQuery(List<AppInfo> applist);
  }

  public static String getJson(String fileName, Context context) {
    //将json数据变成字符串
    StringBuilder stringBuilder = new StringBuilder();
    try {
      //获取assets资源管理器
      AssetManager assetManager = context.getAssets();
      //通过管理器打开文件并读取
      BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
      String line;
      while ((line = bf.readLine()) != null) {
        stringBuilder.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return stringBuilder.toString();
  }
}

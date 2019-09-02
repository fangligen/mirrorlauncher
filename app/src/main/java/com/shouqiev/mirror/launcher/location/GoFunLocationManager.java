package com.shouqiev.mirror.launcher.location;

import android.content.Context;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoqian
 */
public class GoFunLocationManager implements AMapLocationListener {
  private static volatile GoFunLocationManager goFunLocationManager = new GoFunLocationManager();
  public AMapLocationClient mLocationClient = null;
  public AMapLocationClientOption mLocationOption = null;

  private List<GoFunLocationListener> listeners = new ArrayList<>();

  private GoFunLocationManager() {

  }

  public static GoFunLocationManager getGoFunLocationManager() {
    return goFunLocationManager;
  }

  public void init(Context context) {
    mLocationClient = new AMapLocationClient(context.getApplicationContext());
    mLocationClient.setLocationListener(this);
    mLocationOption = new AMapLocationClientOption();
    initOption();
    mLocationClient.setLocationOption(mLocationOption);
  }

  public void startLocation(Context context) {
    if (mLocationClient == null) {
      init(context);
    }
    mLocationClient.startLocation();
  }

  public void stopLocation() {
    if (mLocationClient != null) {
      mLocationClient.stopLocation();
    }
  }

  private void initOption() {
    //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
    mLocationOption.setInterval(10 * 1000);
    //设置是否返回地址信息（默认返回地址信息）
    mLocationOption.setNeedAddress(true);
    //设置是否允许模拟位置,默认为true，允许模拟位置
    mLocationOption.setMockEnable(true);
  }

  public void addGoFunLocationListener(GoFunLocationListener goFunLocationListener) {
    if (!listeners.contains(goFunLocationListener)) {
      listeners.add(goFunLocationListener);
    }
  }

  public void removeListener(GoFunLocationListener locationListener) {
    if (listeners.contains(locationListener)) {
      listeners.remove(locationListener);
    }
  }

  public void clearListener() {
    listeners.clear();
  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    if (aMapLocation != null) {
      for (GoFunLocationListener listener : listeners) {
        listener.onLocationChanged(aMapLocation);
      }
    }
  }
}

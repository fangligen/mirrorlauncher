package com.shouqiev.mirror.launcher.fragment.map;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import java.lang.reflect.Field;

public abstract class BaseMapFragment extends BaseFragment implements AMap.OnMarkerClickListener {
  static final String TAG = BaseMapFragment.class.getCanonicalName();

  protected AMap aMap;
  public @BindView(R.id.fragment_map_view) TextureMapView textureMapView;

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getView() != null) {
      ButterKnife.bind(this, getView());
      if (textureMapView != null) {
        textureMapView.onCreate(savedInstanceState);
        aMap = textureMapView.getMap();
        aMap.setTrafficEnabled(true);
        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
        initMyLocation();
        AMapLocation location = GoFunMirrorApplication.myLocation;
        if (location != null) {
          LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
          moveCamera(latLng, location.getBearing());
        }
      }
    }
  }

  @Override public void onResume() {
    super.onResume();
    if (textureMapView != null) {
      textureMapView.onResume();
      Log.e(TAG, "onResume");
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (textureMapView != null) {
      textureMapView.onPause();
      Log.e(TAG, "onPause");
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (textureMapView != null) {
      textureMapView.onSaveInstanceState(outState);
      Log.e(TAG, "onSaveInstanceState");
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (textureMapView != null) {
      textureMapView.onDestroy();
      Log.e(TAG, "onDestroy");
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    Log.e(TAG, "onDetach");
  }

  private void initMyLocation() {
    MyLocationStyle myLocationStyle;
    //初始化定位蓝点样式类
    myLocationStyle = new MyLocationStyle();
    //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
    //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
    myLocationStyle.interval(0);
    myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_car));
    myLocationStyle.strokeColor(Color.parseColor("#55ffffff"));
    myLocationStyle.radiusFillColor(Color.parseColor("#88ffffff"));
    //设置定位蓝点的Style
    aMap.setMyLocationStyle(myLocationStyle);
    //设置默认定位按钮是否显示，非必需设置。
    aMap.getUiSettings().setMyLocationButtonEnabled(true);
    aMap.getUiSettings().setZoomControlsEnabled(false);
    // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    aMap.setMyLocationEnabled(true);
    aMap.setMapType(AMap.MAP_TYPE_NIGHT);
    aMap.setTrafficEnabled(true);
    aMap.setOnMarkerClickListener(this);
  }

  public void moveCamera(LatLng latLng, float bearing) {
    if (aMap != null) {
      CameraPosition cameraPosition = new CameraPosition(latLng, 16, 0, bearing);
      aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
  }

  public boolean locationChange(AMapLocation myLocation, AMapLocation newLocation) {
    if (myLocation != null && newLocation != null) {
      int rlat = Double.compare(myLocation.getLatitude(), newLocation.getLatitude());
      int rlng = Double.compare(myLocation.getLongitude(), newLocation.getLongitude());
      if (rlat != 0 || rlng != 0) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  }
}

package com.shouqiev.mirror.launcher.fragment.map;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.activity.MainActivity;
import com.shouqiev.mirror.launcher.activity.PhoneActivity;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.fragment.map.entity.OrderInfo;
import com.shouqiev.mirror.launcher.fragment.map.entity.ParkingInfo;
import com.shouqiev.mirror.launcher.fragment.map.util.AMapUtil;
import com.shouqiev.mirror.launcher.fragment.map.widget.DrivingRouteOverlay;
import com.shouqiev.mirror.launcher.location.GoFunLocationListener;
import com.shouqiev.mirror.launcher.location.GoFunLocationManager;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

/**
 * @author gaoqian
 */
public class GoFunRouteFragment extends BaseMapFragment
    implements RouteSearch.OnRouteSearchListener, GoFunLocationListener, GoFunRouteMapContract.IRouteMapView {

  @BindView(R.id.fragment_route_map_end_name) TextView endName;
  @BindView(R.id.fragment_route_map_end_time) TextView time;
  @BindView(R.id.fragment_map_route_cur_time) TextView curTime;
  @BindView(R.id.fragment_map_route_cur_date) TextView curDate;
  @BindView(R.id.fragment_route_map_dis) TextView orderDis;
  @BindView(R.id.fragment_route_map_time) TextView orderTime;
  @BindView(R.id.fragment_route_map_cost) TextView orderCost;
  @BindView(R.id.fragment_map_route_des_info) View layout;

  /**
   * 起点
   */
  private LatLonPoint mStartPoint = null;
  /**
   * 终点
   */
  private LatLonPoint mEndPoint = null;

  private RouteSearch routeSearch;
  private AMapLocation myLocation;

  private GoFunRouteMapPresenter goFunRouteMapPresenter;

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (textureMapView != null) {
      goFunRouteMapPresenter = new GoFunRouteMapPresenter(this);
      routeSearch = new RouteSearch(getActivity());
      routeSearch.setRouteSearchListener(this);
      GoFunLocationManager.getGoFunLocationManager().addGoFunLocationListener(this);
      curTime.setText(AMapUtil.getFriendlyCurTime());
      curDate.setText(AMapUtil.getFriendlyCurDate());
      typeface(curDate);
      typeface(curTime);
      typeface(orderCost);
      typeface(orderDis);
      typeface(orderTime);
      typeface(time);
    }
    int value = Settings.System.getInt(getActivity().getContentResolver(), "open_log", -1);
    Log.e("key", "key is:" + value);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map_route, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    goFunRouteMapPresenter.stopRefreshOrderInfo();
    GoFunLocationManager.getGoFunLocationManager().removeListener(this);
  }

  @OnClick(R.id.fragment_route_map_call_btn) void makeCall() {
    getActivity().startActivity(new Intent(getActivity(), PhoneActivity.class));
  }

  @OnClick(R.id.fragment_map_route_start_navi) void startNavi() {
    ParkingBean parkingBean = new ParkingBean();
    parkingBean.setLatitude(mEndPoint.getLatitude());
    parkingBean.setLongitude(mEndPoint.getLongitude());
    ((MainActivity) getActivity()).startNavi(parkingBean);
  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    curTime.setText(AMapUtil.getFriendlyCurTime());
    if (locationChange(myLocation, aMapLocation)) {
      this.myLocation = aMapLocation;
      moveCamera(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), aMapLocation.getBearing());
      mStartPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
      searchRoute(RouteSearch.DRIVING_SINGLE_AVOID_CONGESTION);
      Log.e(TAG, "location change");
    }
  }

  private void searchRoute(int mode) {
    if (mStartPoint == null || mEndPoint == null) {
      return;
    }
    final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
    // 驾车路径规划
    // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
    // 异步路径规划驾车模式查询
    routeSearch.calculateDriveRouteAsyn(query);
  }

  DriveRouteResult mDriveRouteResult;

  private void showDriveRoute(DriveRouteResult result, int errorCode) {
    aMap.clear();// 清理地图上的所有覆盖物
    if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
      if (result != null && result.getPaths() != null) {
        if (result.getPaths().size() > 0) {
          mDriveRouteResult = result;
          final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
          if (drivePath == null) {
            return;
          }
          DrivingRouteOverlay drivingRouteOverlay =
              new DrivingRouteOverlay(getActivity(), aMap, drivePath, mDriveRouteResult.getStartPos(),
                  mDriveRouteResult.getTargetPos(), null);
          //设置节点marker是否显示
          drivingRouteOverlay.setNodeIconVisibility(false);
          //是否用颜色展示交通拥堵情况，默认true
          drivingRouteOverlay.setIsColorfulline(true);
          drivingRouteOverlay.removeFromMap();
          drivingRouteOverlay.addToMap();
          drivingRouteOverlay.zoomToSpan();
          aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
          int dis = (int) drivePath.getDistance();
          int dur = (int) drivePath.getDuration();
          //String des = "剩余" + AMapUtil.getFriendlyLength(dis) + " | 预计" + AMapUtil.getFriendlyArriveTime(dur * 1000) + "到达";
          SpannableStringBuilder stringBuilder = AMapUtil.getFriendlyRouteInfo(dis, dur);
          time.setText(stringBuilder);
          layout.setVisibility(View.VISIBLE);
        }
      }
    }
  }

  @Override public void showOrderInfo(OrderInfo orderInfo) {
    if (orderInfo == null) {
      return;
    }

    ParkingInfo parkingInfo = orderInfo.getReturnParking();
    if (parkingInfo != null) {
      endName.setText(parkingInfo.getName());
      mEndPoint = new LatLonPoint(parkingInfo.getLatitude(), parkingInfo.getLongitude());
      searchRoute(RouteSearch.DRIVING_SINGLE_AVOID_CONGESTION);
    }
    String duration = String.valueOf(orderInfo.getDuration() / 60);
    orderTime.setText(duration);
    String distance = String.valueOf(orderInfo.getDistance() / 1000);
    orderDis.setText(distance);
  }

  @Override public boolean onMarkerClick(Marker marker) {
    return false;
  }

  @Override public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

  }

  @Override public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
    showDriveRoute(driveRouteResult, i);
  }

  @Override public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

  }

  @Override public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

  }
}

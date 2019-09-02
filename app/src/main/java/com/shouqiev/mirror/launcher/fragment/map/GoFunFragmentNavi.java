package com.shouqiev.mirror.launcher.fragment.map;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.baidu.lbsapi.panoramaview.ImageMarker;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.baidu.lbsapi.tools.CoordinateConverter;
import com.baidu.lbsapi.tools.Point;
import com.gofun.voice.manager.GFVoiceManager;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingDaoUtils;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceConstant;
import com.shouqiev.mirror.launcher.fragment.map.search.GoFunMapSearchContract;
import com.shouqiev.mirror.launcher.fragment.map.search.GoFunSearchPresenter;
import com.shouqiev.mirror.launcher.fragment.map.util.AMapUtil;
import com.shouqiev.mirror.launcher.fragment.map.widget.NextTurnTipView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

/**
 * @author gaoqian
 */
public class GoFunFragmentNavi extends BaseFragment
    implements AMapNaviListener, AMapNaviViewListener, PanoramaViewListener, GoFunMapSearchContract.IGoFunMapSearchView {

  static final int OPEN_PANORAMA_DISTANCE = 120;
  static final int SHOW_GOFUN_PARKING_DISTANCE = 2 * 1000;

  @BindView(R.id.fragment_map_navi_view) AMapNaviView aMapNaviView;
  @BindView(R.id.fragment_navi_info_lay) LinearLayout naviLay;
  @BindView(R.id.fragment_map_navi_arrive_time) TextView arriveTime;
  @BindView(R.id.fragment_map_navi_next_dis) TextView nextDis;
  @BindView(R.id.fragment_map_navi_next_icon) NextTurnTipView nextIcon;
  @BindView(R.id.fragment_map_navi_next_road) TextView nextRoad;
  @BindView(R.id.fragment_map_navi_info_lay) RelativeLayout naviInfoLay;
  @BindView(R.id.fragment_map_navi_cross_img) ImageView crossImg;
  @BindView(R.id.panorama) PanoramaView panoramaView;
  @BindView(R.id.fragment_navi_close_panorama) ImageView closePanoramaBtn;

  private AMap aMap;
  private AMapNavi aMapNavi;

  //选中的地点
  private ParkingBean parkingBean;
  static boolean isNavi = false;

  GoFunMapSearchContract.IGoFunMapSearchPresenter goFunMapSearchPresenter;

  public static GoFunFragmentNavi newInstance(ParkingBean parkingBean) {
    Bundle args = new Bundle();
    GoFunFragmentNavi fragment = new GoFunFragmentNavi();
    args.putSerializable("park", parkingBean);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      parkingBean = (ParkingBean) args.getSerializable("park");
    }
    EventBus.getDefault().register(this);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (aMapNaviView != null) {
      initNaviMap(savedInstanceState);
      typeface(nextDis);
      typeface(arriveTime);
    }
    if (panoramaView != null) {
      if (GoFunMirrorApplication.myLocation != null) {
        panoramaView.setPanoramaViewListener(this);
        panoramaView.setVisibility(View.INVISIBLE);
        closePanoramaBtn.setVisibility(View.INVISIBLE);
      }
    }
    goFunMapSearchPresenter = new GoFunSearchPresenter(this);
  }

  private void initNaviMap(Bundle savedInstanceState) {
    if (aMapNaviView != null) {
      aMapNaviView.onCreate(savedInstanceState);
      aMapNaviView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          aMapNaviView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          AMapNaviViewOptions aMapOptions = aMapNaviView.getViewOptions();
          aMapOptions.setNaviNight(true);
          aMapOptions.setAutoLockCar(true);
          Bitmap carImg = BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_car).getBitmap();
          aMapOptions.setCarBitmap(carImg);
          aMapOptions.setTrafficBarEnabled(false);
          aMapOptions.setLayoutVisible(false);
          aMapOptions.setRealCrossDisplayShow(true);
          aMapOptions.setLockMapDelayed(3 * 1000);
          aMapOptions.setModeCrossDisplayShow(false);
          aMapOptions.setTrafficInfoUpdateEnabled(true);
          aMapOptions.setTrafficLine(true);
          int h = aMapNaviView.getHeight();
          double naviInfoH = naviInfoLay.getHeight() + 0.5d;
          double y = (h - naviInfoH) / 2 / h;
          aMapOptions.setPointToCenter(1.0 / 2, y);
          aMapNaviView.setViewOptions(aMapOptions);
        }
      });
      aMap = aMapNaviView.getMap();
      aMap.setTrafficEnabled(true);
      aMapNavi = AMapNavi.getInstance(getActivity().getApplicationContext());
      aMapNavi.addAMapNaviListener(this);
      aMapNavi.setEmulatorNaviSpeed(60);
      aMapNavi.setUseInnerVoice(true);
      aMapNaviView.setAMapNaviViewListener(this);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map_navi, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    if (aMapNaviView != null) {
      aMapNaviView.onResume();
    }
    if (panoramaView != null) {
      panoramaView.onResume();
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (aMapNaviView != null) {
      aMapNaviView.onPause();
    }
    if (panoramaView != null) {
      panoramaView.onPause();
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (aMapNaviView != null) {
      aMapNaviView.onSaveInstanceState(outState);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (aMapNaviView != null) {
      aMapNaviView.onDestroy();
    }
    if (panoramaView != null) {
      panoramaView.destroy();
    }
    isNavi = false;
    EventBus.getDefault().unregister(this);
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
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void Event(VoiceEvent event) {
    if (event.status == VoiceEvent.VOICE_STATUS_EXIT_NAVI) {
      stopNavi();
      GFVoiceManager.getInstance(getActivity()).doPlayText(VoiceConstant.EXIT_NAVI_SUCESS);
    }
  }

  @OnClick(R.id.fragment_map_navi_stop_btn) public void stopNavi() {
    if (aMapNavi != null) {
      aMapNavi.stopNavi();
      aMapNavi.destroy();
    }
    isNavi = false;
    VoiceEvent voiceEvent = new VoiceEvent();
    voiceEvent.status = VoiceEvent.VOICE_STATUS_EXIT_NAVI_COMPLETED;
    EventBus.getDefault().post(voiceEvent);
    ((GoFunMapContainerFragment) getParentFragment()).switchFragment(false, null, null);
  }

  @OnClick(R.id.fragment_navi_close_panorama) void closePanorama() {
    if (panoramaView.isShown()) {
      panoramaView.setVisibility(View.INVISIBLE);
    }
    if (closePanoramaBtn.isShown()) {
      closePanoramaBtn.setVisibility(View.INVISIBLE);
    }
  }

  @Override public void showList(List<ParkingBean> parkingBeans) {
    if (parkingBeans != null && !parkingBeans.isEmpty()) {
      Log.e("show", "search gofun park");
      List<ParkingBean> beans = new ArrayList<>();
      ParkingBean parkingBean = parkingBeans.get(0);
      LatLng latLng = new LatLng(parkingBean.getLatitude(), parkingBean.getLongitude());
      beans.add(parkingBean);
      GFVoiceManager.getInstance(getActivity()).doPlayText("为您找到最近的购饭停车场，如需切换请说\"确定\"");
      addMarkers(beans);
      reStartNavi(parkingBean);
      moveCamera(latLng, 0);
    }
  }

  public void destoryNavi() {
    if (aMapNavi != null) {
      aMapNavi.pauseNavi();
      aMapNavi.stopNavi();
      aMapNavi.destroy();
    }
    isNavi = false;
  }

  public ParkingBean getParkingBean() {
    return parkingBean;
  }

  private void addPanoramaMarker(double lng, double lat) {
    Log.e("panorama", "add Marker:" + lng + "==" + lat);
    ImageMarker imageMarker = new ImageMarker();
    Point sp = new Point(parkingBean.getLongitude(), parkingBean.getLatitude());
    Point point = CoordinateConverter.converter(CoordinateConverter.COOR_TYPE.COOR_TYPE_GCJ02, sp);
    imageMarker.setMarkerPosition(point);
    imageMarker.setMarkerHeight(10.3f);
    imageMarker.setMarker(getResources().getDrawable(R.mipmap.icon_map_panorama_end_point, null));
    panoramaView.removeAllMarker();
    panoramaView.addMarker(imageMarker);
  }

  private void setPanoramaView(double lng, double lat) {
    Point sp = new Point(lng, lat);
    Point point = CoordinateConverter.converter(CoordinateConverter.COOR_TYPE.COOR_TYPE_GCJ02, sp);
    panoramaView.setPanorama(point.x, point.y);
  }

  public void reStartNavi(ParkingBean parkingBean) {

    aMapNavi.stopNavi();
    this.parkingBean = parkingBean;
    startNavi();
  }

  public void startNavi() {
    /**
     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
     *
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
     */
    int strategy = 0;
    try {
      //再次强调，最后一个参数为true时代表多路径，否则代表单路径
      strategy = aMapNavi.strategyConvert(false, true, true, false, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 驾车算路
    NaviLatLng endPoint = new NaviLatLng(parkingBean.getLatitude(), parkingBean.getLongitude());
    List<NaviLatLng> endPoints = new ArrayList<>();
    endPoints.add(endPoint);
    aMapNavi.calculateDriveRoute(endPoints, null, strategy);
    isNavi = true;
  }

  public void moveCamera(LatLng latLng, float bearing) {
    if (aMap != null) {
      CameraPosition cameraPosition = new CameraPosition(latLng, 16, 0, bearing);
      aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
  }

  private void addMarkers(List<ParkingBean> parkingBeans) {
    if (getActivity() == null) {
      return;
    }
    ArrayList<MarkerOptions> markerOptions = new ArrayList<>();
    int i = 1;
    for (ParkingBean parkingBean : parkingBeans) {
      MarkerOptions markerOption = new MarkerOptions();
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_map_marker, null);
      ImageView iconImg = (ImageView) view.findViewById(R.id.view_map_marker_img);
      TextView textView = (TextView) view.findViewById(R.id.view_map_marker_text);
      textView.setText(String.valueOf(i));
      typeface(textView);
      if (parkingBean.getType() == ParkingBean.PARK_TYPE_AMAP) {
        iconImg.setImageResource(R.mipmap.icon_map_dot1);
      } else {
        iconImg.setImageResource(R.mipmap.icon_map_dot2);
      }
      markerOption.icon(BitmapDescriptorFactory.fromView(view));
      markerOption.position(new LatLng(parkingBean.getLatitude(), parkingBean.getLongitude()));
      markerOptions.add(markerOption);
      i++;
    }
    aMap.addMarkers(markerOptions, false);
  }

  @Override public void onDescriptionLoadEnd(String s) {

  }

  @Override public void onLoadPanoramaBegin() {

  }

  @Override public void onLoadPanoramaEnd(String s) {

  }

  @Override public void onLoadPanoramaError(String s) {

  }

  @Override public void onMessage(String s, int i) {

  }

  @Override public void onCustomMarkerClick(String s) {

  }

  @Override public void onMoveStart() {

  }

  @Override public void onMoveEnd() {

  }

  @Override public void onInitNaviFailure() {
    Log.e("AMapNaviListener", "onInitNaviFailure");
  }

  @Override public void onInitNaviSuccess() {
    startNavi();
  }

  @Override public void onStartNavi(int i) {

  }

  @Override public void onTrafficStatusUpdate() {
    Log.e("AMapNaviListener", "onTrafficStatusUpdate");
  }

  boolean showMarker = false, hasResult = false;

  @Override public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
    if (showMarker) {
      showMarker = false;
      NaviLatLng naviLatLng = aMapNaviLocation.getCoord();
      setPanoramaView(naviLatLng.getLongitude(), naviLatLng.getLatitude());
      addPanoramaMarker(naviLatLng.getLongitude(), naviLatLng.getLatitude());
    }
  }

  @Override public void onGetNavigationText(String s) {

  }

  @Override public void onEndEmulatorNavi() {
    arriveTime.setText("到达目的地");
    Log.e("parklocation", "park location:" + parkingBean.getLatitude() + "===" + parkingBean.getLongitude());
  }

  @Override public void onArriveDestination() {
    arriveTime.setText("到达目的地");
  }

  @Override public void onArrivedWayPoint(int i) {

  }

  @Override public void onGpsOpenStatus(boolean b) {
    Log.e("navi", "onGpsOpenStatus：" + b);
  }

  @Override public void onNaviInfoUpdate(NaviInfo naviInfo) {
    if (naviInfo != null) {
      nextIcon.setIconType(naviInfo.getIconType());
      nextRoad.setText(naviInfo.getNextRoadName());
      int curStepRetainDis = naviInfo.getCurStepRetainDistance();
      if (curStepRetainDis > 10) {
        nextDis.setText(AMapUtil.getFriendlyLength(curStepRetainDis));
      } else {
        nextDis.setText(nextIcon.setIconTypeStr(naviInfo.getIconType()));
      }
      arriveTime.setText(AMapUtil.getFriendlyNaviInfo(naviInfo, getActivity().getApplicationContext()));
      int i = naviInfo.getPathRetainDistance();
      if (i <= OPEN_PANORAMA_DISTANCE && parkingBean.getType() == ParkingBean.PARK_TYPE_GOFUN) {
        if (!panoramaView.isShown()) {
          panoramaView.setVisibility(View.VISIBLE);
          closePanoramaBtn.setVisibility(View.VISIBLE);
          showMarker = true;
        }
      }
      if (i <= SHOW_GOFUN_PARKING_DISTANCE && parkingBean.getType() != ParkingBean.PARK_TYPE_GOFUN && !hasResult) {
        hasResult = true;
        goFunMapSearchPresenter.setLocation(new LatLng(parkingBean.getLatitude(), parkingBean.getLongitude()));
        ParkingDaoUtils parkingDaoUtils=new ParkingDaoUtils(getActivity());
        List<ParkingBean> parkingBeans = parkingDaoUtils.queryBeansByName("首汽大厦");
        reStartNavi(parkingBeans.get(0));
        Log.e("show", "show gofun park");
      }
    }
  }

  @Override public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
    boolean hasCameraInfo = aMapNaviCameraInfos != null && aMapNaviCameraInfos.length > 0;
    Log.e("AMapNaviListener", "updateCameraInfo：" + hasCameraInfo);
  }

  @Override
  public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {
    Log.e("AMapNaviListener", "updateIntervalCameraInfo");
  }

  @Override public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
    Log.e("AMapNaviListener", "onServiceAreaUpdate");
  }

  @Override public void showCross(AMapNaviCross aMapNaviCross) {
    if (aMapNaviCross != null) {
      Bitmap cross = aMapNaviCross.getBitmap();
      if (crossImg != null) {
        crossImg.setVisibility(View.VISIBLE);
        crossImg.setImageBitmap(cross);
      }
    }
  }

  @Override public void hideCross() {
    crossImg.setVisibility(View.INVISIBLE);
  }

  @Override public void showModeCross(AMapModelCross aMapModelCross) {
    Log.e("AMapNaviListener", "showModeCross");
    if (aMapModelCross != null) {
      if (aMapModelCross.getPicBuf1() != null && aMapModelCross.getPicBuf1().length > 0) {
        Bitmap cross = BitmapFactory.decodeByteArray(aMapModelCross.getPicBuf1(), 0, aMapModelCross.getPicBuf1().length);
        crossImg.setVisibility(View.VISIBLE);
        crossImg.setImageBitmap(cross);
      }
    }
  }

  @Override public void hideModeCross() {
    Log.e("AMapNaviListener", "hideModeCross");
    crossImg.setVisibility(View.INVISIBLE);
  }

  @Override public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {
    Log.e("AMapNaviListener", "showLaneInfo");
  }

  @Override public void hideLaneInfo() {
    Log.e("AMapNaviListener", "hideLaneInfo");
  }

  @Override public void notifyParallelRoad(int i) {

  }

  @Override public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
    Log.e("navi", "OnUpdateTrafficFacility");
  }

  @Override public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

  }

  @Override public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

  }

  @Override public void onPlayRing(int i) {

  }

  @Override public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
    if (aMapNavi != null) {
      aMapNavi.pauseNavi();
      aMapNavi.stopNavi();
      aMapNavi.startNavi(NaviType.EMULATOR);
    }
  }

  @Override public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

  }

  @Override public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {
    Log.e("navi", aMapNaviRouteNotifyData.getRoadName()
        + "=="
        + aMapNaviRouteNotifyData.getReason()
        + "=="
        + aMapNaviRouteNotifyData.getNotifyType());
    if (!TextUtils.isEmpty(aMapNaviRouteNotifyData.getReason())) {
      GFVoiceManager.getInstance(getActivity())
          .doPlayText(aMapNaviRouteNotifyData.getReason() + aMapNaviRouteNotifyData.getDistance() + "米");
    }
  }

  @Override public void onNaviSetting() {

  }

  @Override public void onNaviCancel() {

  }

  @Override public void onNaviMapMode(int i) {

  }

  @Override public void onLockMap(boolean b) {
    Log.e("AMapNaviListener", "onLockMap:" + b);
  }

  @Override public void onNaviViewLoaded() {
    Log.e("AMapNaviListener", "onNaviViewLoaded");
  }

  @Override public void onMapTypeChanged(int i) {
    Log.e("AMapNaviListener", "onMapTypeChanged:" + i);
  }

  @Override public void onNaviViewShowMode(int i) {
    Log.e("AMapNaviListener", "onMapTypeChanged:" + i);
  }

  @Override public void onNaviTurnClick() {

  }

  @Override public void onNextRoadClick() {

  }

  @Override public void onScanViewButtonClick() {

  }

  @Override public boolean onNaviBackClick() {
    return false;
  }

  @Override public void onCalculateRouteSuccess(int[] ints) {

  }

  @Override public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

  }

  @Override public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

  }

  @Override public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

  }

  @Override public void onGetNavigationText(int i, String s) {

  }

  @Override public void onCalculateRouteFailure(int i) {

  }

  @Override public void onReCalculateRouteForYaw() {

  }

  @Override public void onReCalculateRouteForTrafficJam() {

  }

  @Override public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

  }
}

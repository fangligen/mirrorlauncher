package com.shouqiev.mirror.launcher.voice.view;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.gofun.voice.manager.GFVoiceManager;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.activity.MainActivity;
import com.shouqiev.mirror.launcher.base.VoiceBaseFragment;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.fragment.map.AdapterPakingList;
import com.shouqiev.mirror.launcher.fragment.map.OnRecyclerItemClickListener;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingDaoUtils;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceConstant;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceResult;
import com.shouqiev.mirror.launcher.fragment.map.search.AdapterSearchIndex;
import com.shouqiev.mirror.launcher.fragment.map.search.AdapterSearchRecycler;
import com.shouqiev.mirror.launcher.fragment.map.util.VoiceUtil;
import com.shouqiev.mirror.launcher.location.GoFunLocationListener;
import com.shouqiev.mirror.launcher.location.GoFunLocationManager;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

public class VoiceFragmentNavigation extends VoiceBaseFragment
    implements PoiSearch.OnPoiSearchListener, OnRecyclerItemClickListener<String>, GoFunLocationListener {

  @BindView(R.id.fragment_map_view) TextureMapView textureMapView;
  @BindView(R.id.fragment_map_search_viewpager) RecyclerView viewPager;
  @BindView(R.id.fragment_map_search_viewpage_index) RecyclerView viewPagerIndex;
  @BindView(R.id.fragment_map_search_title) TextView title;
  private AMap aMap;
  private PoiSearch poiSearch;

  private AdapterSearchRecycler adapterSearchPager;
  private AdapterSearchIndex adapterSearchIndex;
  String keyWord;
  private AMapLocation myLocation;
  private ParkingDaoUtils parkingDaoUtils;

  private LinearLayoutManager linearLayoutManager;

  private ParkingBean tmpPark;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      keyWord = args.getString("key");
    }
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (textureMapView != null) {
      textureMapView.onCreate(savedInstanceState);
      initMap();

      if (!TextUtils.isEmpty(keyWord)) {
        startSearch(keyWord);
      }
      parkingDaoUtils = new ParkingDaoUtils(getActivity());
      adapterSearchPager = new AdapterSearchRecycler(getActivity());
      linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
      viewPager.setLayoutManager(linearLayoutManager);
      viewPager.setAdapter(adapterSearchPager);
      if (GoFunMirrorApplication.myLocation != null) {
        adapterSearchPager.setMyLocation(myLocation);
      }
      GravityPagerSnapHelper snapHelper = new GravityPagerSnapHelper(Gravity.START, true, new GravitySnapHelper.SnapListener() {
        @Override public void onSnap(int position) {
          Log.e("onSnap", "onSnap:" + position);
          setPage(position);
        }
      });
      snapHelper.attachToRecyclerView(viewPager);
      adapterSearchIndex = new AdapterSearchIndex(getActivity());
      LinearLayoutManager indexLinearLayoutManager =
          new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
      viewPagerIndex.setLayoutManager(indexLinearLayoutManager);
      viewPagerIndex.setAdapter(adapterSearchIndex);
      GravityPagerSnapHelper indexSnapHelper =
          new GravityPagerSnapHelper(Gravity.START, false, new GravitySnapHelper.SnapListener() {
            @Override public void onSnap(int position) {
              Log.e("onSnap", "onSnap:" + position);
            }
          });
      adapterSearchIndex.setOnRecyclerItemClickListener(this);
      indexSnapHelper.attachToRecyclerView(viewPagerIndex);
      GoFunLocationManager.getGoFunLocationManager().addGoFunLocationListener(this);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_map_search, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    if (textureMapView != null) {
      textureMapView.onResume();
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (textureMapView != null) {
      textureMapView.onPause();
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (textureMapView != null) {
      textureMapView.onSaveInstanceState(outState);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (textureMapView != null) {
      textureMapView.onDestroy();
    }
    GoFunLocationManager.getGoFunLocationManager().removeListener(this);
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

  @Override public void onPoiSearched(PoiResult poiResult, int rcode) {
    if (rcode == AMapException.CODE_AMAP_SUCCESS) {
      List<PoiItem> poiItems = poiResult.getPois();
      List<ParkingBean> parkingBeans = new ArrayList<>();
      for (PoiItem poiItem : poiItems) {
        ParkingBean parkingBean = new ParkingBean();
        parkingBean.setLongitude(poiItem.getLatLonPoint().getLongitude());
        parkingBean.setLatitude(poiItem.getLatLonPoint().getLatitude());
        parkingBean.setParkingName(poiItem.getTitle());
        parkingBean.setParkingAddress(poiItem.getSnippet());
        parkingBean.setType(ParkingBean.PARK_TYPE_AMAP);
        parkingBeans.add(parkingBean);
      }
      List<ParkingBean> parkingBeans1 = parkingDaoUtils.queryBeansByName(poiResult.getQuery().getQueryString());
      parkingBeans.addAll(0, parkingBeans1);
      int count = (int) Math.ceil(parkingBeans.size() / 3);
      List<List<ParkingBean>> parks = averageAssign(parkingBeans, 3);
      adapterSearchPager.setParkList(parks);
      List<String> strings = new ArrayList<>();
      for (int i = 0; i < parks.size(); i++) {
        strings.add(String.valueOf(i + 1));
      }
      adapterSearchIndex.setStrings(strings);
      setPage(0);
      GFVoiceManager.getInstance(getActivity()).doPlayText(String.format(VoiceConstant.TIPS_SEARCH_FORMAT, poiItems.size()));
    } else {
      Log.e("rcode", "rcode:" + rcode);
    }
    sendEvent(VoiceEvent.VOICE_STATUS_MAP_SEARCH_COMPLETED);
  }

  @Override public void onPoiItemSearched(PoiItem poiItem, int i) {

  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    if (aMapLocation != null) {
      double latitude = aMapLocation.getLatitude();
      double longitude = aMapLocation.getLongitude();
      LatLng latLng = new LatLng(latitude, longitude);
      adapterSearchPager.setMyLocation(aMapLocation);
    }
  }

  @Override public void onItemClick(String s, int position) {
    setPage(position);
  }

  public void selectItem(int position) {
    if (linearLayoutManager == null) {
      return;
    }
    View view = viewPager.getChildAt(0);
    if (view != null) {
      AdapterSearchRecycler.ViewHolderSearch viewHolderSearch =
          (AdapterSearchRecycler.ViewHolderSearch) viewPager.getChildViewHolder(view);
      if (viewHolderSearch != null) {
        AdapterPakingList adapterPakingList = (AdapterPakingList) viewHolderSearch.recyclerView.getAdapter();
        if (adapterPakingList != null) {
          tmpPark = adapterPakingList.getSelectPark(position);
          sendEvent(VoiceEvent.VOICE_STATUS_SELECTED_ITEM_COMPLETED);
        }
      }
    }
  }

  public int getItemCount() {
    if (linearLayoutManager == null) {
      return 0;
    }
    View view = viewPager.getChildAt(0);
    if (view != null) {
      AdapterSearchRecycler.ViewHolderSearch viewHolderSearch =
          (AdapterSearchRecycler.ViewHolderSearch) viewPager.getChildViewHolder(view);
      if (viewHolderSearch != null) {
        AdapterPakingList adapterPakingList = (AdapterPakingList) viewHolderSearch.recyclerView.getAdapter();
        if (adapterPakingList != null) {
          return adapterPakingList.getItemCount();
        }
      }
    }
    return 0;
  }

  public void startNavi() {
    Intent intent = new Intent(getActivity(), MainActivity.class);
    intent.putExtra("park", tmpPark);
    getActivity().startActivity(intent);
    getActivity().finish();
  }

  @OnClick(R.id.fragment_map_search_next_btn) public void nextPage() {
    int curPosition = adapterSearchIndex.getCurPosition();
    int position =
        curPosition + 1 > adapterSearchPager.getItemCount() - 1 ? adapterSearchPager.getItemCount() - 1 : curPosition + 1;
    setPage(position);
    sendEvent(VoiceEvent.VOICE_STATUS_SEARCH_PAGING_COMPLETED);
  }

  @OnClick(R.id.fragment_map_search_pre_btn) public void prePage() {
    int curPosition = adapterSearchIndex.getCurPosition();
    int position = curPosition - 1 < 0 ? 0 : curPosition - 1;
    setPage(position);
    sendEvent(VoiceEvent.VOICE_STATUS_SEARCH_PAGING_COMPLETED);
  }

  public int getCurrentPage() {
    return adapterSearchIndex.getCurPosition();
  }

  public int getPageCount() {
    return adapterSearchIndex.getItemCount();
  }

  public void setPage(int position) {
    if (position >= getPageCount()) {
      return;
    }
    viewPager.scrollToPosition(position);
    viewPagerIndex.scrollToPosition(position);
    adapterSearchIndex.setCurPosition(position);
    addMarkers(adapterSearchPager.parkList.get(position));
  }

  private void initMap() {
    aMap = textureMapView.getMap();
    initMyLocation();
  }

  private void initMyLocation() {
    MyLocationStyle myLocationStyle;
    //初始化定位蓝点样式类
    myLocationStyle = new MyLocationStyle();
    //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
    //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
    myLocationStyle.interval(2000);
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
    aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
  }

  public void startSearch(String keyWord) {
    if (title == null) {
      this.keyWord = keyWord;
      return;
    }
    title.setText("导航到" + keyWord);
    if (poiSearch == null) {
      poiSearch = new PoiSearch(getActivity(), null);
      poiSearch.setOnPoiSearchListener(this);
    }
    PoiSearch.Query query = new PoiSearch.Query(keyWord, "", "010");
    query.setPageSize(50);
    poiSearch.setQuery(query);
    poiSearch.searchPOIAsyn();
  }

  private void addMarkers(List<ParkingBean> parkingBeans) {
    aMap.clear(true);
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
    aMap.addMarkers(markerOptions, true);
  }

  /**
   * 将一个list均分成n个list,主要通过偏移量来实现的
   */
  public static <T> List<List<T>> averageAssign(List<T> resList, int count) {
    if (resList == null || count < 1) {
      return null;
    }
    List<List<T>> ret = new ArrayList<List<T>>();
    int size = resList.size();
    //数据量不足count指定的大小
    if (size <= count) {
      ret.add(resList);
    } else {
      int pre = size / count;
      int last = size % count;
      //前面pre个集合，每个大小都是count个元素
      for (int i = 0; i < pre; i++) {
        List<T> itemList = new ArrayList<T>();
        for (int j = 0; j < count; j++) {
          itemList.add(resList.get(i * count + j));
        }
        ret.add(itemList);
      }
      //last的进行处理
      if (last > 0) {
        List<T> itemList = new ArrayList<T>();
        for (int i = 0; i < last; i++) {
          itemList.add(resList.get(pre * count + i));
        }
        ret.add(itemList);
      }
    }
    return ret;
  }

  public void sendEvent(int status) {
    VoiceEvent voiceEvent = new VoiceEvent();
    voiceEvent.status = status;
    EventBus.getDefault().post(voiceEvent);
  }

  @Override public void clearView() {
  }

  @Override public void refreshView(BaseVoiceMsg viewMsg) {
    VoiceResult voiceResult = JSONObject.parseObject(viewMsg.getVoiceMode().getOriJson(), VoiceResult.class);
    String searchKey = VoiceUtil.getInstance().getSearchKey(voiceResult);
    if (!TextUtils.isEmpty(searchKey)) {
      startSearch(searchKey);
    }
  }

  //  public static final String TEMPLATE_ITEM_NUM = "第{num}个";
  //  public static final String TEMPLATE_LAST_ITEM = "最后一个";
  //  public static final String TEMPLATE_NEXT_PAGE = "下一页";
  //  public static final String TEMPLATE_PRE_PAGE = "上一页";
  //  public static final String TEMPLATE_PAGE_NUM = "第{num}页";
  //  public static final String TEMPLATE_LAST_PAGE = "最后一页";
  @Override public void updateViewBySelect(int num, String type) {
    switch (type) {
      case VoiceConstant.TEMPLATE_ITEM_NUM:
        int position = num - 1;
        if (position < 0 || position > getItemCount() - 1) {
          GFVoiceManager.getInstance(getActivity()).doPlayText(VoiceConstant.NO_SELECT_ITEM);
        } else {
          selectItem(position);
          startNavi();
        }
        break;
      case VoiceConstant.TEMPLATE_LAST_ITEM:
        selectItem(getItemCount() - 1);
        startNavi();
        break;
      case VoiceConstant.TEMPLATE_NEXT_PAGE:
        setMapSearchPage(getCurrentPage() + 1);
        break;
      case VoiceConstant.TEMPLATE_PRE_PAGE:
        setMapSearchPage(getCurrentPage() - 1);
        break;
      case VoiceConstant.TEMPLATE_PAGE_NUM:
        setMapSearchPage(num - 1);
        break;
      case VoiceConstant.TEMPLATE_LAST_PAGE:
        setMapSearchPage(getPageCount() - 1);
        break;
    }
  }

  private void setMapSearchPage(int index) {
    index = index < 0 ? 0 : index;
    index = index > getPageCount() - 1 ? getPageCount() - 1 : index;
    setPage(index);
    GFVoiceManager.getInstance(getActivity()).doPlayText(String.format(VoiceConstant.SELECT_PAGE_FORMAT, (index + 1)));
  }

  @Override public int getViewType() {
    return IGFViewType.VIEW_TYPE_NAVI;
  }
}

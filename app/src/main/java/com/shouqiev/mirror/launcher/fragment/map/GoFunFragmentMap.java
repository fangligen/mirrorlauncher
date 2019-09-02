package com.shouqiev.mirror.launcher.fragment.map;

import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.gofun.voice.IGFVoiceSpeechListener;
import com.gofun.voice.manager.GFVoiceManager;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.GofunViews.VoiceDialog;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingDaoUtils;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceResult;
import com.shouqiev.mirror.launcher.fragment.map.search.GoFunMapSearchContract;
import com.shouqiev.mirror.launcher.fragment.map.search.GoFunSearchPresenter;
import com.shouqiev.mirror.launcher.fragment.map.util.AMapUtil;
import com.shouqiev.mirror.launcher.location.GoFunLocationListener;
import com.shouqiev.mirror.launcher.location.GoFunLocationManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

/**
 * @author gaoqian
 */
public class GoFunFragmentMap extends BaseMapFragment
    implements GoFunLocationListener, OnRecyclerItemClickListener<ParkingBean>, PoiSearch.OnPoiSearchListener,
    AMap.OnMarkerClickListener, GoFunMapSearchContract.IGoFunMapSearchView {

  @BindView(R.id.fragment_map_parking_list) RecyclerView recyclerView;
  @BindView(R.id.fragment_map_search_edit) EditText searchEdit;

  private PoiSearch poiSearch;

  private AdapterPakingList adapterPakingList;
  private ParkingDaoUtils parkingDaoUtils;
  String keyWord;
  boolean isFirst = true;
  VoiceDialog voiceDialog;
  AMapLocation mapLocation;

  GoFunMapSearchContract.IGoFunMapSearchPresenter iGoFunMapSearchPresenter;

  public static GoFunFragmentMap newInstance(String keyWord) {
    Bundle args = new Bundle();
    GoFunFragmentMap fragment = new GoFunFragmentMap();
    args.putString("key", keyWord);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      keyWord = args.getString("key", null);
    }
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (textureMapView != null) {
      initList();
      iGoFunMapSearchPresenter = new GoFunSearchPresenter(this);
      GoFunLocationManager.getGoFunLocationManager().addGoFunLocationListener(this);
      searchEdit.setOnKeyListener(new View.OnKeyListener() {
        @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_ENTER) {
            search();
          }
          return false;
        }
      });
    }
  }

  private void initList() {
    parkingDaoUtils = new ParkingDaoUtils(getActivity());
    adapterPakingList = new AdapterPakingList(getActivity());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapterPakingList);
    if (GoFunMirrorApplication.myLocation != null) {
      adapterPakingList.setMyLocation(
          new LatLng(GoFunMirrorApplication.myLocation.getLatitude(), GoFunMirrorApplication.myLocation.getLongitude()));
    }
    adapterPakingList.setOnRecyclerItemClickListener(this);
    if (TextUtils.isEmpty(keyWord)) {
      List<ParkingBean> parkingBeans = parkingDaoUtils.queryBeansByName("首汽大厦");
      addMarkers(parkingBeans);
      adapterPakingList.setParkingBeans(parkingBeans);
    } else {
      startSearch(keyWord);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map, container, false);
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

  @OnClick(R.id.fragment_map_search_btn) void search() {
    String keyWord = searchEdit.getText().toString();
    if (!TextUtils.isEmpty(keyWord)) {
      // 先隐藏键盘
      AMapUtil.hideKeyboard(getActivity());
      startSearch(keyWord);
    }
  }

  @OnClick(R.id.voice_input_btn) void voiceInput() {
    if (null == voiceDialog) {
      voiceDialog = new VoiceDialog(getActivity());
    }
    GFVoiceManager.getInstance(getActivity()).setIsMute(true);
    GFVoiceManager.getInstance(getActivity()).registerSpeechListener(new IGFVoiceSpeechListener.Stub() {
      @Override public void onBeginOfSpeech() throws RemoteException {
        getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            voiceDialog.showDialog();
          }
        });
      }

      @Override public void onEndOfSpeech() throws RemoteException {
      }

      @Override public void onResult(final String jsonStr) throws RemoteException {
        getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            VoiceResult voiceResult = JSONObject.parseObject(jsonStr, VoiceResult.class);
            if (null != voiceResult) {
              searchEdit.setText(voiceResult.getText());
              searchEdit.setSelection(voiceResult.getText().length());
              search();
            }
            voiceDialog.dismissRecodingDialog();
          }
        });
        GFVoiceManager.getInstance(getActivity()).unRegisterSpeechListener(this);
        GFVoiceManager.getInstance(getActivity()).setIsMute(false);
        GFVoiceManager.getInstance(getActivity()).doWakeup();
      }

      @Override public void onVolumeChanged(final int volume) throws RemoteException {
        Log.e("", "map onVolumeChanged - " + volume);
        getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            if (null != voiceDialog) {
              voiceDialog.updateVoice(volume);
            }
          }
        });
      }

      @Override public void onSelect(int num, String choose) throws RemoteException {

      }

      @Override public void onWakeupWord() throws RemoteException {

      }

      @Override public void onError(int code) throws RemoteException {
        searchEdit.post(new Runnable() {
          @Override public void run() {
            if (null != voiceDialog) {
              voiceDialog.dismissRecodingDialog();
            }
          }
        });
        GFVoiceManager.getInstance(getActivity()).unRegisterSpeechListener(this);
        GFVoiceManager.getInstance(getActivity()).doWakeup();
      }
    });
    GFVoiceManager.getInstance(getActivity()).doRecognition();
  }

  @Override public void onItemClick(ParkingBean parkingBean, int position) {
    if (getActivity().getCurrentFocus() != null) {
      AMapUtil.hideKeyboard(getActivity());
    }
    ((GoFunMapContainerFragment) getParentFragment()).switchFragment(true, parkingBean, null);
  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    if (locationChange(mapLocation, aMapLocation)) {
      mapLocation = aMapLocation;
      LatLng latLng = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
      adapterPakingList.setMyLocation(latLng);
      if (isFirst) {
        moveCamera(latLng, aMapLocation.getBearing());
        isFirst = false;
      }
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
        parkingBean.setType(ParkingBean.PARK_TYPE_AMAP);
        parkingBean.setParkingAddress(poiItem.getSnippet());
        parkingBeans.add(parkingBean);
      }
      List<ParkingBean> parkingBeans1 = parkingDaoUtils.queryBeansByName(poiResult.getQuery().getQueryString());
      parkingBeans.addAll(0, parkingBeans1);
      adapterPakingList.setParkingBeans(parkingBeans);
      addMarkers(parkingBeans);
    }
  }

  @Override public void onPoiItemSearched(PoiItem poiItem, int i) {

  }

  public void startSearch(String keyWord) {
    if (poiSearch == null) {
      poiSearch = new PoiSearch(getActivity(), null);
      poiSearch.setOnPoiSearchListener(this);
    }
    PoiSearch.Query query = new PoiSearch.Query(keyWord, "", "010");
    query.setPageSize(10);
    poiSearch.setQuery(query);
    poiSearch.searchPOIAsyn();
  }

  private void addMarkers(List<ParkingBean> parkingBeans) {
    if (getActivity() == null) {
      return;
    }
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

  @Override public boolean onMarkerClick(Marker marker) {
    return false;
  }

  @Override public void showList(List<ParkingBean> parkingBeans) {
    adapterPakingList.setParkingBeans(parkingBeans);
    addMarkers(parkingBeans);
  }
}
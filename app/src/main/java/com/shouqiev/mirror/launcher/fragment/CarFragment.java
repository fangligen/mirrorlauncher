//package com.shouqiev.mirror.launcher.fragment;
//
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.TextView;
//import cn.iwgang.simplifyspan.SimplifySpanBuild;
//import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
//import com.shouqiev.mirror.launcher.GofunAdapters.CoverFlowAdapter;
//import com.shouqiev.mirror.launcher.GofunAdapters.CoverFlowDetailAdapter;
//import com.shouqiev.mirror.launcher.GofunViews.FeatureCoverFlow;
//import com.shouqiev.mirror.launcher.R;
//import com.shouqiev.mirror.launcher.activity.MainActivity;
//import com.shouqiev.mirror.launcher.base.BaseFragment;
//import com.shouqiev.mirror.launcher.fragment.Car.CarTutorialFragment;
//import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceConstant;
//import com.shouqiev.mirror.launcher.utils.TutorialUtil;
//import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
//import com.shouqiev.mirror.launcher.voice.view.IGFVoiceViewListener;
//import java.util.ArrayList;
//
//import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;
//
///**
// * Created by zhengyonghui on 2018/9/4.
// */
//public class CarFragment extends BaseFragment implements IGFVoiceViewListener {
//  private static final String TAG = "CarFragment";
//  private FeatureCoverFlow mCoverFlow;
//  private CoverFlowAdapter mAdapter;
//  public ArrayList<CoverFlowAdapter.CardEntity> carFragmentData = new ArrayList<>(0);
//  private Context mContext;
//  private TextView carInfo, seat, powerType, totalTutorial;
//  private ImageView carInfoBg;
//  private View view;
//
//  public static CarFragment carFragment;
//  public static Fragment currentFragment;
//  private CarTutorialFragment carTutorialFragment;
//
//  @Override
//  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    view = inflater.inflate(R.layout.fragment_car,null);
//    return view;
//  }
//
//  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//    super.onViewCreated(view, savedInstanceState);
//    initView(view);
//    addListener();
//    setData();
//
//  }
//
//  private void initView(View view) {
//    carFragment = (CarFragment) getFragmentManager().findFragmentByTag(MainActivity.CAR_FRAGMENT_TAG);
//    carInfo = view.findViewById(R.id.car_info);
//    seat = view.findViewById(R.id.seat_info);
//    powerType = view.findViewById(R.id.engine_info);
//    carInfoBg = view.findViewById(R.id.car_info_bg);
//    totalTutorial = view.findViewById(R.id.total_tutorial);
//    mCoverFlow = view.findViewById(R.id.coverflow);
//  }
//
//  private void setData() {
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_2008, "标志 2008", "5座", "燃油"));
//    carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_ariza5e_white, "奇瑞 艾瑞泽 5E", "5座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_cavalier, "雪佛兰 科沃兹", "5座", "燃油"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_e50, "荣威 E50", "4座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_eq, "奇瑞 EQ", "4座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_eq1, "奇瑞 EQ 小蚂蚁", "4座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_ev200, "奇瑞 EV200", "4座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_i3_silver, "宝马 I3", "5座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_iev5, "江淮 IEV5", "4座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_iev6e, "江淮 IEV6E", "4座", "电动"));
//    //carFragmentData.add(new CoverFlowAdapter.CardEntity(R.drawable.gofun_car_type_jetta, "大众 捷达", "5座", "燃油"));
//    mAdapter = new CoverFlowAdapter(getActivity());
//    mAdapter.setData(carFragmentData);
//    mCoverFlow.setAdapter(mAdapter);
//    setTitle(1, carFragmentData.size());
//  }
//
//  private void addListener(){
//    mCoverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
//      @Override public void onScrolledToPosition(int position) {
//        Log.e("position", "position:" + position);
//        carInfo.setText(carFragmentData.get(position).title);
//        seat.setText(carFragmentData.get(position).seat);
//        powerType.setText(carFragmentData.get(position).powerType);
//        carInfoBg.setImageResource(carFragmentData.get(position).imageResId);
//        setTitle(position + 1, carFragmentData.size());
//      }
//
//      @Override public void onScrolling() {
//
//
//      }
//    });
//
//    mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        int albumId = carFragmentData.get(position % carFragmentData.size()).imageResId;
//        switchFragment(MainActivity.TUTORIAL_FRAGMENT_TAG,albumId);
//      }
//    });
//
//  }
//
//  private void setTitle(int position, int totalCount) {
//    String str = "车辆教程";//车辆教程（1/20）
//    SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild();
//    simplifySpanBuild.append(new SpecialTextUnit(str, Color.parseColor("#FFFFFF"), 22));
//    simplifySpanBuild.append(new SpecialTextUnit("(", Color.parseColor("#FFFFFF"), 18));
//    simplifySpanBuild.append(new SpecialTextUnit(String.valueOf(position), Color.parseColor("#2CF785"), 18));
//    simplifySpanBuild.append(new SpecialTextUnit("/" + totalCount + ")", Color.parseColor("#FFFFFF"), 18));
//    totalTutorial.setText(simplifySpanBuild.build());
//    typeface(totalTutorial);
//  }
//
//  @Override public void onHiddenChanged(boolean hidden) {
//    super.onHiddenChanged(hidden);
//  }
//
//  public void switchFragment(String tag,final int carAlbumId) {
//    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//    if (tag.equals(MainActivity.TUTORIAL_FRAGMENT_TAG)) {
//      transaction.hide(carFragment);
//      if (carTutorialFragment == null) {
//        carTutorialFragment = new CarTutorialFragment();
//        transaction.add(R.id.main_content, carTutorialFragment,MainActivity.TUTORIAL_FRAGMENT_TAG);
//      } else {
//        transaction.show(carTutorialFragment);
//      }
//
//      carTutorialFragment.mData.clear();
//      String[] titles = TutorialUtil.getTutorialTitlesWithId(getActivity(),carAlbumId);
//      if (titles!=null) {
//        for (String title:titles) {
//          carTutorialFragment.mData.add(new CoverFlowDetailAdapter.CardEntity(carAlbumId, title,title+".mp4"));
//        }
//      }
//
//      currentFragment = carTutorialFragment;
//    }  else if (tag.equals(MainActivity.CAR_FRAGMENT_TAG)) {
//      if (carTutorialFragment != null) {
//        transaction.hide(carTutorialFragment);
//      }
//      transaction.show(carFragment);
//      currentFragment = carFragment;
//    }
//    transaction.commit();
//  }
//
//  @Override public void clearView() {
//
//  }
//
//  @Override public void refreshView(BaseVoiceMsg viewMsg) {
//
//  }
//
//  @Override public void updateViewBySelect(int num, String type) {
//    switch (type){
//      case VoiceConstant.TEMPLATE_ITEM_NUM:
//
//
//    }
//
//
//
//  }
//
//  @Override public int getViewType() {
//    return 0;
//  }
//}

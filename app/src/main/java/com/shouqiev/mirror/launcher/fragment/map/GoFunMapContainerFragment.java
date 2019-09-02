package com.shouqiev.mirror.launcher.fragment.map;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;

/**
 * @author gaoqian
 */
public class GoFunMapContainerFragment extends BaseFragment {
  boolean navi = false;
  ParkingBean parkingBean;
  String keyWord;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      navi = args.getBoolean("navi");
      parkingBean = (ParkingBean) args.getSerializable("park");
      keyWord = args.getString("key", null);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map_container, container, false);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (navi) {
      switchFragment(navi, parkingBean, null);
    } else {
      switchFragment(navi, null, keyWord);
    }
  }

  public static GoFunMapContainerFragment newInstance(boolean navi, ParkingBean parkingBean) {
    Bundle args = new Bundle();
    GoFunMapContainerFragment fragment = new GoFunMapContainerFragment();
    args.putBoolean("navi", navi);
    args.putSerializable("park", parkingBean);
    fragment.setArguments(args);
    return fragment;
  }

  public static GoFunMapContainerFragment newInstance(boolean navi, String keyWord) {
    Bundle args = new Bundle();
    GoFunMapContainerFragment fragment = new GoFunMapContainerFragment();
    args.putBoolean("navi", navi);
    args.putString("key", keyWord);
    fragment.setArguments(args);
    return fragment;
  }

  public void switchFragment(boolean showNavi, ParkingBean parkingBean, String keyWord) {

    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
    if (showNavi) {
      Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_map_container_lay);
      if (fragment instanceof GoFunFragmentNavi) {
        GoFunFragmentNavi goFunFragmentNavi = (GoFunFragmentNavi) fragment;
        goFunFragmentNavi.destoryNavi();
      }
      fragmentTransaction.replace(R.id.fragment_map_container_lay, GoFunFragmentNavi.newInstance(parkingBean));
    } else {
      fragmentTransaction.replace(R.id.fragment_map_container_lay, GoFunFragmentMap.newInstance(keyWord));
    }
    fragmentTransaction.commitAllowingStateLoss();
  }
}

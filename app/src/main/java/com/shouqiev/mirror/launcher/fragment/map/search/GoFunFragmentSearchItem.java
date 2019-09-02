package com.shouqiev.mirror.launcher.fragment.map.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import com.shouqiev.mirror.launcher.fragment.map.AdapterPakingList;
import com.shouqiev.mirror.launcher.fragment.map.OnRecyclerItemClickListener;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import java.io.Serializable;
import java.util.List;

public class GoFunFragmentSearchItem extends BaseFragment implements OnRecyclerItemClickListener<ParkingBean> {

  @BindView(R.id.fragment_map_search_list) RecyclerView recyclerView;
  private List<ParkingBean> parkingBeans;
  AdapterPakingList adapterPakingList;

  public static GoFunFragmentSearchItem newInstance(List<ParkingBean> parkingBeans) {
    Bundle args = new Bundle();
    GoFunFragmentSearchItem fragment = new GoFunFragmentSearchItem();
    args.putSerializable("list", (Serializable) parkingBeans);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      parkingBeans = (List<ParkingBean>) args.getSerializable("list");
      adapterPakingList = new AdapterPakingList(getActivity());
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.setAdapter(adapterPakingList);
      adapterPakingList.setOnRecyclerItemClickListener(this);
      adapterPakingList.setParkingBeans(parkingBeans);
    }
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map_search_item, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onItemClick(ParkingBean parkingBean, int position) {

  }
}

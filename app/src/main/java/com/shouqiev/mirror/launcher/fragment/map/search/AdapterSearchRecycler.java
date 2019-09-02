package com.shouqiev.mirror.launcher.fragment.map.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.activity.MainActivity;
import com.shouqiev.mirror.launcher.fragment.map.AdapterPakingList;
import com.shouqiev.mirror.launcher.fragment.map.OnRecyclerItemClickListener;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import java.util.ArrayList;
import java.util.List;

public class AdapterSearchRecycler extends RecyclerView.Adapter<AdapterSearchRecycler.ViewHolderSearch> {

  public List<List<ParkingBean>> parkList = new ArrayList<>();

  private AMapLocation myLocation;
  private Activity context;

  public AdapterSearchRecycler(Activity context) {
    this.context = context;
  }

  public void setParkList(List<List<ParkingBean>> parkList) {
    if (parkList != null && !parkList.isEmpty()) {
      int presize = this.parkList.size();
      this.parkList.clear();
      notifyItemRangeRemoved(0, presize);
      this.parkList.addAll(parkList);
      notifyItemRangeInserted(0, this.parkList.size());
    }
  }

  public void setMyLocation(AMapLocation myLocation) {
    this.myLocation = myLocation;
    if (parkList != null) {
      notifyItemChanged(0, parkList.size());
    }
  }

  @Override public ViewHolderSearch onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.fragment_map_search_item, parent, false);
    ViewHolderSearch viewHolderSearch = new ViewHolderSearch(view);
    return viewHolderSearch;
  }

  @Override public void onBindViewHolder(ViewHolderSearch holder, int position) {
    List<ParkingBean> parkingBeans = parkList.get(position);
    AdapterPakingList adapterPakingList = new AdapterPakingList(context);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    holder.recyclerView.setLayoutManager(linearLayoutManager);
    holder.recyclerView.setAdapter(adapterPakingList);
    if (myLocation != null) {
      adapterPakingList.setMyLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
    }
    adapterPakingList.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener<ParkingBean>() {
      @Override public void onItemClick(ParkingBean parkingBean, int position) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("park", parkingBean);
        ((Activity) context).startActivity(intent);
        context.finish();
      }
    });
    adapterPakingList.setParkingBeans(parkingBeans);
  }

  @Override public int getItemCount() {
    return parkList == null ? 0 : parkList.size();
  }

  public class ViewHolderSearch extends RecyclerView.ViewHolder {
    public @BindView(R.id.fragment_map_search_list) RecyclerView recyclerView;

    public ViewHolderSearch(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

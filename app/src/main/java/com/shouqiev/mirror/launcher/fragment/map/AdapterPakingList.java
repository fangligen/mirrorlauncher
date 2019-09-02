package com.shouqiev.mirror.launcher.fragment.map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.fragment.map.util.AMapUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoqian
 */
public class AdapterPakingList extends RecyclerView.Adapter<AdapterPakingList.ParkingViewHolder> {
  private List<ParkingBean> parkingBeans = new ArrayList<>();
  private Context context;
  private LatLng myLocation;
  int selectPosition = -1;

  private OnRecyclerItemClickListener<ParkingBean> onRecyclerItemClickListener;

  public AdapterPakingList(Context context) {
    this.context = context.getApplicationContext();
  }

  public void setParkingBeans(List<ParkingBean> parkingBeans) {
    if (parkingBeans != null && !parkingBeans.isEmpty()) {
      int previousSize = this.parkingBeans.size();
      this.parkingBeans.clear();
      notifyItemRangeRemoved(0, previousSize);
      this.parkingBeans.addAll(parkingBeans);
      notifyItemRangeInserted(0, this.parkingBeans.size());
    }
  }

  public void clearList() {
    if (parkingBeans != null) {
      notifyItemMoved(0, parkingBeans.size());
      parkingBeans.clear();
    }
  }

  public void setSelectPosition(int selectPosition) {
    this.selectPosition = selectPosition;
  }

  public int getSelectPosition() {
    return selectPosition;
  }

  public ParkingBean getSelectPark(int selectPosition) {
    boolean invalid =
        parkingBeans == null || parkingBeans.isEmpty() || selectPosition < 0 || selectPosition > parkingBeans.size() - 1;
    if (invalid) {
      return null;
    }
    return parkingBeans.get(selectPosition);
  }

  public List<ParkingBean> getParkingBeans() {
    return parkingBeans;
  }

  public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<ParkingBean> onRecyclerItemClickListener) {
    this.onRecyclerItemClickListener = onRecyclerItemClickListener;
  }

  public void setMyLocation(LatLng location) {
    if (myLocation != null) {
      int rlat = Double.compare(myLocation.latitude, location.latitude);
      int rlng = Double.compare(myLocation.longitude, location.longitude);
      if (rlat != 0 || rlng != 0) {
        this.myLocation = location;
        notifyItemRangeChanged(0, getItemCount());
      }
    } else {
      this.myLocation = location;
      notifyItemRangeChanged(0, getItemCount());
    }
  }

  @Override public ParkingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View itemView = LayoutInflater.from(context).inflate(R.layout.item_map_parking_list, viewGroup, false);
    ParkingViewHolder parkingViewHolder = new ParkingViewHolder(itemView);
    return parkingViewHolder;
  }

  @Override public void onBindViewHolder(ParkingViewHolder parkingViewHolder, final int position) {
    final ParkingBean parkingBean = parkingBeans.get(position);
    float distance = 0f;
    if (myLocation != null) {
      LatLng disLatlng = new LatLng(parkingBean.getLatitude(), parkingBean.getLongitude());
      distance = AMapUtils.calculateLineDistance(myLocation, disLatlng);
    }
    parkingViewHolder.distanceTxt.setText(AMapUtil.getFriendlyLength((int) distance));
    parkingViewHolder.nameTxt.setText(String.valueOf(position + 1) + "." + parkingBean.getParkingName());
    if (parkingBean.getType() == ParkingBean.PARK_TYPE_AMAP) {
      parkingViewHolder.iconView.setImageResource(R.mipmap.icon_dot1);
    } else {
      parkingViewHolder.iconView.setImageResource(R.mipmap.icon_dot2);
    }
    parkingViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (onRecyclerItemClickListener != null) {
          onRecyclerItemClickListener.onItemClick(parkingBean, position);
        }
      }
    });
  }

  @Override public int getItemCount() {
    return parkingBeans == null ? 0 : parkingBeans.size();
  }

  class ParkingViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_map_parking_icon) public ImageView iconView;
    @BindView(R.id.item_map_parking_distance) public TextView distanceTxt;
    @BindView(R.id.item_map_parking_name) public TextView nameTxt;

    public ParkingViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

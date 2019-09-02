package com.shouqiev.mirror.launcher.fragment.map.search;

import com.amap.api.maps.model.LatLng;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import java.util.List;

public class GoFunMapSearchContract {
  public interface IGoFunMapSearchView {
    void showList(List<ParkingBean> parkingBeans);
  }

  public interface IGoFunMapSearchPresenter {
    void getList();

    void setLocation(LatLng latLng);
  }
}

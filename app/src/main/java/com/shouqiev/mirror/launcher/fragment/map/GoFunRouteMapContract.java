package com.shouqiev.mirror.launcher.fragment.map;

import com.shouqiev.mirror.launcher.fragment.map.entity.OrderInfo;

public class GoFunRouteMapContract {
  interface IRouteMapView {
    void showOrderInfo(OrderInfo orderInfo);
  }

  interface IRouteMapPresenter {
    void refreshOrderInfo();

    void stopRefreshOrderInfo();
  }
}

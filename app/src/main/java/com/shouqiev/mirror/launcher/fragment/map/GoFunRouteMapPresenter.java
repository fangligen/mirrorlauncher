package com.shouqiev.mirror.launcher.fragment.map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.fragment.map.entity.OrderInfo;
import com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant;
import com.shouqiev.mirror.launcher.fragment.map.util.IHttpResponse;
import com.shouqiev.mirror.launcher.fragment.map.util.RequestUtil;
import java.util.HashMap;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author gaoqian
 */
public class GoFunRouteMapPresenter implements GoFunRouteMapContract.IRouteMapPresenter, IHttpResponse {

  static final int MSG_REFRESH_ORDER_ID = 1;
  static final int MSG_REFRESH_DELAY = 30 * 1000;
  private GoFunRouteMapContract.IRouteMapView iRouteMapView;

  public GoFunRouteMapPresenter(GoFunRouteMapContract.IRouteMapView iRouteMapView) {
    this.iRouteMapView = iRouteMapView;
    EventBus.getDefault().register(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void Event(MapEvent event) {
    if (event.status == MapEvent.EVENT_REFRESH_ORDER) {
      handler.sendEmptyMessage(MSG_REFRESH_ORDER_ID);
    }
  }

  @Override public void refreshOrderInfo() {
    Map<String, Object> paras = new HashMap<>();
    paras.put("orderID", GoFunMirrorApplication.getOrderId());
    RequestUtil requestUtil = new RequestUtil();
    requestUtil.requestPost(HttpConstant.ORDER_URL, paras, this);
    Log.e("handler", "refresh order");
  }

  @Override public void stopRefreshOrderInfo() {
    if (handler != null && handler.hasMessages(MSG_REFRESH_ORDER_ID)) {
      handler.removeMessages(MSG_REFRESH_ORDER_ID);
      Log.e("handler", "remove msg");
    }
  }

  @Override public void response(boolean success, String result) {
    if (success) {
      OrderInfo orderInfo = JSONObject.parseObject(result, OrderInfo.class);
      if (orderInfo != null) {
        if (iRouteMapView != null && orderInfo != null) {
          iRouteMapView.showOrderInfo(orderInfo);
        }
      }
    }
    if (!handler.hasMessages(MSG_REFRESH_ORDER_ID)) {
      handler.sendEmptyMessageDelayed(MSG_REFRESH_ORDER_ID, MSG_REFRESH_DELAY);
    }
  }

  Handler handler = new Handler() {
    @Override public void dispatchMessage(Message msg) {
      super.dispatchMessage(msg);
      if (msg.what == MSG_REFRESH_ORDER_ID) {
        refreshOrderInfo();
      }
    }
  };
}

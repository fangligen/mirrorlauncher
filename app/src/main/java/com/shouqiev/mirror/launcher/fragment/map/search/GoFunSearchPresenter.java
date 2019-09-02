package com.shouqiev.mirror.launcher.fragment.map.search;

import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.model.LatLng;
import com.shouqiev.mirror.launcher.GoFunMirrorApplication;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.fragment.map.entity.ParkingInfo;
import com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant;
import com.shouqiev.mirror.launcher.fragment.map.util.IHttpResponse;
import com.shouqiev.mirror.launcher.fragment.map.util.RequestUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaoqian
 */
public class GoFunSearchPresenter implements GoFunMapSearchContract.IGoFunMapSearchPresenter, IHttpResponse {

  static final String TAG = GoFunSearchPresenter.class.getSimpleName();
  static final int SEARCH_RANGE = 5 * 1000;
  private GoFunMapSearchContract.IGoFunMapSearchView iGoFunMapSearchView;

  LatLng latLng;

  public GoFunSearchPresenter(GoFunMapSearchContract.IGoFunMapSearchView iGoFunMapSearchView) {
    this.iGoFunMapSearchView = iGoFunMapSearchView;
  }

  @Override public void getList() {
    if (TextUtils.isEmpty(GoFunMirrorApplication.getOrderId())) {
      return;
    }
    Map<String, Object> pars = new HashMap<>();
    pars.put("orderID", GoFunMirrorApplication.getOrderId());
    pars.put("longitude", String.valueOf(latLng.longitude));
    pars.put("latitude", String.valueOf(latLng.latitude));
    pars.put("range", String.valueOf(SEARCH_RANGE));
    RequestUtil requestUtil = new RequestUtil();
    requestUtil.requestPost(HttpConstant.PARKING_QUERY_URL, pars, this);
  }

  @Override public void setLocation(LatLng latLng) {
    this.latLng = latLng;
    getList();
  }

  @Override public void response(boolean success, String result) {
    if (!success) {
      return;
    }
    JSONObject jsonObject = JSON.parseObject(result);
    if (jsonObject.containsKey("pageInfo")) {
      JSONObject pageInfoJson = jsonObject.getJSONObject("pageInfo");
      if (pageInfoJson.containsKey("list")) {
        String t = pageInfoJson.getString("list");
        List<ParkingInfo> parkingInfos = JSONArray.parseArray(t, ParkingInfo.class);
        List<ParkingBean> parkingBeans = new ArrayList<>();
        for (ParkingInfo parkingInfo : parkingInfos) {
          ParkingBean parkingBean = new ParkingBean();
          parkingBean.convertParkInfo(parkingInfo);
          parkingBeans.add(parkingBean);
        }
        parkingInfos.clear();
        iGoFunMapSearchView.showList(parkingBeans);
        Log.e(TAG, "size is:" + parkingBeans.size());
      }
    }
  }
}

package com.shouqi.mirror.gofunlocationservice.dao;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @author gaoqian
 */
public class LocationDaoUtils {
  private GoFunDaoManager goFunDaoManager;
  private Context context;

  public LocationDaoUtils(Context context) {
    this.context = context.getApplicationContext();
    goFunDaoManager = GoFunDaoManager.getGoFunDaoManager();
    goFunDaoManager.init(context);
    init();
  }

  private void init() {
    List<LocationBean> locationBeans = queryBeansByPages(0, 10);
    if (locationBeans == null || locationBeans.isEmpty()) {
      readDataFromJson();
    }
  }

  private void readDataFromJson() {
    try {
      InputStream inputStream = context.getAssets().open("map_parking_data.json");
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      String json = new String(buffer, "UTF-8");
      JSONObject jsonObject = JSON.parseObject(json);
      String text = jsonObject.getString("RECORDS");
      List<LocationBean> locationBeans = JSON.parseArray(text, LocationBean.class);
      insertList(locationBeans);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean insert(LocationBean locationBean) {
    try {
      return goFunDaoManager.getDaoSession().getLocationBeanDao().insert(locationBean) == -1 ? false : true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean insertList(final List<LocationBean> locationBeans) {
    try {
      goFunDaoManager.getDaoSession().runInTx(new Runnable() {
        @Override public void run() {
          for (LocationBean locationBean : locationBeans) {
            goFunDaoManager.getDaoSession().insertOrReplace(locationBean);
          }
        }
      });
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void update(LocationBean locationBean) {
    goFunDaoManager.getDaoSession().update(locationBean);
  }

  public void delete(LocationBean locationBean) {
    goFunDaoManager.getDaoSession().delete(locationBean);
  }

  public void deleteByOrderId(String orderId) {
    QueryBuilder<LocationBean> queryBuilder = goFunDaoManager.getDaoSession().getLocationBeanDao().queryBuilder();
    queryBuilder.where(LocationBeanDao.Properties.OrderId.eq(orderId)).buildDelete().executeDeleteWithoutDetachingEntities();
  }

  public List<LocationBean> queryBeans() {
    return goFunDaoManager.getDaoSession().loadAll(LocationBean.class);
  }

  public List<LocationBean> queryBeansByPages(int page, int limit) {
    QueryBuilder<LocationBean> parkingBeanQueryBuilder = goFunDaoManager.getDaoSession().queryBuilder(LocationBean.class);
    return parkingBeanQueryBuilder.offset(page).limit(limit).list();
  }
}

package com.shouqiev.mirror.launcher.fragment.map.dao;

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
public class ParkingDaoUtils {
  private GoFunDaoManager goFunDaoManager;
  private Context context;

  public ParkingDaoUtils(Context context) {
    this.context = context.getApplicationContext();
    goFunDaoManager = GoFunDaoManager.getGoFunDaoManager();
    goFunDaoManager.init(context);
    init();
  }

  private void init() {
    List<ParkingBean> parkingBeans = queryBeansByPages(0, 10);
    if (parkingBeans == null || parkingBeans.isEmpty()) {
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
      List<ParkingBean> parkingBeans = JSON.parseArray(text, ParkingBean.class);
      insertList(parkingBeans);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean insert(ParkingBean parkingBean) {
    try {
      return goFunDaoManager.getDaoSession().getParkingBeanDao().insert(parkingBean) == -1 ? false : true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean insertList(final List<ParkingBean> parkingBeans) {
    try {
      goFunDaoManager.getDaoSession().runInTx(new Runnable() {
        @Override public void run() {
          for (ParkingBean parkingBean : parkingBeans) {
            goFunDaoManager.getDaoSession().insertOrReplace(parkingBean);
          }
        }
      });
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void update(ParkingBean parkingBean) {
    goFunDaoManager.getDaoSession().update(parkingBean);
  }

  public void delete(ParkingBean parkingBean) {
    goFunDaoManager.getDaoSession().delete(parkingBean);
  }

  public List<ParkingBean> queryBeans() {
    return goFunDaoManager.getDaoSession().loadAll(ParkingBean.class);
  }

  public List<ParkingBean> queryBeansByName(String name) {
    //ParkingBeanDao.Properties.ParkingName.like("%" + name + "%")
    QueryBuilder<ParkingBean> queryBuilder = goFunDaoManager.getDaoSession().queryBuilder(ParkingBean.class);
    return queryBuilder.whereOr(ParkingBeanDao.Properties.ParkingAddress.like("%" + name + "%"),
        ParkingBeanDao.Properties.ParkingName.like("%" + name + "%")).list();
  }

  public List<ParkingBean> queryBeansByPages(int page, int limit) {
    QueryBuilder<ParkingBean> parkingBeanQueryBuilder = goFunDaoManager.getDaoSession().queryBuilder(ParkingBean.class);
    return parkingBeanQueryBuilder.offset(page).limit(limit).list();
  }
}

package com.shouqiev.mirror.launcher.fragment.map.dao;

import android.content.Context;
import android.os.Environment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shouqiev.mirror.launcher.model.EnvironmentDetail;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @author gaoqian
 */
public class EnvironmentDaoUtils {
  private GoFunDaoManager goFunDaoManager;
  private Context context;

  public EnvironmentDaoUtils(Context context) {
    this.context = context.getApplicationContext();
    goFunDaoManager = GoFunDaoManager.getGoFunDaoManager();
    goFunDaoManager.init(context);
    init();
  }

  private void init() {
    List<EnvironmentDetail>environmentDetails=queryBeans();
    if(environmentDetails==null||environmentDetails.isEmpty()){
      EnvironmentDetail environmentDetail=new EnvironmentDetail();
      environmentDetail.setImgPath(Environment.getExternalStorageDirectory()+"/env/env_detail1.jpg");
      environmentDetail.setEnvLevel(0.56f);
      environmentDetail.setImgTime("2018-08-10");
      insert(environmentDetail);

      EnvironmentDetail environmentDetail1=new EnvironmentDetail();
      environmentDetail1.setImgPath(Environment.getExternalStorageDirectory()+"/env/env_detail2.jpg");
      environmentDetail1.setEnvLevel(0.85f);
      environmentDetail1.setImgTime("2018-08-11");
      insert(environmentDetail1);
    }
  }

  public boolean insert(EnvironmentDetail parkingBean) {
    try {
      return goFunDaoManager.getDaoSession().getEnvironmentDetailDao().insertOrReplace(parkingBean) == -1 ? false : true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean insertList(final List<EnvironmentDetail> parkingBeans) {
    try {
      goFunDaoManager.getDaoSession().runInTx(new Runnable() {
        @Override public void run() {
          for (EnvironmentDetail parkingBean : parkingBeans) {
            goFunDaoManager.getDaoSession().insertOrReplace(parkingBean);
          }
        }
      });
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void update(EnvironmentDetail parkingBean) {
    goFunDaoManager.getDaoSession().update(parkingBean);
  }

  public void delete(EnvironmentDetail parkingBean) {
    goFunDaoManager.getDaoSession().delete(parkingBean);
  }

  public List<EnvironmentDetail> queryBeans() {
    return goFunDaoManager.getDaoSession().loadAll(EnvironmentDetail.class);
  }

  public List<EnvironmentDetail> queryBeansByName(String name) {
    //ParkingBeanDao.Properties.ParkingName.like("%" + name + "%")
    QueryBuilder<EnvironmentDetail> queryBuilder = goFunDaoManager.getDaoSession().queryBuilder(EnvironmentDetail.class);
    return queryBuilder.whereOr(ParkingBeanDao.Properties.ParkingAddress.like("%" + name + "%"),
        ParkingBeanDao.Properties.ParkingName.like("%" + name + "%")).list();
  }

  public List<EnvironmentDetail> queryBeansByPages(int page, int limit) {
    QueryBuilder<EnvironmentDetail> parkingBeanQueryBuilder = goFunDaoManager.getDaoSession().queryBuilder(EnvironmentDetail.class);
    return parkingBeanQueryBuilder.offset(page).limit(limit).list();
  }
}

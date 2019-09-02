package com.shouqi.mirror.gofunlocationservice.dao;

import android.content.Context;

/**
 * @author gaoqian
 */
public class GoFunDaoManager {
  private static final String DB_NAME = "gofun_os_location_db";
  private Context context;
  private volatile static GoFunDaoManager goFunDaoManager = new GoFunDaoManager();
  private static DaoMaster daoMaster;
  private static DaoSession daoSession;
  private static DaoMaster.DevOpenHelper devOpenHelper;

  public static GoFunDaoManager getGoFunDaoManager() {
    return goFunDaoManager;
  }

  public void init(Context context) {
    this.context = context.getApplicationContext();
  }

  /**
   * database object
   */
  public DaoMaster getDaoMaster() {
    if (daoMaster == null) {
      devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
      daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
    }
    return daoMaster;
  }

  public DaoSession getDaoSession() {
    if (daoSession == null) {
      if (daoMaster == null) {
        daoMaster = getDaoMaster();
      }
      daoSession = daoMaster.newSession();
    }
    return daoSession;
  }

  public void closeConnection() {
    closeHelper();
    closeSession();
  }

  public void closeHelper() {
    if (devOpenHelper != null) {
      devOpenHelper.close();
      devOpenHelper = null;
    }
  }

  public void closeSession() {
    if (daoSession != null) {
      daoSession.clear();
      daoSession = null;
    }
  }
}

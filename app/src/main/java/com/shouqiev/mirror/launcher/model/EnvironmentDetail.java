package com.shouqiev.mirror.launcher.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EnvironmentDetail {
  public String getImgPath() {
    return imgPath;
  }

  public void setImgPath(String imgPath) {
    this.imgPath = imgPath;
  }

  public float getEnvLevel() {
    return envLevel;
  }

  public void setEnvLevel(float envLevel) {
    this.envLevel = envLevel;
  }

  public String getImgTime() {
    return imgTime;
  }

  public void setImgTime(String imgTime) {
    this.imgTime = imgTime;
  }

  @Id private String imgPath;
  private float envLevel;
  private String imgTime;
  @Generated(hash = 1996411278)
  public EnvironmentDetail(String imgPath, float envLevel, String imgTime) {
      this.imgPath = imgPath;
      this.envLevel = envLevel;
      this.imgTime = imgTime;
  }

  @Generated(hash = 1761492112)
  public EnvironmentDetail() {
  }
}

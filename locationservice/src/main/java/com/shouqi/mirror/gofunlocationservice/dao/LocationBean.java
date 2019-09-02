package com.shouqi.mirror.gofunlocationservice.dao;

import android.location.Location;
import java.io.Serializable;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author gaoqian
 */
@Entity public class LocationBean implements Serializable {
  private static final long serialVersionUID = -376982431978395816L;

  private String orderId;
  private double Longitude;
  private double Latitude;
  private long timeStamp;

  @Generated(hash = 1986785755) public LocationBean(String orderId, double Longitude, double Latitude, long timeStamp) {
    this.orderId = orderId;
    this.Longitude = Longitude;
    this.Latitude = Latitude;
    this.timeStamp = timeStamp;
  }

  @Generated(hash = 516751439) public LocationBean() {
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setLatitude(double latitude) {
    Latitude = latitude;
  }

  public double getLatitude() {
    return Latitude;
  }

  public void setLongitude(double longitude) {
    Longitude = longitude;
  }

  public double getLongitude() {
    return Longitude;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public static LocationBean paseLocation(Location location, String orderId) {
    LocationBean locationBean = new LocationBean();
    locationBean.setLatitude(location.getLatitude());
    locationBean.setLongitude(location.getLongitude());
    locationBean.setTimeStamp(System.currentTimeMillis());
    locationBean.setOrderId(orderId);
    return locationBean;
  }
}


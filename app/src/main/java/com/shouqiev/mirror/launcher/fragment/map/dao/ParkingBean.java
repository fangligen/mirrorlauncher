package com.shouqiev.mirror.launcher.fragment.map.dao;

import com.shouqiev.mirror.launcher.fragment.map.entity.ParkingInfo;
import java.io.Serializable;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author gaoqian
 */
@Entity public class ParkingBean implements Serializable {
  private static final long serialVersionUID = -376982431978395816L;

  public static final int PARK_TYPE_GOFUN = 1;
  public static final int PARK_TYPE_AMAP = 2;

  @Id private String parkingid;
  private String ParkingName;
  private String ParkingAddress;
  private double Longitude;
  private double Latitude;
  private int type = PARK_TYPE_GOFUN;

  @Generated(hash = 1333770942)
  public ParkingBean(String parkingid, String ParkingName, String ParkingAddress, double Longitude, double Latitude, int type) {
    this.parkingid = parkingid;
    this.ParkingName = ParkingName;
    this.ParkingAddress = ParkingAddress;
    this.Longitude = Longitude;
    this.Latitude = Latitude;
    this.type = type;
  }

  @Generated(hash = 382982177) public ParkingBean() {
  }

  public String getParkingid() {
    return this.parkingid;
  }

  public void setParkingid(String parkingid) {
    this.parkingid = parkingid;
  }

  public String getParkingName() {
    return this.ParkingName;
  }

  public void setParkingName(String ParkingName) {
    this.ParkingName = ParkingName;
  }

  public String getParkingAddress() {
    return this.ParkingAddress;
  }

  public void setParkingAddress(String ParkingAddress) {
    this.ParkingAddress = ParkingAddress;
  }

  public double getLongitude() {
    return this.Longitude;
  }

  public void setLongitude(double Longitude) {
    this.Longitude = Longitude;
  }

  public double getLatitude() {
    return this.Latitude;
  }

  public void setLatitude(double Latitude) {
    this.Latitude = Latitude;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void convertParkInfo(ParkingInfo parkingInfo) {
    this.Latitude = parkingInfo.getLatitude();
    this.Longitude = parkingInfo.getLongitude();
    this.ParkingAddress = parkingInfo.getName();
    this.ParkingName = parkingInfo.getName();
    this.parkingid = parkingInfo.getParkingId();
  }
}

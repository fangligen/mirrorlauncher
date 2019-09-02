package com.shouqiev.mirror.launcher.fragment.map.entity;

public class ParkingInfo {

  private String parkingId;
  private String name;
  private double latitude;
  private double longitude;
  private String image;

  public void setParkingId(String parkingId) {
    this.parkingId = parkingId;
  }

  public String getParkingId() {
    return parkingId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getImage() {
    return image;
  }
}

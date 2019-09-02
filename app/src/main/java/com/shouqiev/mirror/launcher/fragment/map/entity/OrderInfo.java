package com.shouqiev.mirror.launcher.fragment.map.entity;

public class OrderInfo {
  private String orderId;
  private long startTime;
  private int duration;
  private int distance;
  private ParkingInfo takeParking;
  private ParkingInfo returnParking;

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getDuration() {
    return duration;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public int getDistance() {
    return distance;
  }

  public void setTakeParking(ParkingInfo takeParking) {
    this.takeParking = takeParking;
  }

  public ParkingInfo getTakeParking() {
    return takeParking;
  }

  public void setReturnParking(ParkingInfo returnParking) {
    this.returnParking = returnParking;
  }

  public ParkingInfo getReturnParking() {
    return returnParking;
  }
}

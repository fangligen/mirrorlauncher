package com.shouqiev.mirror.launcher.fragment.map.entity;

public class LoginInfo {
  private String token;
  private String orderId;

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getToken() {
    return token;
  }
}

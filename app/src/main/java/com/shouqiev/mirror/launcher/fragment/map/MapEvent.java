package com.shouqiev.mirror.launcher.fragment.map;

public class MapEvent {
  public static final int EVENT_REFRESH_ORDER = 1;
  public int status;

  public MapEvent() {

  }

  public MapEvent(int status) {
    this.status = status;
  }
}

package com.shouqiev.mirror.launcher.receiver;

import java.util.Observable;

/**
 *
 * @author gaoqian
 * @date 2017/7/11
 */
public class AlarmObservable extends Observable {

  private volatile static AlarmObservable mInstance = null;

  private AlarmObservable() {

  }

  public static AlarmObservable getInstance() {
    if (mInstance == null) {
      synchronized (AlarmObservable.class) {
        if (mInstance == null) {
          mInstance = new AlarmObservable();
        }
      }
    }
    return mInstance;
  }

  @Override public void notifyObservers() {
    setChanged();
    super.notifyObservers();
  }

  @Override public void notifyObservers(Object data) {
    setChanged();
    super.notifyObservers(data);
  }
}

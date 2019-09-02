package com.shouqiev.mirror.launcher.event;

public class CameraEvent {
    public String path, errorCode;

    public CameraEvent(String path, String errorCode) {
        this.path = path;
        this.errorCode = errorCode;
    }
}

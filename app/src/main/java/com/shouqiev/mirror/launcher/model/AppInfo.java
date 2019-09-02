package com.shouqiev.mirror.launcher.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;
/**
 * app info
 */

public class AppInfo {

    private String appName;
    private Drawable icon;
    /**
     * launcher this app
     **/
    private Intent intent;
    private String packageName;
    private boolean isSystem;
    private String activityName;

    public AppInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AppInfo){
            return ((AppInfo)obj).getPackageName().equals(getPackageName());
        }
        return super.equals(obj);
    }
}
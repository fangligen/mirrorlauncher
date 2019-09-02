package com.shouqiev.mirror.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.shouqiev.mirror.launcher.fragment.map.entity.FileInfo;
import com.shouqiev.mirror.launcher.fragment.map.util.FileUploadUtils;
import com.shouqiev.mirror.launcher.fragment.map.util.HttpConstant;
import com.shouqiev.mirror.launcher.fragment.map.util.IHttpResponse;
import com.shouqiev.mirror.launcher.fragment.map.util.RequestUtil;
import com.shouqiev.mirror.launcher.utils.PreferencesUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoqian
 */
public class LogUploadService extends Service {

  static final String TAG = LogUploadService.class.getSimpleName();
  static final String LOG_LOCAL_PATH = "";
  boolean uploadFlag = true;

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (uploadFlag) {
      uploadLogFile();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private void uploadLogFile() {
    String[] logFiles = getLogFiles(LOG_LOCAL_PATH);
    if (logFiles == null || logFiles.length < 1) {
      uploadFlag = true;
      return;
    }
    uploadFlag = false;
    final String logFile = logFiles[0];
    FileUploadUtils fileUploadUtils = new FileUploadUtils();
    fileUploadUtils.uploadFile(logFile, FileUploadUtils.FILE_TYPE_TXT, new FileUploadUtils.OnResponseListener() {
      @Override public void onResponse(String desc, FileInfo fileInfo) {
        Log.e(TAG, fileInfo.getFilePath());
        upload2Service(fileInfo.getFilePath(), logFile);
      }
    });
  }

  private void upload2Service(String path, final String localPath) {
    String orderId = PreferencesUtils.getString(getApplicationContext(), LoginService.LOGIN_PREFERENCE_NAME,
        LoginService.LOGIN_PERFERENCE_KEY_ORDER_ID);
    RequestUtil requestUtil = new RequestUtil();
    Map<String, Object> map = new HashMap<>();
    map.put("orderId", orderId);
    map.put("filePath", path);
    requestUtil.requestPost(HttpConstant.LOG_UPLOAD_URL, map, new IHttpResponse() {
      @Override public void response(boolean success, String result) {
        if (success) {
          File file = new File(localPath);
          if (file.exists()) {
            file.delete();
          }
          uploadLogFile();
        }
      }
    });
  }

  private String[] getLogFiles(String path) {
    File file = new File(path);
    if (!file.exists() || !file.isDirectory()) {
      return null;
    }
    String[] fileList = file.list();
    if (fileList == null || fileList.length < 2) {
      return null;
    }
    return fileList;
  }
}

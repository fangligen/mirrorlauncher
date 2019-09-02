package com.shouqiev.mirror.launcher.fragment.map.util;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.shouqiev.mirror.launcher.fragment.map.entity.FileInfo;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUploadUtils implements IHttpResponse {

  /**
   * text", "image", "audio", "video
   */
  public static final String FILE_TYPE_IMAGE = "image/*";
  public static final String FILE_TYPE_TXT = "text/*";
  public static final String FILE_TYPE_AUDIO = "audio/*";
  public static final String FILE_TYPE_VIDEO = "video/*";

  OnResponseListener listener;

  public void setListener(OnResponseListener listener) {
    this.listener = listener;
  }

  public void uploadFile(String path, String fileType, OnResponseListener listener) {
    this.listener = listener;
    uploadFile(path, fileType);
  }

  public void uploadFile(String path, String fileType) {
    if (TextUtils.isEmpty(path)) {
      return;
    }
    File file = new File(path);
    if (!file.exists()) {
      return;
    }
    Map<String, Object> map = new HashMap<>();
    map.put("sim", "18611343388");
    RequestUtil requestUtil = new RequestUtil(this);
    requestUtil.uploadFile(file, fileType, map);
  }

  @Override public void response(boolean success, String result) {
    if (success) {
      FileInfo fileInfo = JSONObject.parseObject(result, FileInfo.class);
      if (listener != null) {
        listener.onResponse(null, fileInfo);
      }
    } else {
      if (listener != null) {
        listener.onResponse(result, null);
      }
    }
  }

  public interface OnResponseListener {
    /**
     * 回调接口
     */
    void onResponse(String desc, FileInfo fileInfo);
  }
}

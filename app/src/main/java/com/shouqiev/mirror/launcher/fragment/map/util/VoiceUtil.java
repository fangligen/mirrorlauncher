package com.shouqiev.mirror.launcher.fragment.map.util;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.gofun.voice.model.VoiceMode;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceConstant;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceResult;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceSemantic;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceSlots;

public class VoiceUtil {

  private static volatile VoiceUtil instance;

  private VoiceUtil() {

  }

  public static VoiceUtil getInstance() {
    if (instance == null) {
      synchronized (VoiceUtil.class) {
        if (instance == null) {
          instance = new VoiceUtil();
        }
      }
    }
    return instance;
  }

  public boolean isMapService(VoiceMode voiceMode) {
    if (voiceMode == null) {
      return false;
    } else if (VoiceConstant.SERVICE_MAP.equals(voiceMode.getService())) {
      return true;
    } else if (!TextUtils.isEmpty(voiceMode.getQuestion()) && VoiceConstant.SERVICE_MAP_START_NAVI.equals(
        voiceMode.getQuestion())) {
      return true;
    } else {
      return moreResulthasMapService(voiceMode);
    }
  }

  public boolean moreResulthasMapService(VoiceMode voiceMode) {
    VoiceResult voiceResult = JSONObject.parseObject(voiceMode.getOriJson(), VoiceResult.class);
    if (voiceResult.getMoreResults() == null || voiceResult.getMoreResults().isEmpty()) {
      return false;
    }
    boolean hasMapService = false;
    for (VoiceResult item : voiceResult.getMoreResults()) {
      hasMapService = VoiceConstant.SERVICE_MAP.equals(item.getService()) || (!TextUtils.isEmpty(voiceMode.getQuestion())
          && VoiceConstant.SERVICE_MAP_START_NAVI.equals(voiceMode.getQuestion()));
      if (hasMapService) {
        break;
      }
    }
    return hasMapService;
  }

  public boolean isStartService(VoiceMode voiceMode) {
    if (voiceMode == null) {
      return false;
    } else if (!TextUtils.isEmpty(voiceMode.getOriJson())) {
      VoiceResult voiceResult = JSONObject.parseObject(voiceMode.getOriJson(), VoiceResult.class);
      boolean valid =
          voiceResult != null && !TextUtils.isEmpty(voiceResult.getText()) && VoiceConstant.SERVICE_MAP_START_NAVI.equals(
              voiceResult.getText());
      return valid;
    } else {
      return false;
    }
  }

  public boolean isWakeUpService(VoiceMode voiceMode) {
    if (voiceMode == null || TextUtils.isEmpty(voiceMode.getOriJson())) {
      return false;
    }
    VoiceResult voiceResult = JSONObject.parseObject(voiceMode.getOriJson(), VoiceResult.class);
    boolean vaild = voiceResult.getVoiceSemantic() != null
        && !voiceResult.getVoiceSemantic().isEmpty()
        && VoiceConstant.SERVICE_CUSTOM.equals(voiceResult.getService());
    if (vaild) {
      VoiceSemantic voiceSemantic = voiceResult.getVoiceSemantic().get(0);
     boolean tvaild = voiceSemantic != null
          && VoiceConstant.INTENT_CMD.equals(voiceSemantic.getIntent())
          && voiceSemantic.getSlots() != null
          && !voiceSemantic.getSlots().isEmpty();
      if (tvaild) {
        VoiceSlots voiceSlots = voiceSemantic.getSlots().get(0);
        if (voiceSlots != null && VoiceConstant.SLOTS_NAME_WAKE.equals(voiceSlots.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  public String getIntent(VoiceResult voiceResult) {
    if (resultSemanticInvalid(voiceResult)) {
      return null;
    }
    VoiceSemantic voiceSemantic = voiceResult.getVoiceSemantic().get(0);
    return voiceSemantic.getIntent();
  }

  public boolean resultSemanticInvalid(VoiceResult voiceResult) {
    boolean invalid = voiceResult == null || voiceResult.getVoiceSemantic() == null || voiceResult.getVoiceSemantic().isEmpty();
    return invalid;
  }

  public String getSearchKey(VoiceResult voiceResult) {
    boolean invalid = resultSemanticInvalid(voiceResult);
    if (invalid) {
      return null;
    }
    VoiceSemantic voiceSemantic = voiceResult.getVoiceSemantic().get(0);
    if (voiceSemantic.getSlots() == null || voiceSemantic.getSlots().isEmpty()) {
      return null;
    }
    String searchKey = null;
    for (VoiceSlots voiceSlots : voiceSemantic.getSlots()) {
      if (VoiceConstant.SLOTS_END_LOC.equals(voiceSlots.getName())) {
        searchKey = voiceSlots.getNormValue();
        break;
      }
    }
    return searchKey;
  }
}

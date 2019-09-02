package com.shouqiev.mirror.launcher.voice.precess;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gofun.voice.manager.GFVoiceManager;
import com.gofun.voice.model.VoiceMode;
import com.gofun.voice.utils.GFOperateType;
import com.gofun.voice.utils.GFServiceType;
import com.gofun.voice.utils.TipsUtil;
import com.shouqiev.mirror.launcher.voice.manager.VoicePrecessManager;
import com.shouqiev.mirror.launcher.voice.manager.VoiceVolumeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class VoiceCmdPrecess implements IGFDialogPrecess{

  private Context mContext;
  private String name;
  private String value;


  @Override
  public boolean doPrecess(Context context, VoiceMode voiceMode) {
    mContext = context;
    Log.e("voice", "operator:" + voiceMode.getIntent());
    VoicePrecessManager.getInstance().clearStatus();
    if(voiceMode.isEmpty()){
      return false;
    }
    initCmdData(voiceMode.getSlots());
    switch (voiceMode.getIntent()){
      case GFOperateType.instruction:
        return doInstruction();
      case GFOperateType.set:
        return doSet();
    }

    return true;
  }
  @Override
  public void clearStatus() {
  }
  @Override
  public String getServiceType() {
    return GFServiceType.cmd;
  }

  private void initCmdData(String slots){
    try{
      JSONObject slot = new JSONArray(slots).getJSONObject(0);
      name = slot.getString("name");
      value = slot.getString("value");
    }catch (JSONException e){
    }

  }


  private boolean doInstruction(){
    switch (value){
      case "volume_plus":
        if(VoiceVolumeManager.getInstance(mContext).getCurrentVolume()==VoiceVolumeManager.getInstance(mContext).getMaxVolume()){
          GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_MAX);
        }else{
          GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_VALUE+(VoiceVolumeManager.getInstance(mContext).getCurrentVolume()-1));
          VoiceVolumeManager.getInstance(mContext).setVolumeUp();
        }
        break;
      case "volume_minus":
        if(VoiceVolumeManager.getInstance(mContext).getCurrentVolume()==1){
          GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_MIN);
        }else{
          GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_VALUE+(VoiceVolumeManager.getInstance(mContext).getCurrentVolume()+1));
          VoiceVolumeManager.getInstance(mContext).setVolumeDowm();
        }
        break;
      case "mute":
        VoiceVolumeManager.getInstance(mContext).setMuteVolume(true);
        break;
      case "unmute":
        VoiceVolumeManager.getInstance(mContext).setMuteVolume(false);
        GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_UNMUTE);
        break;
      case "volume_max":
        VoiceVolumeManager.getInstance(mContext).setMaxVolume();
        GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_MAX);
        break;
      case "volume_min":
        VoiceVolumeManager.getInstance(mContext).setMinVolume();
        GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_MIN);
        break;

    }
    return true;
  }

  private boolean doSet(){
    if("series".equals(name)){
      VoiceVolumeManager.getInstance(mContext).setVolumeToValue(convertVolume(value));
      GFVoiceManager.getInstance(mContext).doPlayText(TipsUtil.TTS_VOLUME_VALUE+value);
      return true;
    }else{
      return false;
    }
  }

  private int convertVolume(String nameValue) {
    if (TextUtils.isEmpty(nameValue)) {
      return -1;
    }
    switch (nameValue) {
      case "零":
        return 0;
      case "一":
        return 1;
      case "二":
        return 2;
      case "三":
        return 3;
      case "四":
        return 4;
      case "五":
        return 5;
      case "六":
        return 6;
      case "七":
        return 7;
      case "八":
        return 8;
      case "九":
        return 9;
      default:
        try {
          return Integer.parseInt(nameValue);
        } catch (Exception e) {
          return -1;
        }
    }
  }

}

package com.shouqiev.mirror.launcher.voice.model;
import com.gofun.voice.model.VoiceMode;
public class BaseVoiceMsg {

  private int ViewType;
  private VoiceMode voiceMode;

  public BaseVoiceMsg(VoiceMode voiceMode,int ViewType){
    this.ViewType = ViewType;
    this.voiceMode = voiceMode;
  }

  public BaseVoiceMsg(){}

  public int getViewType() {
    return ViewType;
  }
  public void setViewType(int viewType) {
    ViewType = viewType;
  }
  public VoiceMode getVoiceMode() {
    return voiceMode;

  }
  public void setVoiceMode(VoiceMode voiceMode) {
    this.voiceMode = voiceMode;
  }
}

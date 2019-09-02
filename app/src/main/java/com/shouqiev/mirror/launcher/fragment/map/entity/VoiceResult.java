package com.shouqiev.mirror.launcher.fragment.map.entity;

import com.alibaba.fastjson.JSONArray;
import java.util.List;

public class VoiceResult {
  /*data
  rc : 0
  voiceSemantic
  service : "mapU"
  uuid : "cida15f8eb7@dx002c0f3475b601002a"
  text : "导航到t3"
  state
      used_state
  answer
  dialog_stat : "DataValid"
  save_history : true
  sid : "cida15f8eb7@dx002c0f3475b601002a"*/

  private String data;
  private int rc;
  private String semantic;
  private List<VoiceSemantic> voiceSemantic;
  private String service;
  private String uuid;
  private String text;
  private String state;
  private String used_state;
  private String answer;
  private List<VoiceResult> moreResults;

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public void setData(String data) {
    this.data = data;
  }

  public void setRc(int rc) {
    this.rc = rc;
  }

  public void setSemantic(String semantic) {
    this.semantic = semantic;
    voiceSemantic = JSONArray.parseArray(semantic, VoiceSemantic.class);
  }

  public void setService(String service) {
    this.service = service;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setUsed_state(String used_state) {
    this.used_state = used_state;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public int getRc() {
    return rc;
  }

  public String getAnswer() {
    return answer;
  }

  public String getData() {
    return data;
  }

  public List<VoiceSemantic> getVoiceSemantic() {
    return voiceSemantic;
  }

  public String getService() {
    return service;
  }

  public String getState() {
    return state;
  }

  public String getText() {
    return text;
  }

  public String getUsed_state() {
    return used_state;
  }

  public String getUuid() {
    return uuid;
  }

  public void setMoreResults(List<VoiceResult> moreResults) {
    this.moreResults = moreResults;
  }

  public List<VoiceResult> getMoreResults() {
    return moreResults;
  }
}

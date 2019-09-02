package com.gofun.voice.model;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class VoiceMode {

  private String question;
  private String answer;
  private String service;
  private String intent;
  private String template;
  private String slots;
  private String oriJson;
  private String name;
  private String normValue;
  private String value;

  public boolean isEmpty() {
    return TextUtils.isEmpty(question);
  }

  public VoiceMode(){}

  public VoiceMode(String jsonStr) {
    try {
      JSONObject data = new JSONObject(jsonStr);
      if (data != null && data.has("text")) {
        question = data.getString("text");
        service = data.getString("service");
      }
      if (data.has("answer")) {
        JSONObject answer = data.getJSONObject("answer");
        if (answer != null && answer.has("text")) {
          this.answer = answer.getString("text");
        }
      }
      if (data.has("semantic")) {
        JSONObject semantic = data.getJSONArray("semantic").getJSONObject(0);
        if (semantic.has("intent")) {
          intent = semantic.getString("intent");
        }
        if(semantic.has("template")) {
          template = semantic.getString("template");
        }
        if (semantic.has("slots")) {
          slots = semantic.getString("slots");
        }
        if(!TextUtils.isEmpty(slots) && slots.length()>4){
          JSONObject slotsFirst = semantic.getJSONArray("slots").getJSONObject(0);
          if(slotsFirst.has("name")){
            name = slotsFirst.getString("name");
          }
          if(slotsFirst.has("normValue")){
            normValue = slotsFirst.getString("normValue");
          }
          if(slotsFirst.has("value")){
            value = slotsFirst.getString("value");
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      this.oriJson = jsonStr;
    }
  }

  public String getQuestion() {
    return TextUtils.isEmpty(question)?"":question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswer() {
    return TextUtils.isEmpty(answer)?"": answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getIntent() {
    return intent;
  }

  public void setIntent(String intent) {
    this.intent = intent;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getSlots() {
    return slots;
  }

  public void setSlots(String slots) {
    this.slots = slots;
  }

  public String getOriJson() {
    return oriJson;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getNormValue() {
    return normValue;
  }
  public void setNormValue(String normValue) {
    this.normValue = normValue;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
}

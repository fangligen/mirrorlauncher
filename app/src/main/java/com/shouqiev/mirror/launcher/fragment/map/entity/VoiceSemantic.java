package com.shouqiev.mirror.launcher.fragment.map.entity;

import java.util.List;

public class VoiceSemantic {
 /* "intent": "QUERY",
      "slots":*/

  private String intent;
  private List<VoiceSlots> slots;
  private String template;

  public void setIntent(String intent) {
    this.intent = intent;
  }

  public String getIntent() {
    return intent;
  }

  public List<VoiceSlots> getSlots() {
    return slots;
  }

  public void setSlots(List<VoiceSlots> slots) {
    this.slots = slots;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }
}

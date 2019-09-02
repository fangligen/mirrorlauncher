package com.shouqiev.mirror.launcher.voice.view;

import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
public interface IGFVoiceViewListener {
    public void clearView();
    public void refreshView(BaseVoiceMsg viewMsg);
    public void updateViewBySelect(int num,String type);
    public int getViewType();

}

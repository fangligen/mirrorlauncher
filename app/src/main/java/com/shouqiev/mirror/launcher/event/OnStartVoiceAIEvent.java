package com.shouqiev.mirror.launcher.event;

/**
 * MainActivity点击直接进入VoiceActivity页面，直接开启语音识别
 */
public class OnStartVoiceAIEvent {
    public boolean isStart;

    public OnStartVoiceAIEvent(boolean isStart) {
        this.isStart = isStart;
    }
}

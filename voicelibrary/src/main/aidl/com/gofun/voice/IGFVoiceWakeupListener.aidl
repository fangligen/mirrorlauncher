// IGFVoiceWakeUpListener.aidl
package com.gofun.voice;

// Declare any non-default types here with import statements

interface IGFVoiceWakeupListener {
    void onBeginOfSpeech();
    void onWakeup(String sst,String id,String score);
    void onVolumeChanged(int volume);
    void onError(int code);
}

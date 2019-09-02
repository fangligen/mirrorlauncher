// IGFVoiceSpeechListener.aidl
package com.gofun.voice;

// Declare any non-default types here with import statements

interface IGFVoiceSpeechListener {
    void onBeginOfSpeech();
    void onEndOfSpeech();
    void onResult(String jsonStr);
    //声音
    void onVolumeChanged(int volume);
    //选择第几页，支持1～6，最后一页，取消
    void onSelect(int num,String choose);
    void onWakeupWord();
    //-1 onResult==null -2 service_error
    void onError(int code);
}

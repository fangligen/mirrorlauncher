// IGFVoicePlayListener.aidl
package com.gofun.voice;

// Declare any non-default types here with import statements

interface IGFVoicePlayListener {
    void onPlayStart(String text);
    void onPlayCompleted(String text);
    void onProgressReturn(String text,int progress, int beginPos, int endPos);
    void onError(int code);
}

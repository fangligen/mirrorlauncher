// IGFVoiceService.aidl
package com.gofun.voice;
import com.gofun.voice.IGFVoicePlayListener;
import com.gofun.voice.IGFVoiceWakeupListener;
import com.gofun.voice.IGFVoiceSpeechListener;
// Declare any non-default types here with import statements

interface IGFVoiceService {
    void doPlayText(String text,in IGFVoicePlayListener playListener);
    void doWakeup(in IGFVoiceWakeupListener wakeupListener);
    void doRecognition(in IGFVoiceSpeechListener speechListener);
    boolean isTTSPlaying();
    boolean isInited();
    boolean isRecognition();
    void stopTTs();
    void stopRecognition();
}

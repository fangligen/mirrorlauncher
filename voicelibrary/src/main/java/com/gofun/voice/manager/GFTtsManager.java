package com.gofun.voice.manager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
public class GFTtsManager implements Handler.Callback,InitListener{


    private static GFTtsManager mInstance = null;
    private static SpeechSynthesizer mTts;
    private String currentTXT;
    private Handler mHandler;
    private Context mContext;
    private List<SynthesizerListener> playListeners;

    private AtomicBoolean ttsInited = new AtomicBoolean(false);


    public static GFTtsManager getInstance(){
        if(mInstance == null){
            mInstance = new GFTtsManager();
        }
        return mInstance;
    }

    public GFTtsManager init(Context context){
        playListeners = new ArrayList<>();
        mHandler = new Handler(this);
        mContext = context;
        mTts = SpeechSynthesizer.createSynthesizer(mContext, this);
        if (null != mTts) {
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi"); // 设置发音人
            mTts.setParameter(SpeechConstant.SPEED, "60");// 设置语速
            mTts.setParameter(SpeechConstant.PITCH, "50");// 设置语调
            mTts.setParameter(SpeechConstant.VOLUME, "100");// 设置音量，范围 0~100
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
            //设置合成音频保存位置（可自定义保存位置），保存在 “./sdcard/iflytek.pcm”
            //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
            //仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
            mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        }


        return this;
    }

    public void registerListener(SynthesizerListener playListener){
        if(!playListeners.contains(playListener)){
            this.playListeners.add(playListener);
        }
    }

    public void unregisterListener(SynthesizerListener playListener){
        if(playListeners.contains(playListener)){
            playListeners.remove(playListener);
        }
    }

    public boolean isReady(){
        return ttsInited.get();
    }

    public boolean isSpeaking(){
        return mTts!=null && mTts.isSpeaking();
    }


    public void speakTXT(String txt){
        if(isReady()){
            currentTXT = txt;
            mTts.stopSpeaking();
            mTts.startSpeaking(txt,synthesizerListener);
        }
    }


    public void speakTXT(String txt,SynthesizerListener playListener){
        if(isReady()){
            currentTXT = txt;
            stopSpeak();
            mTts.startSpeaking(txt,synthesizerListener);
        }
    }

    public void stopSpeak(){
        if(mTts.isSpeaking()){
            mTts.stopSpeaking();
        }
    }

    public String getCurrentTXT(){
        return currentTXT;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    private SynthesizerListener synthesizerListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onSpeakBegin();
                    }
                }
            });
        }
        @Override
        public void onBufferProgress(final int i, final int i1, final int i2, final String s) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onBufferProgress(i,i1,i2,s);
                    }
                }
            });
        }
        @Override
        public void onSpeakPaused() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onSpeakPaused();
                    }
                }
            });
        }
        @Override
        public void onSpeakResumed() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onSpeakResumed();
                    }
                }
            });
        }
        @Override
        public void onSpeakProgress(final int progress, final int beginPos, final int endPos) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onSpeakProgress(progress,beginPos,endPos);
                    }
                }
            });
        }
        @Override
        public void onCompleted(final SpeechError speechError) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onCompleted(speechError);
                    }
                }
            });
        }
        @Override
        public void onEvent(final int i, final int i1, final int i2, final Bundle bundle) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SynthesizerListener listener:playListeners){
                        listener.onEvent(i,i1,i2,bundle);
                    }
                }
            });
        }
    };

    @Override
    public void onInit(int i) {
        ttsInited.set(i==0);
    }
}

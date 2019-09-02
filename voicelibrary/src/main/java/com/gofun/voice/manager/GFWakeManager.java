package com.gofun.voice.manager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gofun.voice.utils.ConfigUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
public class GFWakeManager implements Handler.Callback{

    private final String TAG="GFWakeManager";
    private List<WakeuperListener> wakeupListeners;
    private Handler mHandler = null;

    private VoiceWakeuper mIvw;
    private int curThresh = 1450;

    private String keep_alive = "1";
    private String ivwNetMode = "1";

    private Context mContext;
    private AtomicBoolean wakeupInited = new AtomicBoolean(false);

    private static GFWakeManager mInstance = null;
    public static GFWakeManager getInstance(){
        if(mInstance == null){
            mInstance = new GFWakeManager();
        }
        return mInstance;
    }

    public void registerListener(WakeuperListener wakeupListener){
        if(!wakeupListeners.contains(wakeupListener)){
            this.wakeupListeners.add(wakeupListener);
        }
    }

    public void unregisterListener(WakeuperListener wakeupListener){
        if(wakeupListeners.contains(wakeupListener)){
            wakeupListeners.remove(wakeupListener);
        }
    }

    public boolean isReady(){
        return wakeupInited.get();
    }

    public GFWakeManager init(Context context){
        mHandler = new Handler(this);
        wakeupListeners = new ArrayList<>();
        mContext = context;

        mIvw = VoiceWakeuper.getWakeuper();
        if(mIvw == null){
            mIvw = VoiceWakeuper.createWakeuper(mContext, null);
        }
        if(mIvw == null){
            wakeupInited.set(false);
            return this;
        }
        wakeupInited.set(true);
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, "");
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets,
                "ivw/" + ConfigUtil.APP_ID + ".jet"));
        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
        mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
        //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
        Log.d(TAG,"init()   ");
        startWakeup();
        return this;
    }

    public void startWakeup(){
        if(wakeupInited.get()){
            mIvw.startListening(wakeuperListener);
        }
    }

    public void stopWakeup(){
        if(wakeupInited.get()){
            mIvw.stopListening();
            mIvw.cancel();

        }
    }

    public void destroy(){
        if(wakeupInited.get()){
            mIvw.destroy();
            wakeupInited.set(false);
        }
    }




    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case 1:
                for (WakeuperListener listener:wakeupListeners){
                    listener.onVolumeChanged(msg.arg1);
                }
                break;
        }
        return false;
    }

    private WakeuperListener wakeuperListener = new WakeuperListener(){

        @Override
        public void onBeginOfSpeech() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"onBeginOfSpeech()");
                    for (WakeuperListener listener:wakeupListeners){
                        listener.onBeginOfSpeech();
                    }
                }
            });
        }
        @Override
        public void onResult(final WakeuperResult wakeuperResult) {
            Log.d(TAG,"onResult() : " + wakeuperResult.getResultString());
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    for (WakeuperListener listener:wakeupListeners){
                        listener.onResult(wakeuperResult);
                    }
                }
            });
        }
        @Override
        public void onError(final SpeechError speechError) {
            Log.d(TAG,"onError() = "+ speechError.getErrorCode() + " , "+ speechError.getErrorDescription());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (WakeuperListener listener:wakeupListeners){
                        listener.onError(speechError);
                    }
                }
            });
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
        @Override
        public void onVolumeChanged(int volume) {
            Message msg = new Message();
            msg.what=1;
            msg.arg1 = volume;
            mHandler.sendMessage(msg);
        }

    };



}

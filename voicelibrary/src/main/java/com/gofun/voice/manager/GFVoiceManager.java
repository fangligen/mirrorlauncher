package com.gofun.voice.manager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.gofun.voice.IGFVoicePlayListener;
import com.gofun.voice.IGFVoiceSpeechListener;
import com.gofun.voice.IGFVoiceWakeupListener;
import com.gofun.voice.utils.AIUIEventUtil;
import com.gofun.voice.utils.ConfigUtil;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
public class GFVoiceManager{

    private Context mContext;
    private static GFVoiceManager mInstance;
    private final String TAG = "GFVoiceManager";


    private GFTtsManager mGFTtsManager = null;
    private GFWakeManager mGFWakeManager = null;
    private GFAIUIManager mGFAIUIManager = null;
    private GFMediaManager  mGFMediaManager = null;

    private List<IGFVoiceWakeupListener> wakeupListeners;
    private List<IGFVoicePlayListener> playListeners;
    private List<IGFVoiceSpeechListener> speechListeners;

    private AtomicInteger voiceStatus = new AtomicInteger(ConfigUtil.VOICE_STATUS_UNINIT);
    private AtomicBoolean hasSpeech = new AtomicBoolean(true);
    private AtomicBoolean isMute = new AtomicBoolean(false);





    public GFVoiceManager(Context context){
        mContext = context;
    }

    public static GFVoiceManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new GFVoiceManager(context);
        }
        return mInstance;
    }

    public void initIflytek(){
        StringBuffer param = new StringBuffer();
        param.append(SpeechConstant.APPID + "=" +  ConfigUtil.APP_ID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(mContext, param.toString());
        Setting.setLogLevel(Setting.LOG_LEVEL.none);
    }


    public void init(){
        if(mGFTtsManager==null || mGFWakeManager == null
                || mGFAIUIManager == null  || mGFWakeManager == null ){

            mGFTtsManager = GFTtsManager.getInstance().init(mContext);
            mGFAIUIManager = GFAIUIManager.getInstance().init(mContext);
            mGFWakeManager = GFWakeManager.getInstance().init(mContext);
            mGFMediaManager = GFMediaManager.getInstance().initMedia(mContext);

            mGFTtsManager.registerListener(ttsListener);
            mGFWakeManager.registerListener(wakeuperListener);
            mGFAIUIManager.registerListener(aiuiListener);

            wakeupListeners = new ArrayList<>();
            playListeners = new ArrayList<>();
            speechListeners = new ArrayList<>();
        }
    }
    public boolean isInited() {
        if(mGFTtsManager == null || mGFWakeManager == null || mGFAIUIManager == null){
            return false;
        }
        return mGFTtsManager.isReady() && mGFAIUIManager.isReady() && mGFWakeManager.isReady();
    }

    public void doPlayText(String text)  {
        switchVoiceStatus(ConfigUtil.VOICE_STATUS_TTS);
        this.hasSpeech.set(true);
        mGFTtsManager.speakTXT(text);
    }
    public void doPlayText(String text,boolean hasSpeech)  {
        switchVoiceStatus(ConfigUtil.VOICE_STATUS_TTS);
        this.hasSpeech.set(hasSpeech);
        mGFTtsManager.speakTXT(text);
    }
    public void doWakeup() {
        switchVoiceStatus(ConfigUtil.VOICE_STATUS_WAKEUP);
        mGFWakeManager.startWakeup();
    }
    public void doRecognition()  {
        mGFAIUIManager.stopVoiceSR();
        switchVoiceStatus(ConfigUtil.VOICE_STATUS_SR);
        mGFAIUIManager.startVoiceSr();
    }
    public AtomicBoolean getIsMute() {
        return isMute;
    }
    public void setIsMute(boolean isMute) {
        this.isMute.set(isMute);
    }
    public boolean isTTSPlaying()  {
        return mGFTtsManager.isSpeaking();
    }
    public boolean isRecognition()  {
        return mGFAIUIManager.isRecognition();
    }
    public void stopTTs()  {
        mGFTtsManager.stopSpeak();
    }
    public void stopRecognition()  {
        mGFAIUIManager.stopVoiceSR();
    }

    public void switchVoiceStatus(int status){
        switch (status){
            case ConfigUtil.VOICE_STATUS_WAKEUP:
                mGFAIUIManager.stopVoiceSR();
                voiceStatus.set(status);
                break;
            case ConfigUtil.VOICE_STATUS_TTS:
                mGFAIUIManager.stopVoiceSR();
                voiceStatus.set(status);
                break;
            case ConfigUtil.VOICE_STATUS_SR:
                mGFTtsManager.stopSpeak();
                mGFWakeManager.stopWakeup();
                voiceStatus.set(status);
                break;

        }
    }


    public void registerWakeupListener(IGFVoiceWakeupListener listener){
        if(!wakeupListeners.contains(listener)){
            wakeupListeners.add(listener);
        }
    }
    public void registerSpeechListener(IGFVoiceSpeechListener listener){
        if(!speechListeners.contains(listener)){
            speechListeners.add(listener);
        }
    }
    public void registerTTSListener(IGFVoicePlayListener listener){
        if(!playListeners.contains(listener)){
            playListeners.add(listener);
        }
    }
    public void unRegisterWakeupListener(IGFVoiceWakeupListener listener){
        if(wakeupListeners.contains(listener)){
            wakeupListeners.remove(listener);
        }
    }
    public void unRegisterSpeechListener(IGFVoiceSpeechListener listener){
        if(speechListeners.contains(listener)){
            speechListeners.remove(listener);
        }
    }
    public void unRegisterTTSListener(IGFVoicePlayListener listener){
        if(playListeners.contains(listener)){
            playListeners.remove(listener);
        }
    }

    private AIUIListener aiuiListener = new AIUIListener(){
        @Override
        public void onEvent(AIUIEvent event) {

        switch (event.eventType) {
            case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                Log.d(TAG,"已连接服务器 eventType="+ event.eventType);
                break;

            case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                Log.d(TAG,"与服务器断连 eventType="+ event.eventType);
                break;

            case AIUIConstant.EVENT_WAKEUP:
                mGFMediaManager.playMedia(mediaListener);
                Log.d(TAG, "进入识别状态 eventType="+ event.eventType );
                break;
            case AIUIConstant.EVENT_RESULT:
                String result = AIUIEventUtil.processResult(event);
                Log.e(TAG,"识别结果 : "+ result);
                if(!isEmptyWord(result)) {
                    JSONObject selectJosn = AIUIEventUtil.getSelectJson(result);
                    for (IGFVoiceSpeechListener listener : speechListeners) {
                        try {
                            if(selectJosn!=null){
                                try {
                                    listener.onSelect(selectJosn.getInt("num"),selectJosn.getString("type"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            listener.onResult(result);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case AIUIConstant.EVENT_ERROR:
                Log.e(TAG,"错误 : "+event.info);
                for (IGFVoiceSpeechListener listener : speechListeners) {
                    try {
                        listener.onError(-2);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            break;

            case AIUIConstant.EVENT_VAD:
                break;

            case AIUIConstant.EVENT_START_RECORD:
                Log.d(TAG, "开始录音  eventType="+ event.eventType );
                for (IGFVoiceSpeechListener listener:speechListeners){
                    try {
                        listener.onBeginOfSpeech();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case AIUIConstant.EVENT_STOP_RECORD:
                Log.d(TAG, "停止录音  eventType="+ event.eventType );
                for (IGFVoiceSpeechListener listener:speechListeners){
                    try {
                        listener.onEndOfSpeech();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case AIUIConstant.EVENT_STATE: 	// 状态事件

                break;
            case AIUIConstant.EVENT_CMD_RETURN:
                Log.e(TAG,"cmd : "+ event.info);
                break;

            default:
                break;
        }
        }
    };

    private boolean isEmptyWord(String str){
        return TextUtils.isEmpty(str) || "null".equals(str) || "{}".equals(str) || "none".equals(str);
    }

    private SynthesizerListener ttsListener = new SynthesizerListener(){
        @Override
        public void onSpeakBegin() {
            for (IGFVoicePlayListener listener:playListeners) {
                try {
                    listener.onPlayStart(mGFTtsManager.getCurrentTXT());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if(!ConfigUtil.hasWakeupWord(mGFTtsManager.getCurrentTXT())){
                doWakeup();
            }
        }
        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }
        @Override
        public void onSpeakPaused() {

        }
        @Override
        public void onSpeakResumed() {

        }
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            for (IGFVoicePlayListener listener:playListeners) {
                try {
                    listener.onProgressReturn(mGFTtsManager.getCurrentTXT(),i,i1,i2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onCompleted(SpeechError speechError) {
            for (IGFVoicePlayListener listener:playListeners) {
                try {
                    listener.onPlayCompleted(mGFTtsManager.getCurrentTXT());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if(hasSpeech.get()){
                hasSpeech.set(false);
                doRecognition();
            }else if(ConfigUtil.hasWakeupWord(mGFTtsManager.getCurrentTXT())){
                doWakeup();
            }
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private WakeuperListener wakeuperListener = new WakeuperListener() {
        @Override
        public void onBeginOfSpeech() {
            voiceStatus.set(ConfigUtil.VOICE_STATUS_WAKEUP);
        }
        @Override
        public void onResult(WakeuperResult wakeuperResult) {
            for (IGFVoiceWakeupListener listener:wakeupListeners) {
                try {
                    listener.onWakeup("","","");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onError(SpeechError speechError) {
            for (IGFVoiceWakeupListener listener:wakeupListeners) {
                try {
                    listener.onError(speechError.getErrorCode());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
        @Override
        public void onVolumeChanged(int i) {
            for (IGFVoiceWakeupListener listener:wakeupListeners) {
                try {
                    listener.onVolumeChanged(i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private MediaPlayer.OnCompletionListener mediaListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mp) {
        }
    };

    private int cutAndInt(String str){
        try {
            if(TextUtils.isEmpty(str)){
                return 0;
            }else if(str.startsWith("page")){
                return Integer.parseInt(str.substring(5));
            }else if(str.startsWith("num")){
                return Integer.parseInt(str.substring(4));
            }else{
                return 0;
            }
        }catch (Exception E){
            return 0;
        }

    }
    private String cutStr(String str){
        if(TextUtils.isEmpty(str)){
            return "";
        }else if(str.startsWith("UorD")){
            return str.substring(5);
        }else if(str.startsWith("sure")){
            return str.substring(5);
        }else{
            return "";
        }
    }
}

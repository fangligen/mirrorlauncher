package com.gofun.voice.manager;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class GFAIUIManager implements Handler.Callback,AIUIListener {

    private static GFAIUIManager mInstance = null;
    public static GFAIUIManager getInstance(){
        if(mInstance == null){
            mInstance = new GFAIUIManager();
        }
        return mInstance;
    }

    private final String TAG = "GFAIUIManager";
    private AIUIAgent mAIUIAgent = null;
    private AtomicInteger mAIUIState = new AtomicInteger(AIUIConstant.STATE_IDLE);
    private Context mContext = null;
    private Handler mHandler = null;
    private List<AIUIListener> speechListeners;

    public GFAIUIManager init(Context context){
        mContext = context;
        speechListeners = new ArrayList<>();
        mHandler = new Handler(this);
        mAIUIAgent = AIUIAgent.createAgent(mContext, getAIUIParams(), this);
        return this;
    }


    public void registerListener(AIUIListener speechListener){
        if(!speechListeners.contains(speechListener)){
            this.speechListeners.add(speechListener);
        }
    }

    public void unregisterListener(AIUIListener speechListener){
        if(speechListeners.contains(speechListener)){
            speechListeners.remove(speechListener);
        }
    }


    //开始识别
    public void startVoiceSr(){
        if( AIUIConstant.STATE_WORKING !=this.mAIUIState.get() ){
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0,null, null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }
        // 打开AIUI内部录音机，开始录音。若要使用上传的个性化资源增强识别效果，则在参数中添加pers_param设置
        // 个性化资源使用方法可参见http://doc.xfyun.cn/aiui_mobile/的用户个性化章节
        // 在输入参数中设置tag，则对应结果中也将携带该tag，可用于关联输入输出
        String params = "sample_rate=16000,data_type=audio,pers_param={\"uid\":\"\"},tag=audio-tag";
        AIUIMessage startRecord = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(startRecord);
    }

    //取消识别
    public void stopVoiceSR(){
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage stopRecord = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(stopRecord);
    }

    //销毁实例
    public void destroyAIUI(){
        if (null != mAIUIAgent) {
            mAIUIAgent.destroy();
            mAIUIState.set(AIUIConstant.STATE_IDLE);
            mAIUIAgent = null;
        }
    }

    //上传自定义词，如通讯录，应用列表等
    public void syncContacts(String json) {
        if( AIUIConstant.STATE_WORKING !=this.mAIUIState.get() ){
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0,"", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }
        AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_UPLOAD_LEXICON,0, 0, json, null);
        mAIUIAgent.sendMessage(msg);
    }


    public boolean isReady(){
        return mAIUIState.get() != AIUIConstant.STATE_IDLE;
    }

    public boolean isRecognition(){
        return mAIUIState.get() == AIUIConstant.STATE_WORKING;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
    @Override
    public void onEvent(final AIUIEvent event) {
        Log.i( TAG,  "on event: " + event.eventType );
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (AIUIListener listener:speechListeners){
                    listener.onEvent(event);
                }
            }
        });

    }

    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = mContext.getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }
}

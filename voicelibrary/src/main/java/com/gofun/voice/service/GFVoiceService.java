package com.gofun.voice.service;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.gofun.voice.IGFVoicePlayListener;
import com.gofun.voice.IGFVoiceService;
import com.gofun.voice.IGFVoiceSpeechListener;
import com.gofun.voice.IGFVoiceWakeupListener;
import com.gofun.voice.manager.GFVoiceManager;

import java.util.List;
public class GFVoiceService extends Service {

    private final static String TAG = "GFVoiceService";

    private GFVoiceManager mVoiceManager;

    private List<IGFVoiceWakeupListener> wakeupListeners;
    private List<IGFVoicePlayListener> playListeners;
    private List<IGFVoiceSpeechListener> speechListeners;

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initService();
        return super.onStartCommand(intent, flags, startId);
    }
    public void initService(){
        if(mVoiceManager == null){
            mVoiceManager = GFVoiceManager.getInstance(GFVoiceService.this);
        }
        if(!mVoiceManager.isInited()){
            mVoiceManager.init();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        initService();
        return iGFvoiceService;
    }

    private IGFVoiceService.Stub iGFvoiceService = new IGFVoiceService.Stub() {
        @Override
        public void doPlayText(String text, IGFVoicePlayListener playListener) throws RemoteException {
            mVoiceManager.registerTTSListener(playListener);
            mVoiceManager.doPlayText(text);
        }
        @Override
        public void doWakeup(IGFVoiceWakeupListener wakeupListener) throws RemoteException {
            mVoiceManager.registerWakeupListener(wakeupListener);
            mVoiceManager.doWakeup();
        }
        @Override
        public void doRecognition(IGFVoiceSpeechListener speechListener) throws RemoteException {
            mVoiceManager.registerSpeechListener(speechListener);
            mVoiceManager.doRecognition();
        }
        @Override
        public boolean isTTSPlaying() throws RemoteException {
            return mVoiceManager.isTTSPlaying();
        }
        @Override
        public boolean isInited() throws RemoteException {
            return mVoiceManager.isInited();
        }
        @Override
        public boolean isRecognition() throws RemoteException {
            return mVoiceManager.isRecognition();
        }
        @Override
        public void stopTTs() throws RemoteException {
            mVoiceManager.stopTTs();
        }
        @Override
        public void stopRecognition() throws RemoteException {
            mVoiceManager.stopRecognition();
        }
    } ;
}

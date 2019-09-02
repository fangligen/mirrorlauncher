package com.shouqiev.mirror.launcher;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gofun.voice.IGFVoicePlayListener;
import com.gofun.voice.IGFVoiceSpeechListener;
import com.gofun.voice.IGFVoiceWakeupListener;
import com.gofun.voice.manager.GFVoiceManager;
import com.gofun.voice.utils.TipsUtil;
import com.shouqiev.mirror.launcher.activity.MainActivity;
import com.shouqiev.mirror.launcher.activity.VoiceActivity;
import com.shouqiev.mirror.launcher.voice.manager.VoicePrecessManager;
import com.gofun.voice.model.VoiceMode;
import com.shouqiev.mirror.launcher.voice.manager.VoiceViewManager;
public class VoiceServices extends Service  implements Handler.Callback{


  private static final String TAG = "VoiceServices";
  private GFVoiceManager mVoiceManager;
  private static VoiceServices mService;

  private Handler mHandler;
  private final int VOICE_WAIT_STATUS =1;
  private final int VOICE_WAIT_OUTTIME =1000*30;
  private boolean isExit = false;
  private Class<?> exitClass = null;


  @Nullable @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  public static VoiceServices getService(){
    return mService;
  }
  @Override
  public void onCreate() {
    super.onCreate();
    mService  =this;
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    initService();
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy VoiceActivity");
    mVoiceManager.stopTTs();
    mVoiceManager.doWakeup();
    mVoiceManager.unRegisterWakeupListener(wakeupListener);
    mVoiceManager.unRegisterSpeechListener(speechListener);
    mVoiceManager.unRegisterTTSListener(playListener);
  }

  public void initService(){
    isExit = false;
    exitClass = null;
    if(mVoiceManager == null){
      mVoiceManager = GFVoiceManager.getInstance(VoiceServices.this);
    }
    if(!mVoiceManager.isInited()){
      mVoiceManager.init();
    }
    mVoiceManager.registerSpeechListener(speechListener);
    mVoiceManager.registerWakeupListener(wakeupListener);
    mVoiceManager.registerTTSListener(playListener);
    mHandler = new Handler(this);
  }

  private IGFVoiceWakeupListener.Stub wakeupListener = new IGFVoiceWakeupListener.Stub() {
    @Override
    public void onBeginOfSpeech() throws RemoteException {
      VoiceViewManager.getInstance().startRecordAnimation();
    }
    @Override
    public void onWakeup(String sst, String id, String score) throws RemoteException {
      wakeup();
    }
    @Override
    public void onVolumeChanged(int volume) throws RemoteException {
      VoiceViewManager.getInstance().refreshVolume(volume);
    }
    @Override
    public void onError(int code) throws RemoteException {
      Log.d(TAG,"Wakeup onError code="+code);
    }
  };



  private IGFVoiceSpeechListener.Stub speechListener = new IGFVoiceSpeechListener.Stub() {
    @Override
    public void onBeginOfSpeech() throws RemoteException {
      VoiceViewManager.getInstance().startRecordAnimation();
      VoiceViewManager.getInstance().stopLoadding();

    }
    @Override
    public void onEndOfSpeech() throws RemoteException {
//      VoiceViewManager.getInstance().stopRecordAnimation();
//      VoiceViewManager.getInstance().startLoadding();
    }
    @Override
    public void onResult(String jsonStr) throws RemoteException {
//      VoiceViewManager.getInstance().startRecordAnimation();
//      VoiceViewManager.getInstance().stopLoadding();
      isExit = false;
      exitClass = null;
      updateWaitTime();
      VoiceMode mode = new VoiceMode(jsonStr);
      try {
        if(!VoicePrecessManager.getInstance().doPrecess(VoiceServices.this,mode)){
          mVoiceManager.doPlayText(TipsUtil.TTS_UNKONW);
          mode.setAnswer(TipsUtil.TTS_UNKONW);
          VoiceViewManager.getInstance().upadteAnwserView(mode);
        }
      }catch (Exception e){
        mVoiceManager.doPlayText(TipsUtil.TTS_UNKONW);
        mode.setAnswer(TipsUtil.TTS_UNKONW);
        VoiceViewManager.getInstance().upadteAnwserView(mode);
      }
    }
    @Override
    public void onVolumeChanged(int volume) throws RemoteException {
      VoiceViewManager.getInstance().refreshVolume(volume);
    }
    @Override
    public void onSelect(int num, String msg) throws RemoteException {
      VoiceViewManager.getInstance().updateViewBySelect(num,msg);
    }
    @Override
    public void onWakeupWord() throws RemoteException {
      mVoiceManager.stopRecognition();
      mVoiceManager.doPlayText(TipsUtil.TTS_WAKE_RESPONSE);
    }
    @Override
    public void onError(int code) throws RemoteException {
      Log.d(TAG,"VoiceSpeech onError code="+code);
    }
  };

  private IGFVoicePlayListener.Stub playListener= new IGFVoicePlayListener.Stub(){
    @Override
    public void onPlayStart(String text) throws RemoteException {
      updateWaitTime();
    }
    @Override
    public void onPlayCompleted(String text) throws RemoteException {
      if(isExit){
        isExit = false;
        removeWaitTime();
        VoicePrecessManager.getInstance().clearStatus();
        VoiceViewManager.getInstance().finishVoiceActivity();
        if(exitClass!=null){
          mService.startActivity(new Intent(mService, exitClass).addFlags(
              Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
          exitClass=null;
        }
      }else{
        updateWaitTime();
      }
    }
    @Override
    public void onProgressReturn(String text, int progress, int beginPos, int endPos) throws RemoteException {
    }
    @Override
    public void onError(int code) throws RemoteException {
    }
  };

  public void startVoiceView(){
    startActivity(new Intent(this,VoiceActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
  }
  public static void startService(MainActivity mainActivity) {
    Intent service = new Intent(mainActivity, VoiceServices.class);
    mainActivity.startService(service);
  }

  private void updateWaitTime(){
    mHandler.removeMessages(VOICE_WAIT_STATUS);
    mHandler.sendEmptyMessageDelayed(VOICE_WAIT_STATUS,VOICE_WAIT_OUTTIME);
  }

  public void removeWaitTime(){
      mHandler.removeMessages(VOICE_WAIT_STATUS);
  }


  @Override
  public boolean handleMessage(Message msg) {
    switch (msg.what){
      case VOICE_WAIT_STATUS:
        this.isExit = true;
        mVoiceManager.doPlayText(TipsUtil.TTS_TIMEOUT,false);
        break;
    }
    return false;
  }

  public void wakeup(){
    isExit = false;
    exitClass = null;
    startVoiceView();
    VoicePrecessManager.getInstance().clearStatus();
    mVoiceManager.doPlayText(TipsUtil.TTS_WAKE_RESPONSE);
    updateWaitTime();
    VoiceViewManager.getInstance().showWakeupView();

  }

  public void exitVoice(){
    exitVoice(TipsUtil.TTS_OUTSIDE);
  }

  public void exitVoice(String tts){
    this.isExit = true;
    removeWaitTime();
    GFVoiceManager.getInstance(mService).doPlayText(tts, false);
  }

  public void exitVoiceAndStartActivity(Class<?> cls) {
    this.exitClass = cls;
    exitVoice();
  }

  public void exitVoiceAndStartActivity(Class<?> cls,String tts) {
    this.exitClass = cls;
    exitVoice(tts);
  }
}

package com.shouqiev.mirror.launcher.voice.manager;

import com.gofun.voice.manager.GFVoiceManager;
import com.gofun.voice.model.VoiceMode;
import com.gofun.voice.utils.TipsUtil;
import com.shouqiev.mirror.launcher.VoiceServices;
import com.shouqiev.mirror.launcher.activity.VoiceActivity;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
import com.shouqiev.mirror.launcher.voice.view.IGFViewType;
import com.shouqiev.mirror.launcher.voice.view.IGFVoiceViewListener;
import com.shouqiev.mirror.launcher.voice.view.VoiceViewFactory;

import java.util.ArrayList;
import java.util.List;
public class VoiceViewManager implements IGFVoiceViewListener {

  private static VoiceViewManager mInstance=null;

  private VoiceActivity voiceActivity = null;
  private List<IGFVoiceViewListener > OtherViews  = null;

  public static VoiceViewManager getInstance(){
    if(mInstance == null){
      mInstance = new VoiceViewManager();
    }
    return mInstance;
  }

  public VoiceViewManager(){
    OtherViews = new ArrayList<IGFVoiceViewListener>();
  }

  @Override
  public void clearView() {
    for (IGFVoiceViewListener listener:OtherViews){
      listener.clearView();
    }
    OtherViews.clear();
  }

  public void finishVoiceActivity(){
    if(voiceActivity!=null && !voiceActivity.isDestroyed()){
      voiceActivity.finish();
    }
  }

  @Override
  public void refreshView(BaseVoiceMsg viewMsg) {
    if(OtherViews.size()>0){
      for (IGFVoiceViewListener listener:OtherViews){
        listener.refreshView(viewMsg);
      }
    }else if(!GFVoiceManager.getInstance(VoiceServices.getService()).getIsMute().get()){
      if(voiceActivity == null || voiceActivity.isDestroyed()){
        VoiceServices.getService().startVoiceView();
      }else{
        voiceActivity.showVoiceView(viewMsg);
        VoiceViewFactory.getInstance().refreshView(viewMsg);
      }
    }
  }
  @Override
  public void updateViewBySelect(int num, String type) {
    if(OtherViews.size()>0){
      for (IGFVoiceViewListener listener:OtherViews){
        listener.updateViewBySelect(num,type);
      }
    }else{
      VoiceViewFactory.getInstance().updateViewBySelect(num,type);
    }
  }
  @Override
  public int getViewType() {
    return VoiceViewFactory.getInstance().getViewType();
  }
  public void upadteAnwserView(VoiceMode mode){
    refreshView(new BaseVoiceMsg(mode,IGFViewType.VIEW_TYPE_ANSWER));
  }

  public void registerVoiceActivity(VoiceActivity voiceActivity){
    this.voiceActivity = voiceActivity;
    showWakeupView();
  }

  public void unRegisterVoiceActivity(){
    this.voiceActivity = voiceActivity;
  }

  public void registerIGFVoiceView(IGFVoiceViewListener listener){
    if(!OtherViews.contains(listener )){
      OtherViews.add(listener);
    }
  }

  public void unRegisterIGFVoiceView(IGFVoiceViewListener listener){
    if(OtherViews.contains(listener )){
      OtherViews.remove(listener);
    }
  }
  public void showWakeupView() {
    VoiceMode mode= new VoiceMode();
    mode.setQuestion(TipsUtil.TTS_WAKE_RESPONSE);
    VoicePrecessManager.getInstance().clearStatus();
    refreshView(new BaseVoiceMsg(mode,IGFViewType.VIEW_TYPE_ANSWER));
  }

  public  void startRecordAnimation(){
    if(voiceActivity!=null){
      voiceActivity.startRecordAimation();
    }
  }

  public void stopRecordAnimation(){
    if(voiceActivity!=null){
      voiceActivity.stopRecordAimation();
    }
  }

  public void startLoadding(){
    if(voiceActivity!=null){
      voiceActivity.startLoaddind();
    }
  }

  public void stopLoadding(){
    if(voiceActivity!=null){
      voiceActivity.stopLoadding();
    }
  }

  public void refreshVolume(int volume){
    if(voiceActivity!=null){
      voiceActivity.refreshVolumne(volume);
    }
  }
}
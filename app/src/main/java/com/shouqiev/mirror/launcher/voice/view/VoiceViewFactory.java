package com.shouqiev.mirror.launcher.voice.view;

import com.shouqiev.mirror.launcher.base.VoiceBaseFragment;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
public class VoiceViewFactory implements IGFVoiceViewListener{
  private static VoiceViewFactory instance = null;
  public static VoiceViewFactory getInstance(){
    if (instance == null){
      instance = new VoiceViewFactory();
    }
    return instance;
  }

  private VoiceBaseFragment baseFragment = null;
  @Override
  public void clearView() {
    if(baseFragment!=null){
      baseFragment.clearView();
    }
    baseFragment = null;
  }
  @Override
  public void refreshView(BaseVoiceMsg baseVoiceMsg){
    if(baseVoiceMsg !=null) {
      getVoiceFragment(baseVoiceMsg);
      baseFragment.refreshView(baseVoiceMsg);
    }
  }
  @Override
  public void updateViewBySelect(int num, String type) {
    if(baseFragment!=null){
      baseFragment.updateViewBySelect(num,type);
    }
  }
  @Override
  public int getViewType() {
    return baseFragment!=null?baseFragment.getViewType():0;
  }

  public VoiceBaseFragment getVoiceFragment(BaseVoiceMsg baseVoiceMsg){
    if (baseFragment == null) {
      baseFragment = createVoiceFragment(baseVoiceMsg);
    }else if(baseFragment.getViewType() != baseVoiceMsg.getViewType()){
      baseFragment.clearView();
      baseFragment = createVoiceFragment(baseVoiceMsg);
    }
    return baseFragment;
  }

  private VoiceBaseFragment createVoiceFragment(BaseVoiceMsg baseVoiceMsg) {
    switch (baseVoiceMsg.getViewType()) {
      case IGFViewType.VIEW_TYPE_ANSWER:
        return new VoiceFragmentAnswer();
      case IGFViewType.VIEW_TYPE_NAVI:
        return new VoiceFragmentNavigation();
      case IGFViewType.VIEW_TYPE_CMD:
        return new VoiceFragmentCmd();
      default:
        return new VoiceFragmentAnswer();
    }
  }
}

package com.shouqiev.mirror.launcher.voice.precess;
import android.content.Context;
import android.text.TextUtils;

import com.gofun.voice.model.VoiceMode;
import com.gofun.voice.utils.GFOperateType;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.voice.manager.VoiceViewManager;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
import com.shouqiev.mirror.launcher.voice.view.IGFViewType;

import org.greenrobot.eventbus.EventBus;
public class VoiceMapPrecess implements IGFDialogPrecess{

  private Context mContext;

  @Override
  public boolean doPrecess(Context context, VoiceMode voiceMode) throws Exception {
    this.mContext = context;
    if(voiceMode == null || voiceMode.isEmpty()){
      return false;
    }
    switch (voiceMode.getIntent()){
      case GFOperateType.query:
        VoiceViewManager.getInstance().refreshView(new BaseVoiceMsg(voiceMode,IGFViewType.VIEW_TYPE_NAVI));
        break;
      case GFOperateType.locate:
        VoiceViewManager.getInstance().refreshView(new BaseVoiceMsg(voiceMode,IGFViewType.VIEW_TYPE_NAVI));
        break;
      case GFOperateType.close_map:
        EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_STATUS_EXIT_NAVI));
        break;
    }
    return true;
  }
  @Override
  public void clearStatus() {
  }
  @Override
  public String getServiceType() {
    return null;
  }
}

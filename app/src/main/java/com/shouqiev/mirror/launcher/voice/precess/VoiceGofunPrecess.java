package com.shouqiev.mirror.launcher.voice.precess;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gofun.voice.model.VoiceMode;
import com.gofun.voice.utils.GFOperateType;
import com.gofun.voice.utils.GFServiceType;
import com.gofun.voice.utils.TipsUtil;
import com.hikvision.mirror.HikMirrorApi;
import com.shouqiev.mirror.launcher.VoiceServices;
import com.shouqiev.mirror.launcher.activity.PhoneActivity;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.voice.manager.VoicePrecessManager;

import org.greenrobot.eventbus.EventBus;
public class VoiceGofunPrecess implements IGFDialogPrecess{

  private Context mContext;
  @Override
  public boolean doPrecess(Context context, VoiceMode voiceMode) throws Exception {
    this.mContext = context;
    VoicePrecessManager.getInstance().clearStatus();
    if(voiceMode == null || voiceMode.isEmpty()){
      return false;
    }
    switch (voiceMode.getIntent()){
      case GFOperateType.instruction:
        doInstruction(voiceMode);
        break;
      case GFOperateType.system:
          doSystem(voiceMode);
        break;
      case GFOperateType.select:
        break;
      case GFOperateType.tutorial:
        doTuroial(voiceMode);
        break;
    }
    return true;
  }

  @Override
  public void clearStatus() {
  }
  @Override
  public String getServiceType() {
    return GFServiceType.gofun;
  }

  private void doInstruction(VoiceMode voiceMode) {
    Log.e(TAG, "doInstruction:" + voiceMode.getTemplate());
    switch (voiceMode.getTemplate()) {
      case TipsUtil.instruction_logout:
      case TipsUtil.instruction_logout_1:
      case TipsUtil.instruction_logout_2:
        VoiceServices.getService().exitVoice();
        break;
    }
  }
  private void doSystem(VoiceMode voiceMode){
    Log.e(TAG, "doInstruction:" + voiceMode.getTemplate());
    switch (voiceMode.getTemplate()) {
      case TipsUtil.instruction_wakeuo_1:
      case TipsUtil.instruction_wakeuo_2:

        break;
      case TipsUtil.system_contact_customer_server:
      case TipsUtil.system_contact_customer_server1:
        VoiceServices.getService().exitVoiceAndStartActivity(PhoneActivity.class,TipsUtil.TTS_CUSTOM);
      break;
      case TipsUtil.system_safety_assist_off:
        HikMirrorApi.getInstance().setDBAVolumeEnable(false);
        HikMirrorApi.getInstance().setDBAEnable(false);
        VoiceServices.getService().exitVoice(TipsUtil.TTS_CLOSE_SAFE);
        break;
      case TipsUtil.system_safety_assist_on:
        HikMirrorApi.getInstance().setDBAVolumeEnable(true);
        HikMirrorApi.getInstance().setDBAEnable(true);
        VoiceServices.getService().exitVoice(TipsUtil.TTS_OPEN_SAFE);
        break;
      case TipsUtil.system_view_ctrl_open:
      case TipsUtil.system_view_ctrl_back:
        switch (voiceMode.getNormValue()){
          case "首页":
            EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_ENTER_HOME));
            break;
          case "地图":
            EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_ENTER_NAVI));
            break;
          case "教程":
            EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_ENTER_CAR));
            break;
          case "应用":
            EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_ENTER_MORE));
            break;
          default:
            break;
        }
        if(!TextUtils.isEmpty(voiceMode.getNormValue())){
          VoiceServices.getService().exitVoice(String.format(TipsUtil.system_view_ctrl_reply_msg,voiceMode.getTemplate().replace("{viewname}",voiceMode.getNormValue())));
        }
        break;
      case TipsUtil.system_view_ctrl_close:
        EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_ENTER_HOME));
        VoiceServices.getService().exitVoice(String.format(TipsUtil.system_view_ctrl_reply_msg,voiceMode.getTemplate().replace("{viewname}",voiceMode.getNormValue())));
        break;

    }
  }
  private void doTuroial(VoiceMode voiceMode) {
    Log.e(TAG, "doInstruction:" + voiceMode.getTemplate());
    switch (voiceMode.getTemplate()) {
      case TipsUtil.system_view_tutorial_play:
      case TipsUtil.system_view_tutorial_replay:
        EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_START_PLAY));
        VoiceServices.getService().exitVoice(String.format(TipsUtil.system_view_ctrl_reply_msg,voiceMode.getTemplate()));
        break;
      case TipsUtil.system_view_tutorial_stop1:
        EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_STOP_PLAY));
        VoiceServices.getService().exitVoice(String.format(TipsUtil.system_view_ctrl_reply_msg,voiceMode.getTemplate()));
        break;
      case TipsUtil.system_view_tutorial_play1:
      case TipsUtil.system_view_tutorial_play2:
        if(!TextUtils.isEmpty(voiceMode.getNormValue())){
          switch (voiceMode.getNormValue()){
            case "雨刷":
              EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_WIPER));
              break;
            case "启动":
              EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_START_ENGINE));
              break;
            case "空调":
              EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_AIR_CONDITIONER));
              break;
            case "灯光":
              EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_LIGHT));
              break;
            case "充电":
              EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_CHARGE));
              break;
            case "车门":
              EventBus.getDefault().post(new VoiceEvent(VoiceEvent.VOICE_TUTORIAL_DOOR));
              break;
          }
          VoiceServices.getService().exitVoice(String.format(TipsUtil.system_view_tutorial_reply_msg,voiceMode.getNormValue()));
        }else{
          VoiceServices.getService().exitVoice();
        }
        break;
    }
  }
}

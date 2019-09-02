package com.shouqiev.mirror.launcher.voice.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.gofun.voice.manager.GFVoiceManager;
import com.gofun.voice.model.VoiceMode;
import com.gofun.voice.utils.GFServiceType;
import com.shouqiev.mirror.launcher.voice.precess.IGFDialogPrecess;
import com.shouqiev.mirror.launcher.voice.precess.VoiceCmdPrecess;
import com.shouqiev.mirror.launcher.voice.precess.VoiceGofunPrecess;
import com.shouqiev.mirror.launcher.voice.precess.VoiceMapPrecess;
import java.util.HashMap;

public class VoicePrecessManager implements IGFDialogPrecess {

  private String TAG = "VoicePrecessManager";

  private Context mContext;

  private static VoicePrecessManager mInstance = null;

  private boolean canSpeak = true;

  private IGFDialogPrecess mPresentDialog = null;

  public static VoicePrecessManager getInstance() {
    if (mInstance == null) {
      mInstance = new VoicePrecessManager();
    }
    return mInstance;
  }

  public void setCanSpeak(boolean canSpeak) {
    this.canSpeak = canSpeak;
  }

  private static final HashMap<String, Class> DIALOG_PRECESS = new HashMap<String, Class>() {
    {
      put(GFServiceType.cmd, VoiceCmdPrecess.class);
      put(GFServiceType.gofun, VoiceGofunPrecess.class);
      put(GFServiceType.map, VoiceMapPrecess.class);
    }
  };

  public boolean isSupportDialog(String focus) {
    return DIALOG_PRECESS.containsKey(focus);
  }

  @Override public boolean doPrecess(Context context, VoiceMode mode) throws Exception {
    if (mode == null || mode.isEmpty()) {
      return false;
    }
    this.mContext = context;
    Log.d(TAG, "service=" + mode.getService() + ";  question=" + mode.getQuestion() + " ; answer" + mode.getAnswer());
    if (TextUtils.isEmpty(mode.getService())) {
      //没有结果返回暂不处理，等待下一次返回
      Log.e(TAG, "service is null");
      Log.d("voice", "json:" + mode.getOriJson());
      return true;
    } else if (isSupportDialog(mode.getService())) {
      Log.e(TAG, "isSupportDialog");
      if (mPresentDialog == null) {
        Class cls = DIALOG_PRECESS.get(mode.getService());
        Log.e(TAG, "doPrecess() cls.newInstance() : cls.name = " + cls.getName());
        IGFDialogPrecess newHandle = (IGFDialogPrecess) cls.newInstance();
        mPresentDialog = newHandle;
        return newHandle.doPrecess(context, mode);
      } else {
        mode.setService(mPresentDialog.getServiceType());
        return mPresentDialog.doPrecess(context, mode);
      }
    } else {
      if (!GFVoiceManager.getInstance(mContext).getIsMute().get()) {
        if (TextUtils.isEmpty(mode.getAnswer())) {
          return false;
        } else {
          if (canSpeak) {
            GFVoiceManager.getInstance(mContext).doPlayText(mode.getAnswer());
          }
        }
      }
      Log.e(TAG, "end");
    }
    VoiceViewManager.getInstance().upadteAnwserView(mode);
    return true;
  }

  @Override public void clearStatus() {
    mPresentDialog = null;
  }

  @Override public String getServiceType() {
    if (mPresentDialog != null) {
      return mPresentDialog.getServiceType();
    } else {
      return "";
    }
  }
}
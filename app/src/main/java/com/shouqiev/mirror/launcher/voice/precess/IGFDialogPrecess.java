package com.shouqiev.mirror.launcher.voice.precess;
import android.content.Context;
import com.gofun.voice.model.VoiceMode;
public interface IGFDialogPrecess {
  public String TAG = "DIALOG_PRECESS";

  public boolean doPrecess(Context context, VoiceMode voiceMode) throws Exception;

  public void clearStatus();

  public String getServiceType();

}

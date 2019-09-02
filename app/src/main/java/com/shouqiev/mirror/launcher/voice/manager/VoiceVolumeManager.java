package com.shouqiev.mirror.launcher.voice.manager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
public class VoiceVolumeManager {
  private Context mContext;
  private AudioManager mAudioManager;
  private final  String TAG = "VoiceVolumeManager";

  public static final int MUSIC_LOWER = 1;
  public static final int MUSIC_RAISE = 2;
  public static final int MUSIC_SETMAX = 3;
  public static final int MUSIC_SETMIN = 4;

  private static VoiceVolumeManager mInstance = null;
  public static VoiceVolumeManager getInstance(Context mContext){
    if(mInstance == null){
      mInstance = new VoiceVolumeManager(mContext);
    }
    return mInstance;
  }

  public VoiceVolumeManager(Context mContext){
    this.mContext = mContext;
    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
  }

  public void setVolumeUp(){
    setVolume(MUSIC_RAISE);
  }

  public void setVolumeDowm(){
    setVolume(MUSIC_LOWER);
  }

  public void setMaxVolume(){
    setVolume(MUSIC_SETMAX);
  }

  public void setMinVolume(){
    setVolume(MUSIC_SETMIN);
  }

  public void setMuteVolume(boolean mute){
    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
      mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,mute?AudioManager.ADJUST_MUTE:AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_SHOW_UI);
    }else{
      mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,mute);
    }
  }

  public void setVolumeToValue(int value){
    int max = getMaxVolume();
    if (value < 0) {
      value = 0;
    } else if (value > max) {
      value = max;
    }
    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,value, AudioManager.FLAG_SHOW_UI);
    }else{
      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
    }
  }

  public int  getMaxVolume(){
    Log.d(TAG,"maxVolume = " + mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
  }

  public int getCurrentVolume(){
    Log.d(TAG,"CurrentVolume = " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
  }

  public void setVolume(int type) {
    int current = getCurrentVolume();
    int max = getMaxVolume();
    switch (type) {
      case MUSIC_RAISE:
        current++;
        if (current >= max) {
          current = max;
        }
        break;
      case MUSIC_LOWER:
        current--;
        if (current <= 0) {
          current = 0;
        }
        break;
      case MUSIC_SETMAX:
        current = max;
        break;
      case MUSIC_SETMIN:
        current = 1;
        break;
      default:
        break;
    }
    setVolumeToValue(current);
  }

}

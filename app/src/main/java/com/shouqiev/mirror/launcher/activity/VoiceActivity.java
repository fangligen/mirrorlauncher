package com.shouqiev.mirror.launcher.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.gofun.voice.manager.GFVoiceManager;
import com.gofun.voice.utils.TipsUtil;
import com.haozhang.lib.AnimatedRecordingView;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.VoiceServices;
import com.shouqiev.mirror.launcher.base.VoiceBaseFragment;
import com.shouqiev.mirror.launcher.utils.KeyUtil;
import com.shouqiev.mirror.launcher.voice.manager.VoiceViewManager;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
import com.shouqiev.mirror.launcher.voice.view.IGFViewType;
import com.shouqiev.mirror.launcher.voice.view.VoiceViewFactory;
import java.util.Random;

/**
 * Created by mingsheng on 2018/9/4.
 */
public class VoiceActivity extends Activity  {
  private String TAG = "VoiceActivity";

  private ImageView loadingImg;
  private LinearLayout loadingLl;
  private AnimatedRecordingView recordingView;
  private RandomThreand mRandomThreand;
  private VoiceBaseFragment fragment;

  ImageView startRecordBtn;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    KeyUtil.hideBottomUIMenu(this);
    setContentView(R.layout.activity_voice);
    initUi();
    VoiceViewManager.getInstance().registerVoiceActivity(this);
    startRandomVolumeView();
  }

  @Override protected void onRestart() {
    KeyUtil.hideBottomUIMenu(this);
    super.onRestart();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    stopRandmVolumeView();
    VoiceServices.getService().removeWaitTime();
    GFVoiceManager.getInstance(this).stopTTs();
    GFVoiceManager.getInstance(this).doWakeup();
    VoiceViewManager.getInstance().unRegisterVoiceActivity();
  }

  @SuppressLint("ShowToast") private void initUi() {
    startRecordBtn = (ImageView) findViewById(R.id.activity_voice_start_record);
    loadingImg = findViewById(R.id.loadingImg);
    loadingLl = (LinearLayout)findViewById(R.id.loadingLl);
    recordingView = findViewById(R.id.recordingView);
    ObjectAnimator ra = ObjectAnimator.ofFloat(loadingImg, "rotation", 0f, 360f);
    loadingImg.setPivotX(27);
    loadingImg.setPivotY(27);
    ra.setDuration(500);
    ra.setRepeatCount(ValueAnimator.INFINITE);
    ra.setRepeatMode(ValueAnimator.RESTART);
    ra.setInterpolator(new LinearInterpolator());
    ra.start();
    findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    startRecordBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        GFVoiceManager.getInstance(VoiceActivity.this).doPlayText(TipsUtil.TTS_WAKE_RESPONSE);
        VoiceViewManager.getInstance().showWakeupView();
      }
    });
  }

  public void stopRecordAimation(){
    recordingView.stop();
  }
  public void startRecordAimation(){
    if (!recordingView.isWorking()) {
      recordingView.start();
    }
  }
  public void startLoaddind(){
    loadingLl.setVisibility(View.VISIBLE);
  }

  public void stopLoadding(){
    loadingLl.setVisibility(View.GONE);
  }
  public void refreshVolumne(int volume){
    if (GFVoiceManager.getInstance(VoiceActivity.this).isRecognition()) {
      if (recordingView != null) {
        if (!recordingView.isWorking() || mRandomThreand == null) {
          startRandomVolumeView();
        }
        if (volume != lastVolume) {
          lastVolume = volume;
        }
      }
    }
  }

  int lastVolume = 0;

  public static void start(Context context) {
    Intent i = new Intent(context, VoiceActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    context.startActivity(i);
  }

  private void startRandomVolumeView() {
    recordingView.start();

    //随机设置音量大小.
    if (mRandomThreand != null) {
      mRandomThreand.stopRunning();
      mRandomThreand = null;
    }
    mRandomThreand = new RandomThreand();
    mRandomThreand.start();
  }

  private void stopRandmVolumeView() {
    if (mRandomThreand != null) {
      mRandomThreand.stopRunning();
      mRandomThreand = null;
    }
    recordingView.setVolume(20);
  }
  private class RandomThreand extends Thread {
    private static final int MOVE_STOP = 1;

    private static final int MOVE_START = 0;

    private int state;

    @Override public void run() {
      state = MOVE_START;

      while (true) {
        if (state == MOVE_STOP) {
          break;
        }
        try {
          sleep(6);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        recordingView.setVolume(getRandom());
      }
    }

    public void stopRunning() {
      state = MOVE_STOP;
    }
  }

  private Random random = new Random();

  private int getRandom() {
    if (lastVolume <= 0) {
      lastVolume = 5;
    }
    if (lastVolume > 30) {
      lastVolume = 30;
    }
    int r = random.nextInt(lastVolume * 3 + 10) % (lastVolume * 3 - (lastVolume * 2) + 10) + (lastVolume * 2);
    return r;
  }

  public void showVoiceView(BaseVoiceMsg voiceMsg){
    if(fragment==null || fragment.getViewType() != voiceMsg.getViewType()){
      FragmentTransaction transaction = getFragmentManager().beginTransaction();
      fragment = VoiceViewFactory.getInstance().getVoiceFragment(voiceMsg);
      transaction.replace(R.id.activity_voice_coutent_layout,fragment);
      transaction.commit();
      startRecordBtn.setVisibility(fragment.getViewType() == IGFViewType.VIEW_TYPE_NAVI?View.VISIBLE:View.GONE);
      recordingView.setVisibility(fragment.getViewType() == IGFViewType.VIEW_TYPE_NAVI?View.GONE:View.VISIBLE);

    }
  }
}
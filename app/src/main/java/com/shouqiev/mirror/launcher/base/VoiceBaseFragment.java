package com.shouqiev.mirror.launcher.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.shouqiev.mirror.launcher.voice.view.IGFVoiceViewListener;
public abstract class VoiceBaseFragment extends Fragment implements View.OnClickListener,IGFVoiceViewListener {

  @Override public void onClick(View v) {

  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.setOnClickListener(this);
  }

}

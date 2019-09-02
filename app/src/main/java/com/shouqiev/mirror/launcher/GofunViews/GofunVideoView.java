package com.shouqiev.mirror.launcher.GofunViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class GofunVideoView extends VideoView {
  private String mPath;
  private PlayStatusListener playStatusListener;
  public GofunVideoView(Context context) {
    super(context);
  }

  public GofunVideoView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GofunVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setPlayStatusListener(PlayStatusListener playListener) {
    this.playStatusListener = playListener;
  }

  @Override public void pause() {
    super.pause();
    if(playStatusListener!=null){
      playStatusListener.onPause();
    }
  }

  @Override public void setVideoPath(String path) {
    super.setVideoPath(path);
    mPath = path;
  }

  public String getVideoPath(){
    return mPath;
  }

  @Override public void start() {
    super.start();
    if(playStatusListener!=null){
      playStatusListener.onStart();
    }
  }

  public interface PlayStatusListener{
    public void onPause();
    public void onStart();

  }
}

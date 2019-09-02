package com.gofun.voice.manager;
import android.content.Context;
import android.media.MediaPlayer;

import com.gofun.voice.R;
public class GFMediaManager {

    private MediaPlayer mediaPlayer;
    private Context mContext;

    private static GFMediaManager mInstance;
    public static GFMediaManager getInstance(){
        if(mInstance == null){
            mInstance = new GFMediaManager();
        }
        return mInstance;
    }

    public GFMediaManager initMedia(Context context) {
        this.mContext = context;
        mediaPlayer = MediaPlayer.create(mContext, R.raw.tip);
        try {
            //设置是否循环播放
            mediaPlayer.setLooping(false);
            //设置播放起始点
            mediaPlayer.seekTo(0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    public void playMedia(MediaPlayer.OnCompletionListener listener){
        if(listener!=null){
            mediaPlayer.setOnCompletionListener(listener);
        }
        mediaPlayer.start();
    }
}

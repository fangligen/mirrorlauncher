package com.shouqiev.mirror.launcher.GofunViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.shouqiev.mirror.launcher.R;

import java.util.Random;

public class VoiceDialog {
    private Dialog dialog;
    private ImageView voice;
    private Context context;
    private Random rand = new Random();

    public VoiceDialog(Context context) {
        this.context = context;
    }

    int[] imgIds = new int[]{R.drawable.ease_record_animate_01, R.drawable.ease_record_animate_02
            , R.drawable.ease_record_animate_03, R.drawable.ease_record_animate_04
            , R.drawable.ease_record_animate_05, R.drawable.ease_record_animate_06
            , R.drawable.ease_record_animate_07, R.drawable.ease_record_animate_08
            , R.drawable.ease_record_animate_09, R.drawable.ease_record_animate_10
            , R.drawable.ease_record_animate_11, R.drawable.ease_record_animate_12
            , R.drawable.ease_record_animate_13, R.drawable.ease_record_animate_14};

    public void showRecodingDialog() {
        dialog = new Dialog(context, R.style.Theme_audioDialog);
        dialog.setContentView(R.layout.dialog_voice);
        voice = (ImageView) dialog.findViewById(R.id.dialog_voice);
        dialog.show();
    }

    public void showDialog() {
        if (dialog != null) {
            dialog.show();
        }else{
            showRecodingDialog();
        }
        handler.removeMessages(100);
        handler.sendEmptyMessage(100);
    }

    public void dismissRecodingDialog() {
        handler.removeMessages(100);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    /**
     * 通过level来显示声音图片的更新
     *
     * @param level
     */
    public void updateVoice(int level) {
        if (dialog != null && dialog.isShowing()) {
            int i = level / 2;
            if (1 > 13) {
                i = 13;
            }
            voice.setImageResource(imgIds[i]);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100){
                updateVoice(rand.nextInt(26));
                sendEmptyMessageDelayed(100,500);
            }
        }
    };
}


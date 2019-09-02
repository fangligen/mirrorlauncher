package com.shouqiev.mirror.launcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.event.EndCallEvent;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.event.VoiceVolume;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhengyonghui on 2018/9/4.
 */
public class PhoneActivity extends Activity implements View.OnClickListener {

    private TimerTask timerTask;
    private Timer timer;
    private Handler handler;
    private TextView phone_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_phone);
        phone_text = (TextView) findViewById(R.id.phone_text);
        findViewById(R.id.phone_goback).setOnClickListener(this);
        findViewById(R.id.phone_stop).setOnClickListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (phone_text.getText().length() < 6) {
                            phone_text.setText(phone_text.getText() + ".");
                        } else {
                            phone_text.setText("拨号中");
                        }
                        break;
                    case 2:
                        finish();
                        break;
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 400);
        handler.sendEmptyMessageDelayed(2, 10 * 1000);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timerTask.cancel();
        handler.removeMessages(1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EndCallEvent event) {
        if (null != event) {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_goback:
            case R.id.phone_stop:
                finish();
                break;
        }
    }


}

package com.gofun.voice;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.gofun.voice.service.GFVoiceService;
import com.gofun.voice.utils.ConfigUtil;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class GoFunVoiceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSpeech();
        startService();
    }

    private void initSpeech() {
        StringBuffer param = new StringBuffer();
        param.append(SpeechConstant.APPID + "=" +  ConfigUtil.APP_ID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());
        Setting.setLogLevel(Setting.LOG_LEVEL.none);
    }

    public void startService() {
        Intent service = new Intent(this, GFVoiceService.class);
        startService(service);
    }
}

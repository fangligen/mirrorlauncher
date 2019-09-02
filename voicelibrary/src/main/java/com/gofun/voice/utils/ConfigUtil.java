package com.gofun.voice.utils;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ConfigUtil {
    public final static int ERROR_NOT_INIT = -1;
    public final static String APP_NAME = "CoFunVoice";
    public final static String APP_ID = "5b9ba2cf";

    public final static int VOICE_STATUS_UNINIT = 0;
    public final static int VOICE_STATUS_TTS = 1;
    public final static int VOICE_STATUS_WAKEUP = 2;
    public final static int VOICE_STATUS_SR = 3;


    //语意定制域名
    public final static String VOICE_MADE_SERVER = "HUIWOLF.gofun";
    //语意定制实例
    public final static String VOICE_MADE_SERVER_CMD = "cmd";
    public final static String VOICE_MADE_SERVER_SELECT = "select";




    public static boolean hasWakeupWord(String str){
        String filter = "(你好小(方|范|贩|芳|放|fun))|(小(方|范|贩|芳|放|fun)同学)";
        if(TextUtils.isEmpty(str)){
            return false;
        }
        Pattern p = Pattern.compile(filter);
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }
}

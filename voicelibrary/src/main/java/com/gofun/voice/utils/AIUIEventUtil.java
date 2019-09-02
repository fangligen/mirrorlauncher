package com.gofun.voice.utils;
import android.text.TextUtils;

import com.gofun.voice.model.VoiceMode;
import com.iflytek.aiui.AIUIEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class AIUIEventUtil {

    /**
     * 处理AIUI结果事件（听写结果和语义结果）
     * @param event 结果事件
     */
    public static String processResult(AIUIEvent event) {
        try {
            JSONObject bizParamJson = new JSONObject(event.info);
            JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
            JSONObject params = data.getJSONObject("params");
            JSONObject content = data.getJSONArray("content").getJSONObject(0);
            if (content.has("cnt_id")) {
                String cnt_id = content.getString("cnt_id");
                JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));
                String sub = params.optString("sub");
                if ("nlp".equals(sub)) {
                    return cntJson.getString("intent");
//                    return cntJson.optJSONObject("intent");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析选择结果
     * @param jsonStr
     * @return
     */
    public static JSONObject getSelectJson(String jsonStr){
        JSONObject  result= null;
        VoiceMode mode = new VoiceMode(jsonStr);
            if(ConfigUtil.VOICE_MADE_SERVER.equals(mode.getService())
                    && ConfigUtil.VOICE_MADE_SERVER_SELECT.equals(mode.getIntent())) {
                result = new JSONObject();
                try {
                    result.put("type", mode.getTemplate());
                    result.put("num", 0);
                    JSONObject slots = new JSONArray(mode.getSlots()).getJSONObject(0);
                    if (slots != null && "num".equals(slots.getString("name"))) {
                        result.put("num", slots.getInt("normValue"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        return result;
    }
}

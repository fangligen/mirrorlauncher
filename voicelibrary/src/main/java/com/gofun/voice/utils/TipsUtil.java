package com.gofun.voice.utils;
public interface TipsUtil {

    //指令词
    String instruction_logout = "退出";
    String instruction_logout_1 = "退出语音";
    String instruction_logout_2 = "关闭语音";
    String instruction_wakeuo_1="你好{wake_word}";
    String instruction_wakeuo_2="{wake_word}同学";
    String system_contact_customer_server = "联系客服";
    String system_contact_customer_server1 = "拨打客报电话";
    String system_safety_assist_on = "打开安全辅助";
    String system_safety_assist_off = "关闭安全辅助";
    String system_view_ctrl_open="打开{viewname}";
    String system_view_ctrl_close="关闭{viewname}";
    String system_view_ctrl_back="回到{viewname}";
    String system_view_ctrl_reply_msg="已为您%s";
    String system_view_tutorial_reply_msg="已为您播放%s教程";

    String system_view_tutorial_stop1="停止播放";
    String system_view_tutorial_replay ="继续播放";
    String system_view_tutorial_play ="播放教程";
    String system_view_tutorial_play1="怎么使用{tutorial}";
    String system_view_tutorial_play2="怎么{tutorial}";


    //本地提示语
    String TTS_WELCAME = "欢迎使用首汽共享汽车，祝您用车愉快，如需帮助，请说“小fun同学”。";
    String TTS_WAKE_RESPONSE = "在的，请吩咐";
    String TTS_UNKONW = "对不起，我不太理解，请尝试其它说法";
    String TTS_TIMEOUT = "我先休息了，有需要请叫我。";
    String TTS_OUTSIDE = "主人，再见";
    String TTS_CUSTOM = "已经为您拨打客服电话，请稍等！";
    String TTS_OPEN_SAFE = "安全辅助已开启！";
    String TTS_CLOSE_SAFE = "安全辅助已关闭！";



    String PAGING_COMPLETED = "请选择“上一页”，“下一页”，“第几个”，等操作";
    String ITEM_COMPLETED = "已选择，是否开始导航？";


    //音量
    String TTS_VOLUME_MAX = "音量已经调到最大了.";
    String TTS_VOLUME_MIN = "音量已经调到最小了.";
    String TTS_VOLUME_VALUE = "音量已经调到";
    String TTS_VOLUME_UNMUTE = "已经为您取消静音";



}

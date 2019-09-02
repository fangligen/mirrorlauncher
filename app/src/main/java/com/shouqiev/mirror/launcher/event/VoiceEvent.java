package com.shouqiev.mirror.launcher.event;

public class VoiceEvent {

  public int status;
  public String question;
  public String answer;

  /**
   * 0: 待唤醒状态
   * 1：已经唤醒，等待问问题
   * 2：收到回复
   * 3：没有听懂
   * 4：先展示问题，1500毫秒后刷新答案
   * 9：退出
   * 10:导航
   * 11:语音界面VoiceActivity finish，停止语音识别
   * 12:地图搜索完毕
   * 13:翻页完毕
   * 15:选择完毕
   * 16:上一页
   * 17:下一页
   * 18:第几个
   * 19:开始导航
   * 20:退出导航
   */
  public static final int VOICE_STATUS_WAITING_ASK = 1;
  public static final int VOICE_STATUS_LOADING = 4;
  public static final int VOICE_STATUS_EXIT = 9;
  public static final int VOICE_STATUS_NAVI = 10;
  public static final int VOICE_STATUS_MAP_SEARCH_COMPLETED = 12;
  public static final int VOICE_STATUS_SEARCH_PAGING_COMPLETED = 13;
  public static final int VOICE_STATUS_SELECTED_ITEM_COMPLETED = 15;
  public static final int VOICE_STATUS_PRE_PAGE = 16;
  public static final int VOICE_STATUS_NEXT_PAGE = 17;
  public static final int VOICE_STATUS_SELECT_ITEM = 18;
  public static final int VOICE_STATUS_START_NAVI = 19;
  public static final int VOICE_STATUS_EXIT_NAVI = 20;
  public static final int VOICE_STATUS_EXIT_NAVI_COMPLETED = 21;
  public static final int VOICE_STATUS_RESETVIEW = 22;


  public static final int VOICE_ENTER_HOME = 23;
  public static final int VOICE_ENTER_NAVI = 24;
  public static final int VOICE_ENTER_CAR = 25;
  public static final int VOICE_ENTER_MORE = 26;

  public static final int VOICE_TUTORIAL_DOOR = VOICE_ENTER_MORE+1;
  public static final int VOICE_TUTORIAL_CHARGE = VOICE_TUTORIAL_DOOR+1;
  public static final int VOICE_TUTORIAL_LIGHT = VOICE_TUTORIAL_CHARGE+1;
  public static final int VOICE_TUTORIAL_AIR_CONDITIONER = VOICE_TUTORIAL_LIGHT+1;
  public static final int VOICE_TUTORIAL_START_ENGINE = VOICE_TUTORIAL_AIR_CONDITIONER+1;
  public static final int VOICE_TUTORIAL_WIPER = VOICE_TUTORIAL_START_ENGINE+1;
  public static final int VOICE_TUTORIAL_START_PLAY = VOICE_TUTORIAL_WIPER+1;
  public static final int VOICE_TUTORIAL_STOP_PLAY = VOICE_TUTORIAL_START_PLAY+1;
  public int type;

  public VoiceEvent() {

  }

  public VoiceEvent(int status) {
    this.status = status;
  }
}

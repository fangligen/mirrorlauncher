package com.shouqiev.mirror.launcher.fragment.map.entity;

public class VoiceConstant {
  public static final String INTENT_MAP_SEARCH = "QUERY";
  public static final String INTENT_MAP_LOCATE = "LOCATE";
  public static final String INTENT_MAP_CLOSE = "CLOSE_MAP";
  public static final String INTENT_CMD = "cmd";
  public static final String SLOTS_END_LOC = "endLoc.ori_loc";
  public static final String SLOTS_NAME_WAKE = "wake_word";

  /**
   * 服务类型
   */
  public static final String SERVICE_CUSTOM = "HUIWOLF.gofun";
  public static final String SERVICE_MAP = "mapU";
  public static final String SERVICE_MAP_START_NAVI = "开始导航";

  /**
   * 地图相关语句
   */
  public static final String NO_SELECT_ITEM = "没有找到，请重新选择";
  public static final String SELECT_ITEM_FORMAT = "";
  public static final String SELECT_PAGE_FORMAT = "已经选择第%d页";
  public static final String SELECT_LAST_ITEM = "已经选择最后一个";
  public static final String NO_DESTINATION = "您尚未选择目的地";
  public static final String EXIT_NAVI_SUCESS = "导航结束";
  public static final String TIPS_SEARCH_FORMAT = "找到%d个结果，请选择第几页，第几个";

  //地图相关模版 template
  public static final String TEMPLATE_ITEM_NUM = "第{num}个";
  public static final String TEMPLATE_LAST_ITEM = "最后一个";
  public static final String TEMPLATE_NEXT_PAGE = "下一页";
  public static final String TEMPLATE_PRE_PAGE = "上一页";
  public static final String TEMPLATE_PAGE_NUM = "第{num}页";
  public static final String TEMPLATE_LAST_PAGE = "最后一页";
}

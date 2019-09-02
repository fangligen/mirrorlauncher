package com.shouqiev.mirror.launcher.fragment.map.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.view.inputmethod.InputMethodManager;
import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.services.core.LatLonPoint;
import com.shouqiev.mirror.launcher.R;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author gaoqian
 */
public class AMapUtil {
  final static String WEEK_NAME[] = { "SUN", "MON", "TUES", "WED", "THUR", "FRI", "SAT" };
  final static int KM_10 = 10000;
  final static int KM_1 = 1000;

  public static String getFriendlyLength(int lenMeter) {
    // 10 km
    if (lenMeter > KM_10) {
      int dis = lenMeter / KM_1;
      return dis + ChString.Kilometer;
    }
    if (lenMeter > KM_1) {
      float dis = (float) lenMeter / KM_1;
      DecimalFormat fnum = new DecimalFormat("##0.0");
      String dstr = fnum.format(dis);
      return dstr + ChString.Kilometer;
    }
    if (lenMeter > 100) {
      int dis = lenMeter / 50 * 50;
      return dis + ChString.Meter;
    }
    int dis = lenMeter / 10 * 10;
    //if (dis == 0) {
    //  dis = 0;
    //}
    return dis + ChString.Meter;
  }

  public static String[] getFriendlyLengthArray(int lenMeter) {
    String[] array = new String[2];
    // 10 km
    if (lenMeter > KM_10) {
      array[0] = String.valueOf(lenMeter / KM_1);
      array[1] = ChString.Kilometer;
    } else if (lenMeter > KM_1) {
      float dis = (float) lenMeter / KM_1;
      DecimalFormat fnum = new DecimalFormat("##0.0");
      array[0] = fnum.format(dis);
      array[1] = ChString.Kilometer;
    } else if (lenMeter > 100) {
      array[0] = String.valueOf(lenMeter / 50 * 50);
      array[1] = ChString.Meter;
    } else {
      int dis = lenMeter / 10 * 10;
      if (dis == 0) {
        dis = 10;
      }
      array[0] = String.valueOf(dis);
      array[1] = ChString.Meter;
    }
    return array;
  }

  /**
   * 把LatLng对象转化为LatLonPoint对象
   */
  public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
    return new LatLonPoint(latlon.latitude, latlon.longitude);
  }

  /**
   * 把LatLonPoint对象转化为LatLon对象
   */
  public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
    return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
  }

  /**
   * 把集合体的LatLonPoint转化为集合体的LatLng
   */
  public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
    ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
    for (LatLonPoint point : shapes) {
      LatLng latLngTemp = AMapUtil.convertToLatLng(point);
      lineShapes.add(latLngTemp);
    }
    return lineShapes;
  }

  /**
   * long类型时间格式化
   */
  public static String convertToTime(long time) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date(time);
    return df.format(date);
  }

  public static final String HtmlBlack = "#000000";
  public static final String HtmlGray = "#808080";

  public static String getFriendlyCurTime() {
    long time = System.currentTimeMillis();
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    Date date = new Date(time);
    String ft = df.format(date);
    return ft;
  }


  public static String getFriendlyCurDate() {
    Calendar calendar = Calendar.getInstance();
    Date date = new Date();
    calendar.setTime(date);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayOfWeek < 0) {
      dayOfWeek = 0;
    }
    SimpleDateFormat df = new SimpleDateFormat("MM/dd");
    String ft = df.format(date);
    return ft + " " + WEEK_NAME[dayOfWeek];
  }

  public static String getFriendlyTime(int second) {
    if (second > 3600) {
      int hour = second / 3600;
      int miniate = (second % 3600) / 60;
      return hour + "小时" + miniate + "分钟";
    }
    if (second >= 60) {
      int miniate = second / 60;
      return miniate + "分钟";
    }
    return second + "秒";
  }

  public static List<String> getFriendlyTimeArray(int second) {
    List<String> aray = new ArrayList<>();
    if (second > 3600) {
      int hour = second / 3600;
      int miniate = (second % 3600) / 60;
      aray.add(String.valueOf(hour));
      aray.add("小时");
      aray.add(String.valueOf(miniate));
      aray.add("分钟");
    } else if (second >= 60) {
      int miniate = second / 60;
      aray.add(String.valueOf(miniate));
      aray.add("分钟");
    } else {
      aray.add(String.valueOf(second));
      aray.add("秒");
    }
    return aray;
  }

  //路径规划方向指示和图片对应
  public static int getDriveActionID(String actionName) {
    if (actionName == null || actionName.equals("")) {
      return R.mipmap.dir3;
    }
    if ("左转".equals(actionName)) {
      return R.mipmap.dir2;
    }
    if ("右转".equals(actionName)) {
      return R.mipmap.dir1;
    }
    if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName)) {
      return R.mipmap.dir6;
    }
    if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName)) {
      return R.mipmap.dir5;
    }
    if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName)) {
      return R.mipmap.dir7;
    }
    if ("向右后方行驶".equals(actionName)) {
      return R.mipmap.dir8;
    }
    if ("直行".equals(actionName)) {
      return R.mipmap.dir3;
    }
    if ("减速行驶".equals(actionName)) {
      return R.mipmap.dir4;
    }
    return R.mipmap.dir3;
  }

  public static String getFriendlyArriveTime(long pathTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis() + pathTime);
    Date d = calendar.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    return sdf.format(d);
  }

  public static SpannableStringBuilder getFriendlyNaviInfo(NaviInfo naviInfo, Context context) {
    String remain_pfix = "剩余";
    String[] retain_dis = AMapUtil.getFriendlyLengthArray(naviInfo.getPathRetainDistance());
    List<String> retain_time = AMapUtil.getFriendlyTimeArray(naviInfo.getPathRetainTime());
    String arrive_time = AMapUtil.getFriendlyArriveTime(naviInfo.getPathRetainTime() * 1000);
    SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild();
    simplifySpanBuild.append(new SpecialTextUnit(remain_pfix, Color.parseColor("#88858A"), 14));
    simplifySpanBuild.append(new SpecialTextUnit(retain_dis[0], Color.parseColor("#000E00"), 14));
    simplifySpanBuild.append(new SpecialTextUnit(retain_dis[1] + "  ", Color.parseColor("#88858A"), 14));
    if (retain_time.size() == 4) {
      simplifySpanBuild.append(new SpecialTextUnit(retain_time.get(0), Color.parseColor("#000E00"), 14));
      simplifySpanBuild.append(new SpecialTextUnit(retain_time.get(1), Color.parseColor("#88858A"), 14));
      simplifySpanBuild.append(new SpecialTextUnit(retain_time.get(2), Color.parseColor("#000E00"), 14));
      simplifySpanBuild.append(new SpecialTextUnit(retain_time.get(3), Color.parseColor("#88858A"), 14));
    } else {
      simplifySpanBuild.append(new SpecialTextUnit(retain_time.get(0), Color.parseColor("#000E00"), 14));
      simplifySpanBuild.append(new SpecialTextUnit(retain_time.get(1), Color.parseColor("#88858A"), 14));
    }
    simplifySpanBuild.append(new SpecialTextUnit("  预计", Color.parseColor("#88858A"), 14));
    simplifySpanBuild.append(new SpecialTextUnit(arrive_time, Color.parseColor("#000E00"), 14));
    simplifySpanBuild.append(new SpecialTextUnit("到达", Color.parseColor("#88858A"), 14));
    return simplifySpanBuild.build();
  }

  public static SpannableStringBuilder getFriendlyRouteInfo(int distance, long time) {
    String remain_pfix = "剩余";
    String[] retain_dis = AMapUtil.getFriendlyLengthArray(distance);
    String arriveTime = AMapUtil.getFriendlyArriveTime(time * 1000);
    SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild();
    simplifySpanBuild.append(new SpecialTextUnit(remain_pfix, Color.parseColor("#88858A"), 14));
    simplifySpanBuild.append(new SpecialTextUnit(retain_dis[0], Color.parseColor("#000E00"), 14));
    simplifySpanBuild.append(new SpecialTextUnit(retain_dis[1], Color.parseColor("#88858A"), 14));
    simplifySpanBuild.append(new SpecialTextUnit("   | 预计", Color.parseColor("#88858A"), 14));
    simplifySpanBuild.append(new SpecialTextUnit(arriveTime, Color.parseColor("#000E00"), 14));
    simplifySpanBuild.append(new SpecialTextUnit("到达", Color.parseColor("#88858A"), 14));
    return simplifySpanBuild.build();
  }

  public static void hideKeyboard(Activity activity) {
    if (activity.getCurrentFocus() != null) {
      ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
          activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }
}

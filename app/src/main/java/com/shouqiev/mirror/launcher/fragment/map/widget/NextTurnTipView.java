package com.shouqiev.mirror.launcher.fragment.map.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.shouqiev.mirror.launcher.R;

/**
 * 下一个路口转向View类
 */
public class NextTurnTipView extends ImageView {
  private int[] customIconTypeDrawables;
  private Bitmap nextTurnBitmap;
  private long mLastIconType = -1;
  private Resources customRes;
  private int[] defaultIconTypes = {
      R.mipmap.caricon, R.mipmap.caricon, R.mipmap.hud_sou2, R.mipmap.hud_sou3, R.mipmap.hud_sou4, R.mipmap.hud_sou5,
      R.mipmap.hud_sou6, R.mipmap.hud_sou7, R.mipmap.hud_sou8, R.mipmap.hud_sou9, R.mipmap.hud_sou10, R.mipmap.hud_sou11,
      R.mipmap.hud_sou12, R.mipmap.hud_sou13, R.mipmap.hud_sou14, R.mipmap.hud_sou15, R.mipmap.hud_sou16, R.mipmap.hud_sou17,
      R.mipmap.hud_sou18, R.mipmap.hud_sou20,
  };
  /**
   * 导航段转向图标类型 无定义（数值：0）自定义转向图标数组，请忽略这个元素，从左转图标开始
   * 导航段转向图标类型 自车图标（数值：1）自定义转向图标数组，请忽略这个元素，从左转图标开始
   * 导航段转向图标类型 左转图标（数值：2）
   * 导航段转向图标类型 右转图标（数值：3）
   * 导航段转向图标类型 左前方图标（数值：4）
   * 导航段转向图标类型 右前方图标（数值：5）
   * 导航段转向图标类型 左后方图（数值：6）
   * 导航段转向图标类型 右后方图标（数值：7）
   * 导航段转向图标类型 左转掉头图标（数值：8）
   * 导航段转向图标类型 直行图标（数值：9）
   * 导航段转向图标类型 到达途经点图标（数值：10）
   * 导航段转向图标类型 进入环岛图标（数值：11）
   * 导航段转向图标类型 驶出环岛图标（数值：12）
   * 导航段转向图标类型 到达服务区图标（数值：13）
   * 导航段转向图标类型 到达收费站图标（数值：14）
   * 导航段转向图标类型 到达目的地图标（数值：15）
   * 导航段转向图标类型 到达隧道图标（数值：16）
   * 导航段转向图标类型：通过人行横道图标（数值：17）
   * 导航段转向图标类型：通过过街天桥图标（数值：18）
   * 导航段转向图标类型：通过地下通道图标（数值：19）
   * 导航段转向图标类型：通过广场图标（数值：20）
   */
  private String[] defaultIconTypesStr = {
      "即将", "即将", "左转", "右转", "左前方", "右前方", "左后方", "右后方", "左转掉头", "直行", "途经点", "进入环岛", "驶出环岛", "到达服务区", "到达收费站", "到达目的地", "到达隧道",
      "通过人行横道", "通过过街天桥", "通过地下通道",
  };

  /**
   * 用于设置自定义的icon图片数组。
   * <p>
   * 此方法与NextTurnTipView.setIconType(int)相互配合。
   * 导航过程中，SDK会调用NextTurnTipView.setIconType(int)设置转向图片，其中图片
   * 就是通过customIconTypes[]数组对应index下标获取的。
   *
   * @param res Resource资源
   * @param customIconTypes icon数组，数组中存放图片的顺序需要与IconType依次对应
   */
  public void setCustomIconTypes(Resources res, int[] customIconTypes) {
    try {
      if (res == null || customIconTypes == null) {
        return;
      }
      this.customRes = res;
      customIconTypeDrawables = new int[customIconTypes.length + 2];
      for (int i = 0; i < customIconTypes.length; i++) {
        customIconTypeDrawables[i + 2] = customIconTypes[i];
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  /**
   * @exclude
   */
  public NextTurnTipView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  /**
   * @exclude
   */
  public NextTurnTipView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * @exclude
   */
  public NextTurnTipView(Context context) {
    super(context);
  }

  /**
   * 释放图片资源
   */
  public void recycleResource() {
    try {
      if (nextTurnBitmap != null) {
        nextTurnBitmap.recycle();
        nextTurnBitmap = null;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  /**
   * 设置自定义的转向图标，请参考{@link com.amap.api.navi.enums.IconType}
   *
   * @param iconType 图片的id
   */
  public void setIconType(int iconType) {
    try {
      if (iconType > 19 || mLastIconType == iconType) {
        return;
      }
      recycleResource();
      if (customIconTypeDrawables != null && customRes != null) {
        nextTurnBitmap = BitmapFactory.decodeResource(customRes, customIconTypeDrawables[iconType]);
      } else {
        nextTurnBitmap = BitmapFactory.decodeResource(getResources(), defaultIconTypes[iconType]);
      }
      setImageBitmap(nextTurnBitmap);
      mLastIconType = iconType;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String setIconTypeStr(int iconType) {
    try {
      if (iconType > 19 || mLastIconType == iconType) {
        return "";
      }
      return defaultIconTypesStr[iconType];
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}

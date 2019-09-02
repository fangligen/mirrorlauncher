package com.shouqiev.mirror.launcher.utils;

import android.content.Context;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.params.CommonParams;

public class TutorialUtil {
  public static String[] getTutorialTitlesWithKey(Context context, String key){
    switch (key){
      case CommonParams.CAR_TYPE_TYPE_ARIZA5E:
        return context.getResources().getStringArray(R.array.type_ariza5e);
      //case CommonParams.CAR_TYPE_TYPE_2008:
      //return context.getResources().getStringArray(R.array.type_2008);
      //case CommonParams.CAR_TYPE_TYPE_CAVALIER:
      //  return context.getResources().getStringArray(R.array.type_cavalier);
      //case CommonParams.CAR_TYPE_TYPE_E50:
      //  return context.getResources().getStringArray(R.array.type_e50);
      //case CommonParams.CAR_TYPE_TYPE_EQ:
      //  return context.getResources().getStringArray(R.array.type_eq);
      //case CommonParams.CAR_TYPE_TYPE_EQ1:
      //  return context.getResources().getStringArray(R.array.type_eq1);
      //case CommonParams.CAR_TYPE_TYPE_EV200:
      //  return context.getResources().getStringArray(R.array.type_ev200);
      //case CommonParams.CAR_TYPE_TYPE_I3:
      //  return context.getResources().getStringArray(R.array.type_i3);
      //case CommonParams.CAR_TYPE_TYPE_IEV5:
      //  return context.getResources().getStringArray(R.array.type_iev5);
      //case CommonParams.CAR_TYPE_TYPE_IEV6E:
      //  return context.getResources().getStringArray(R.array.type_iev6e);
      //case CommonParams.CAR_TYPE_TYPE_JETTA:
      //  return context.getResources().getStringArray(R.array.type_jetta);
      default:
        return null;
    }
  }

  public static String[] getTutorialTitlesWithId(Context context, int key) {
    switch (key) {
      case R.drawable.gofun_car_type_2008:
        return context.getResources().getStringArray(R.array.type_2008);
      case R.drawable.gofun_car_type_ariza5e_white:
        return context.getResources().getStringArray(R.array.type_ariza5e);
      case R.drawable.gofun_car_type_cavalier:
        return context.getResources().getStringArray(R.array.type_cavalier);
      case R.drawable.gofun_car_type_e50:
        return context.getResources().getStringArray(R.array.type_e50);
      case R.drawable.gofun_car_type_eq:
        return context.getResources().getStringArray(R.array.type_eq);
      case R.drawable.gofun_car_type_eq1:
        return context.getResources().getStringArray(R.array.type_eq1);
      case R.drawable.gofun_car_type_ev200:
        return context.getResources().getStringArray(R.array.type_ev200);
      case R.drawable.gofun_car_type_i3_silver:
        return context.getResources().getStringArray(R.array.type_i3);
      case R.drawable.gofun_car_type_iev5:
        return context.getResources().getStringArray(R.array.type_iev5);
      case R.drawable.gofun_car_type_iev6e:
        return context.getResources().getStringArray(R.array.type_iev6e);
      case R.drawable.gofun_car_type_jetta:
        return context.getResources().getStringArray(R.array.type_jetta);
      default:
        return null;
    }
  }

  public static String getTutorialDirectoryName(int resourceId){
    switch (resourceId) {
      case R.drawable.gofun_car_type_ariza5e_white:
        return CommonParams.CAR_TYPE_TYPE_ARIZA5E;
      //case R.drawable.gofun_car_type_2008:
      //return CommonParams.CAR_TYPE_TYPE_EV200;
      //case R.drawable.gofun_car_type_cavalier:
      //  return CommonParams.CAR_TYPE_TYPE_CAVALIER;
      //case R.drawable.gofun_car_type_e50:
      //  return CommonParams.CAR_TYPE_TYPE_E50;
      //case R.drawable.gofun_car_type_eq:
      //  return CommonParams.CAR_TYPE_TYPE_EQ;
      //case R.drawable.gofun_car_type_eq1:
      //  return CommonParams.CAR_TYPE_TYPE_EQ1;
      //case R.drawable.gofun_car_type_ev200:
      //  return CommonParams.CAR_TYPE_TYPE_EV200;
      //case R.drawable.gofun_car_type_i3_silver:
      //  return CommonParams.CAR_TYPE_TYPE_I3;
      //case R.drawable.gofun_car_type_iev5:
      //  return CommonParams.CAR_TYPE_TYPE_IEV5;
      //case R.drawable.gofun_car_type_iev6e:
      //  return CommonParams.CAR_TYPE_TYPE_IEV6E;
      //case R.drawable.gofun_car_type_jetta:
      //  return CommonParams.CAR_TYPE_TYPE_JETTA;
      default:
        return null;
    }

  }

}

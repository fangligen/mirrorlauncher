package com.shouqiev.mirror.launcher.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.hikvision.mirror.HikMirrorApi;
import com.hikvision.mirror.interfaces.DataListener;
import com.hikvision.mirror.interfaces.SnapCallback;

import java.io.File;

public class SnapUtil {



  private static byte[] imgByte = new byte[]{};
  /**
   *
   * @param num 摄像头 1-5
   * @param listener
   */
  public static void startSnap(int num,final SnapListener listener) {

    HikMirrorApi.getInstance().startSnap(num, new SnapCallback() {
        @Override
      public void onSnapResult(final boolean b, final String s) {
        Log.i("", "startSnap b=" + b + ",s=" + s);
        if (!b) {
          onError(listener,"snap startSnap error");
          return;
        }

        HikMirrorApi.getInstance().downSnapFile(s, new DataListener() {
          @Override
          public void onNewData(int i, final byte[] bytes, int i1, int i2) {
            Log.i("", "startSnap onNewData i=" + i + ",i1=" + i1 + ",i2=" + i2);
            imgByte = byteMerger(imgByte,bytes);
          }

          @Override
          public void onDownloadOver(boolean b) {
            if (b) {
              try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                if (null == bitmap) {
                  onError(listener,"create bitmap fail");
                  return;
                }
                String path = getSDPath() + "/" + s;
                boolean f = BitmapUtil.saveBitmap(bitmap, path);
                if (!f) {
                  onError(listener,"file is empty");
                  return;
                }
                if (null != listener) {
                  imgByte = new byte[]{};
                  listener.onSuccess(bitmap, path);
                }
              } catch (Exception e) {
                e.printStackTrace();
                onError(listener,"onDownloadOver exception");
              }
            }else{
              onError(listener,"onDownloadOver failed");
            }
          }
        });
      }
    });
  }

  private static void onError(final SnapListener listener,String errorCode){
    imgByte = new byte[]{};
    if (null != listener) {
      listener.onFail(errorCode);
    }
  }

  public static String getSDPath() {
    File sdDir = null;
    boolean sdCardExist = Environment.getExternalStorageState()
        .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
    if (sdCardExist) {
      sdDir = Environment.getExternalStorageDirectory();//获取跟目录
    }
    return sdDir.toString();
  }

  //java 合并两个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
    byte[] byte_3 = new byte[byte_1.length+byte_2.length];
    System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
    return byte_3;
  }

  public interface SnapListener {
    void onSuccess(Bitmap bitmap, String path);
    void onFail(String errorCode);
  }

  /**
   * 打开关闭安全提示的功能
   * @param b
   * @return
   */
  public static void setDBAVolume(boolean b) {
    HikMirrorApi.getInstance().setDBAVolumeEnable(b);
    HikMirrorApi.getInstance().setDBAEnable(b);
  }
}

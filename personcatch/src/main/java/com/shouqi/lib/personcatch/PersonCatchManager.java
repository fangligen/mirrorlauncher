/**
 * 人体、物品识别工具类
 *
 * 使用方法：调用recognizeImage()函数即可
 * 例:PersonCatchManager.getInstance(context).recognizeImage(bitmap,"person",callback);
 * 参数说明：
 * bitmap：需要识别图片
 * "person"：指定特别标签，根据pb库及class文件填写
 * callback：回调监听
 *
 * 回调监听方法说明：
 * onResult(int type, List<Classifier.Recognition> results)
 * type：返回类型，1成功;2 PB模型初始化失败; 3识别无结果。
 * results：返回识别结果数据。
 *
 * pb修改方法：
 * 1. 将.pb模型文件，.txt模型标签文件 放入assets目录。
 * 2. 修改本类的TF_OD_API_MODEL_FILE常量和TF_OD_API_LABELS_FILE常量指向文件地址
 *
 * 图片压缩尺寸修改：
 * 1. 修改本类TF_OD_API_INPUT_SIZE常量数值，建议最大720
 *
 */
package com.shouqi.lib.personcatch;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class PersonCatchManager {

  private String TF_OD_API_MODEL_FILE = "file:///android_asset/ssd_mobilenet_v1_android_export.pb";
  private String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
  private int TF_OD_API_INPUT_SIZE = 720;

  PersonCatchInterface catchListener;
  Bitmap checkBitmap;
  String checkLabel;

  private Handler handler;
  private HandlerThread handlerThread;
  private Classifier detector;
  private static PersonCatchManager instance = null;
  private Context mContext;
  public static PersonCatchManager getInstance(Context context){
    if (instance == null){
      instance = new PersonCatchManager(context);
    }
    return instance;
  };
  public PersonCatchManager(Context mContext){
    this.mContext = mContext;
    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }
  /**
   * 送到入图片，返回所有结果
   * @param bitmap
   * @param catchInterface
   */
  public void recognizeImage(Bitmap bitmap,PersonCatchInterface catchInterface){
    recognizeImage(bitmap,null,catchInterface);
  }
  /**
   * 送入图片，返回指定标签个数。
   * @param bitmap
   * @param label
   * @param catchInterface
   */
  public void recognizeImage(Bitmap bitmap,String label,PersonCatchInterface catchInterface){
    this.catchListener = catchInterface;
    this.checkBitmap = bitmap;
    this.checkLabel = label;
    if(bitmap == null){
      return;
    }
    runInBackground(
        new Runnable() {
          @Override
          public void run() {
            if(detector == null){
              init();
            }
            try{
              int width = checkBitmap.getWidth();
              int height = checkBitmap.getHeight();
              int max = Math.max(width, height);
              if (max > TF_OD_API_INPUT_SIZE) {
                float scale = ((float)TF_OD_API_INPUT_SIZE)/ max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                checkBitmap = Bitmap.createScaledBitmap(checkBitmap, w, h, true);
              }
              final List<Classifier.Recognition> tempList = detector.recognizeImage(checkBitmap);
              List<Classifier.Recognition> results = new ArrayList<>();
              for (Classifier.Recognition result : tempList) {
                if(result.getConfidence()>0){
                  if(TextUtils.isEmpty(checkLabel)){
                    results.add(result);
                  }else if(checkLabel.equals(result.getTitle())){
                    results.add(result);
                  }
                }
              }
              if(catchListener!=null){
                catchListener.onResult(results.size()==0?PersonCatchInterface.status_error_none:PersonCatchInterface.status_success,results);
              }
            }catch (Exception e){
              e.printStackTrace();
              if(catchListener!=null){
                catchListener.onResult(PersonCatchInterface.status_error_initfile,null);
              }
            }

          }
        }
    );
  }


  private void init(){
    try {
      detector = TensorFlowObjectDetectionAPIModel.create(
          mContext.getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
    } catch (final IOException e) {
        e.printStackTrace();
//      Toast.makeText(mContext, "Classifier could not be initialized", Toast.LENGTH_SHORT).show();
      if(catchListener !=null){
        catchListener.onResult(PersonCatchInterface.status_error_initfile,null);
      }
    }
  }


  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

}

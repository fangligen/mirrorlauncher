//package com.shouqiev.mirror.launcher.fragment.Car;
//import android.animation.ObjectAnimator;
//import android.animation.ValueAnimator;
//import android.app.Fragment;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.LinearInterpolator;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.rd.PageIndicatorView;
//import com.rd.animation.type.AnimationType;
//import com.shouqiev.mirror.launcher.GofunViews.GofunStyleTextView;
//import com.shouqiev.mirror.launcher.R;
//import com.shouqiev.mirror.launcher.activity.MainActivity;
//import com.shouqiev.mirror.launcher.base.BaseFragment;
//import com.shouqiev.mirror.launcher.fragment.CarFragment;
//import com.shouqiev.mirror.launcher.fragment.map.util.AMapUtil;
//import com.shouqiev.mirror.launcher.fragment.map.dao.EnvironmentDaoUtils;
//import com.shouqiev.mirror.launcher.model.EnvironmentDetail;
//import com.shouqiev.mirror.launcher.utils.SnapUtil;
//
//import java.io.File;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Created by zhengyonghui on 2018/9/4.
// */
//public class CarEnvironmentFragment extends BaseFragment implements MirrorRestClient.OnMirrorClientCallBack{
//
//  static final int MSG_NET_CALL_BACK_SUCCESS =1;
//  static final int MSG_NET_CALL_BACK_FAIL=2;
//  static final int MSG_TAKE_PIC_COMPLETE=3;
//  static final int MSG_TAKE_PIC_FAIL=4;
//  static final String ERROR_NET_PIC="网络错误";
//  static final String ERROR_TAKE_PIC="拍照错误";
//
//  private Context mContext;
//  private CounterDownUtil counterDownUtil;
//  Button captureEnvironment,back,moveLeft,moveRight;
//  private long millisUntilFinished;
//
//  TextView envDetailTv,getEnvDetailTime;
//  private ImageView dialogImg;
//  private GofunStyleTextView environmentLevel;
//  private ProgressDialog loadingDialog;
//  private View dialogView;
//  private ViewPager envDetail;
//  private PageIndicatorView pageIndicator;
//  private DetailPagerAdapter adapter;
//  private View view;
//  private List<EnvironmentDetail> filePath = new ArrayList<EnvironmentDetail>();
//
//  private EnvironmentDaoUtils environmentDaoUtils;
//  MirrorRestClient mirrorRestClient ;
//
//  String tmpPicPath="";
//
//  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    environmentDaoUtils=new EnvironmentDaoUtils(getActivity());
//  }
//
//  @Override
//  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    try {
//      mContext = getActivity();
//      view = inflater.inflate(R.layout.fragment_car_environment, null);
//      dialogView = inflater.inflate(R.layout.uploading_view, null);
//      initViews(view);
//      addListeners();
//      setData();
//    } catch (Exception e) {
//      e.printStackTrace();
//    } finally {
//      return view;
//    }
//  }
//
//  @Override
//  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//    super.onActivityCreated(savedInstanceState);
//  }
//  Handler mainHandler = new Handler(){
//    @Override public void dispatchMessage(Message msg) {
//      super.dispatchMessage(msg);
//      switch (msg.what){
//        case MSG_NET_CALL_BACK_SUCCESS:
//          if(msg.obj!=null){
//            String jsonData=msg.obj.toString();
//            refreshView(jsonData,tmpPicPath,true);
//          }
//          break;
//        case MSG_TAKE_PIC_COMPLETE:
//          if(msg.obj!=null) {
//            String picPath = msg.obj.toString();
//            uploadPic(picPath);
//          }
//          break;
//        case MSG_NET_CALL_BACK_FAIL:
//          String error="";
//          if(msg.obj!=null){
//            error=msg.obj.toString();
//          }else{
//            error="网络异常";
//          }
//          refreshView(error,null,false);
//          break;
//        case  MSG_TAKE_PIC_FAIL:
//          String error_pic="";
//          if(msg.obj!=null) {
//            error_pic=msg.obj.toString();
//          }else{
//            error_pic="设备异常";
//          }
//          refreshView(error_pic, null, false);
//          break;
//      }
//    }
//  };
//
//  private void setData() throws Exception{
//    filePath = environmentDaoUtils.queryBeans();
//    adapter = new DetailPagerAdapter(mContext,filePath);
//    envDetail.setAdapter(adapter);
//    envDetail.setCurrentItem(adapter.getCount()-1>0? adapter.getCount()-1:0);
//  }
//
//  private void setCurrentData(int position){
//    EnvironmentDetail detail =  filePath.get(position);
//    float level = detail.getEnvLevel();
//    if (level <0.1f){
//      level = 0.1f;
//    }
//    if (level > 0.65){
//      environmentLevel.setTextColor(getResources().getColor(R.color.text_order_green));
//    } else {
//      environmentLevel.setTextColor(getResources().getColor(R.color.text_order_red));
//    }
//    DecimalFormat fnum = new DecimalFormat("##0.0");
//    String strlevel = fnum.format(level* 10);
//    getEnvDetailTime.setText(detail.getImgTime());
//    environmentLevel.setText(strlevel);
//  }
//
//
//  private void initViews(View view){
//    moveLeft = view.findViewById(R.id.env_detail_left);
//    moveRight = view.findViewById(R.id.env_detail_right);
//    envDetailTv = view.findViewById(R.id.enviroment_view);
//    envDetail = view.findViewById(R.id.car_environment_detail);
//    pageIndicator = view.findViewById(R.id.pageIndicatorView);
//    pageIndicator.setAnimationType(AnimationType.WORM);
//    back = view.findViewById(R.id.back_forward_environment);
//    captureEnvironment = view.findViewById(R.id.capture_environment);
//    dialogImg = dialogView.findViewById(R.id.loadingImg);
//    environmentLevel = view.findViewById(R.id.environment_level);
//    getEnvDetailTime = view.findViewById(R.id.enviroment_time);
//    loadingDialog = new ProgressDialog(mContext,R.style.dialog_style);
//    loadingDialog.setCancelable(false);
//  }
//
//  private void addListeners(){
//    envDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//      }
//
//      @Override public void onPageSelected(int position) {
//        setCurrentData(position);
//      }
//
//      @Override public void onPageScrollStateChanged(int state) {
//
//      }
//    });
//    moveRight.setOnClickListener(new View.OnClickListener() {
//      @Override public void onClick(View v) {
//        if (envDetail.getCurrentItem()==adapter.getCount()-1){
//          envDetail.setCurrentItem(0);
//        } else {
//          envDetail.setCurrentItem(envDetail.getCurrentItem()+1);
//        }
//      }
//    });
//
//    moveLeft.setOnClickListener(new View.OnClickListener() {
//      @Override public void onClick(View v) {
//        if (envDetail.getCurrentItem() == 0){
//          envDetail.setCurrentItem(adapter.getCount()-1);
//        } else {
//          envDetail.setCurrentItem(envDetail.getCurrentItem()-1);
//        }
//      }
//    });
//
//    back.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        CarFragment.carFragment.switchFragment(MainActivity.CAR_FRAGMENT_TAG,0);
//
//        if (counterDownUtil != null && counterDownUtil.isRunning()) {
//          counterDownUtil.extinguish();
//          captureEnvironment.setText("拍照");
//          captureEnvironment.setEnabled(true);
//          if (finishRunnable!=null) {
//            mainHandler.removeCallbacks(finishRunnable);
//            mainHandler.removeCallbacks(tickRunnable);
//          }
//        }
//      }
//    });
//
//    captureEnvironment.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        captureEnvironment.setEnabled(false);
//        counterDownUtil = new CounterDownUtil(10 * 1000) {
//          @Override
//          public void onTick(final long millis) {
//            millisUntilFinished = millis;
//            mainHandler.post(tickRunnable);
//          }
//
//          @Override
//          public void onFinish() {
//            mainHandler.post(finishRunnable);
//            try {
//              takePic();
//            } catch (Exception e) {
//              e.printStackTrace();
//            }
//          }
//        };
//
//        counterDownUtil.fire();
//      }
//    });
//  }
//
//
//
//  private void takePic(){
//    SnapUtil.startSnap(3, new SnapUtil.SnapListener() {
//      @Override
//      public void onSuccess(Bitmap bitmap, final String path) {
//        try {
//          if (bitmap!=null && !bitmap.isRecycled()){
//            bitmap.recycle();
//          }
//          tmpPicPath=path;
//          Message message=new Message();
//          message.what=MSG_TAKE_PIC_COMPLETE;
//          message.obj=path;
//          mainHandler.sendMessage(message);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//
//      @Override
//      public void onFail(String errorMsg) {
//        Message message=new Message();
//        message.what=MSG_TAKE_PIC_FAIL;
//        message.obj= TextUtils.isEmpty(errorMsg)?ERROR_TAKE_PIC:errorMsg;
//        mainHandler.sendMessage(message);
//      }
//    });
//
//  }
//
//  private void uploadPic(final String path){
//    new Thread(new Runnable() {
//      @Override public void run() {
//        File bitmapFile = new File(path);
//        if(mirrorRestClient==null){
//          mirrorRestClient = new MirrorRestClient();
//          mirrorRestClient.setMirrorClientCallBack(CarEnvironmentFragment.this);
//        }
//        mirrorRestClient.upload(bitmapFile);
//      }
//    }).start();
//
//  }
//
//  private void refreshView(String result,String sdPath,boolean success){
//    if(success) {
//      //{"dirty": "0.81835896", "clean": "0.18156165", "result": "dirty", "img_url": "upload/ch13_20180919_175757_0056_00_036110336_000246532.jpg"}
//      ImgDetectionBean detectionBean= JSON.parseObject(result,ImgDetectionBean.class);
//      Float level = Float.parseFloat( detectionBean.getClean());
//      EnvironmentDetail detail = new EnvironmentDetail();
//      detail.setImgPath(sdPath);
//      detail.setEnvLevel(level);
//      detail.setImgTime(AMapUtil.getFriendlyCurTime());
//      environmentDaoUtils.insert(detail);
//      try {
//        setData();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }else{
//      captureEnvironment.setEnabled(true);
//      captureEnvironment.setText("拍照");
//      getEnvDetailTime.setText(result);
//    }
//    if (loadingDialog!=null&&loadingDialog.isShowing()){
//      loadingDialog.dismiss();
//    }
//  }
//
//
//  @Override
//  public void onPause() {
//    super.onPause();
//  }
//
//  Runnable tickRunnable = new Runnable() {
//    @Override
//    public void run() {
//      captureEnvironment.setText("" + millisUntilFinished  / 1000 + "s");
//    }
//
//  };
//
//  Runnable finishRunnable = new Runnable() {
//    @Override
//    public void run() {
//      if (!loadingDialog.isShowing()){
//        loadingDialog.show();
//        loadingDialog.setContentView(dialogView);
//
//        View decorView = loadingDialog.getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//
//
//        ObjectAnimator ra = ObjectAnimator.ofFloat(dialogImg, "rotation", 0f, 360f);
//        ra.setDuration(500);
//        ra.setRepeatCount(ValueAnimator.INFINITE);
//        ra.setRepeatMode(ValueAnimator.RESTART);
//        ra.setInterpolator(new LinearInterpolator());
//        ra.start();
//      }
//      captureEnvironment.setEnabled(true);
//      captureEnvironment.setText("拍照");
//    }
//  };
//
//  @Override public void onSuccess(String data) {
//    Message message=new Message();
//    message.what= MSG_NET_CALL_BACK_SUCCESS;
//    message.obj=data;
//    mainHandler.sendMessage(message);
//  }
//
//  @Override public void onFailure(String msg) {
//    Message message=new Message();
//    message.what=MSG_NET_CALL_BACK_FAIL;
//    message.obj=msg;
//    mainHandler.sendMessage(message);
//  }
//}
//
//class DetailPagerAdapter extends PagerAdapter{
//  private List<EnvironmentDetail>  mData;
//  private Context mContext;
//  public DetailPagerAdapter(Context context,List<EnvironmentDetail> data){
//    mContext = context;
//    mData = data;
//  }
//
//
//
//  @Override
//  public Object instantiateItem(ViewGroup collection, int position) {
//    ImageView imageView = new ImageView(mContext);
//    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//    try {
//      BitmapFactory.Options options = new BitmapFactory.Options();
//      options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
//      Bitmap bitmap = null;
//      bitmap = BitmapFactory.decodeFile(mData.get(position).getImgPath(), options);
//      options.inSampleSize = caculateInSampleSize(options,360,180);
//      options.inPreferredConfig = Bitmap.Config.ARGB_4444;/* 下面两个字段需要组合使用 */
//      options.inPurgeable = true;
//      options.inInputShareable = true;
//      options.inJustDecodeBounds = false;
//      bitmap = BitmapFactory.decodeFile(mData.get(position).getImgPath(), options);
//      imageView.setImageBitmap(bitmap);
//      collection.addView(imageView);
//    } catch (OutOfMemoryError e) {
//      Log.e("instantiateItem", "OutOfMemoryError");
//      e.printStackTrace();
//      imageView.setImageResource(R.drawable.ic_delete);
//    }
//    return imageView;
//  }
//
//
//
//
//  @Override public int getCount() {
//    return mData!=null? mData.size():0;
//  }
//
//  @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//    return view == object;
//  }
//
//  @Override
//  public void destroyItem(ViewGroup collection, int position, Object view) {
//    collection.removeView((View) view);
//  }
//
//  private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//    int width = options.outWidth;
//    int height = options.outHeight;
//    int inSampleSize = 1;
//    if (width > reqWidth || height > reqHeight) {
//      int widthRadio = Math.round(width * 1.0f / reqWidth);
//      int heightRadio = Math.round(height * 1.0f / reqHeight);
//      inSampleSize = Math.max(widthRadio, heightRadio);
//    }    return inSampleSize;
//  }
//}
//
//abstract class CounterDownUtil {
//  private long totalTime;
//  private static final long period = 1000;
//  private long elapsedTime;
//  private Timer mTimer;
//  private TimerTask mTimerTask;
//  private boolean isRunning;
//
//
//  public CounterDownUtil(long totalTimeInMillis) {
//    totalTime = totalTimeInMillis;
//    elapsedTime = 0;
//    isRunning = false;
//  }
//
//  public void fire(){
//    if (mTimer == null) {
//      mTimer = new Timer();
//    }
//
//    if (mTimerTask == null) {
//      mTimerTask = new TimerTask() {
//        @Override
//        public void run() {
//          elapsedTime += period;
//          if (elapsedTime > totalTime) {
//            onFinish();
//            extinguish();
//          } else {
//            onTick(totalTime - elapsedTime);
//            isRunning = true;
//          }
//        }
//      };
//    }
//    mTimer.schedule(mTimerTask, 0, period);
//  }
//
//  public boolean isRunning(){
//    return isRunning;
//  }
//
//  public long getElapsedTime(){
//    return elapsedTime;
//  }
//
//  public long getRemainTime(){
//    return totalTime - elapsedTime;
//  }
//
//  public void extinguish(){
//    if (mTimerTask != null && isRunning) {
//      mTimerTask.cancel();
//      mTimer.cancel();
//      mTimerTask = null;
//      mTimer = null;
//      isRunning = false;
//    }
//  }
//
//  public abstract void onTick(long millisUntilFinished);
//
//  public abstract void onFinish();
//
//
//}
//

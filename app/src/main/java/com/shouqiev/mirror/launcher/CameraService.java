package com.shouqiev.mirror.launcher;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.shouqiev.mirror.launcher.GofunViews.CameraWindow;
import com.shouqiev.mirror.launcher.event.CameraEvent;
import com.shouqiev.mirror.launcher.utils.SnapUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CameraService extends Service {

    /**
     * 自定义窗口
     */
    private CameraWindow myWindow;
    /**
     * 窗口管理者
     */
    private WindowManager mWindowManager;
    /**
     * 窗口布局参数
     */
    private LayoutParams Params;
    private boolean isAdd = false;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (!myWindow.isAttachedToWindow()) {
                if (!isAdd) {
                    mWindowManager.addView(myWindow, Params);
                    isAdd = true;
                }
            }
            super.handleMessage(msg);
        }
    };

    public void disMiss() {
        if (myWindow.isAttachedToWindow()) {
            mWindowManager.removeView(myWindow);
            isAdd = false;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //目前只支持5.0
        showWindow();
    }

    private void showWindow() {
        //创建MyWindow的实例
        myWindow = new CameraWindow(getApplicationContext(), this);
        //窗口管理者
        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        Params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT,
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        Params.gravity = Gravity.LEFT | Gravity.TOP;
        //布局的宽和高
        Params.width = 1;
        Params.height = 1;


        myWindow.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        Params.x = (int) event.getRawX() - myWindow.getWidth() / 2;
                        Params.y = (int) event.getRawY() - myWindow.getHeight() / 2;
                        //更新布局位置
                        mWindowManager.updateViewLayout(myWindow, Params);
                        break;
                }
                return false;
            }
        });
        Message message = new Message();
        handler.sendMessageDelayed(message, 1000);
    }

    public void takePic() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myWindow.takePic(new SnapUtil.SnapListener() {
                    @Override
                    public void onFail(String errorCode) {
                        EventBus.getDefault().post(new CameraEvent(null, errorCode));
                        CameraService.this.stopSelf();
                    }

                    @Override
                    public void onSuccess(Bitmap bitmap, String path) {
                        EventBus.getDefault().post(new CameraEvent(path, null));
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        CameraService.this.stopSelf();
                    }
                });
            }
        }, 500);
    }

    /**
     * @return 获取桌面(Launcher)的包名
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : resolveInfo) {
            names.add(info.activityInfo.packageName);
        }
        return names;
    }

    /**
     * @return 判断当前是否是桌面
     */
    public boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        List<String> strs = getHomes();
        if (strs != null && strs.size() > 0) {
            return strs.contains(rti.get(0).topActivity.getPackageName());
        } else {
            return false;
        }
    }

}

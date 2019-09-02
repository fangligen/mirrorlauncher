package com.shouqiev.mirror.launcher.GofunViews;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.shouqiev.mirror.launcher.CameraService;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.event.CameraEvent;
import com.shouqiev.mirror.launcher.utils.SnapUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraWindow extends LinearLayout implements SurfaceTextureListener {
    /**
     * 相机类
     */
    private Camera myCamera;
    private Context context;
    private TextureView textureView;
    private CameraService myService;
    private WindowManager mWindowManager;
    SnapUtil.SnapListener listener;

    public CameraWindow(Context context, CameraService s) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.camera_window, this);
        this.context = context;
        myService = s;
        initView();
    }

    private void initView() {
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        mWindowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
    }

    public void takePic(SnapUtil.SnapListener l) {
        listener = l;
        myCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.e("", "");
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                String path = getSDPath() + "/" + System.currentTimeMillis() + ".jpg";
                File f = new File(path);
                saveBitmap(bitmap, f);
            }
        });
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        if (myCamera == null) {
            // 创建Camera实例
            try {
                myCamera = Camera.open();
                // 设置预览在textureView上
                myCamera.setPreviewTexture(surface);
                myCamera.setDisplayOrientation(SetDegree(CameraWindow.this));
                // 开始预览
                myCamera.startPreview();
                myService.takePic();
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new CameraEvent(null,e.getMessage()));
            }

        }
    }

    private int SetDegree(CameraWindow myWindow) {
        // 获得手机的方向
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        myCamera.stopPreview(); //停止预览
        myCamera.release();     // 释放相机资源
        myCamera = null;
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }


    public boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) {
            if (null != listener) {
                listener.onFail("bitmap == nul");
            }
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            if (null != listener) {
                listener.onSuccess(bitmap, file.getPath());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onFail("bitmap == nul");
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            myService.disMiss();
        }
        return false;
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}

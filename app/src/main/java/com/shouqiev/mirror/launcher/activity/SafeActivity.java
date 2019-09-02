package com.shouqiev.mirror.launcher.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.hikvision.mirror.HikMirrorApi;
import com.hikvision.mirror.interfaces.PreviewCallback;
import com.shouqiev.mirror.launcher.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;


public class SafeActivity extends Activity {

    private final static String TAG = "SafeActivity";
    private Button btn;
    private SurfaceView surfaceView;//预览摄像头
    private HikMirrorApi mirrorApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.safe_activity);
        btn = findViewById(R.id.btn);
        surfaceView = findViewById(R.id.main_surface_view);
        btn.setText("关闭");
        mirrorApi = HikMirrorApi.getInstance();
//        mirrorApi.init(this);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
//                wakeLock.setReferenceCounted(false);
//                wakeLock.release();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ("打开".equals(btn.getText().toString())) {
                    btn.setText("关闭");
                    HikMirrorApi.getInstance().setDBAVolumeEnable(true);
                    HikMirrorApi.getInstance().setDBAEnable(true);
                } else {
                    btn.setText("打开");
                    HikMirrorApi.getInstance().setDBAVolumeEnable(false);
                    HikMirrorApi.getInstance().setDBAEnable(false);
                }
            }
        });
        initSurfaceView(surfaceView, 2);
    }

    private void initSurfaceView(SurfaceView surfaceView, final int chanNo) {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surface one is valid!!!");
                startOrStopPreView(chanNo, holder, true);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                startOrStopPreView(chanNo, null, false);
            }
        });
    }

    private void startOrStopPreView(int chanNo, SurfaceHolder holder, boolean isStart) {
        if (isStart) {
            mirrorApi.startPreview(chanNo, holder);
        } else {
            mirrorApi.stopPreview(chanNo);
        }
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, SafeActivity.class));
    }

}

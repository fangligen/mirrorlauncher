package com.shouqiev.mirror.launcher.GofunViews;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
public class MirrorImageView extends View {
    private Context mContext;
    private Bitmap mBitmap;
    private int mBitmapWidth, mBitmapHeight;

    public MirrorImageView(Context context) {
        this(context, null);
    }

    public MirrorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MirrorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext =  context;
    }
    public void setImageResource(int resId) {
//        mBitmap = compressMatrix(BitmapFactory.decodeResource(getResources(), resId));
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
    }

    public void setImageDrawable(Drawable drawable){
        try{
            if(drawable instanceof BitmapDrawable){
                BitmapDrawable bd = (BitmapDrawable) drawable;
//                mBitmap = compressMatrix(bd.getBitmap());
                mBitmap = bd.getBitmap();
                mBitmapWidth = mBitmap.getWidth();
                mBitmapHeight = mBitmap.getHeight();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


//    private Bitmap compressMatrix(Bitmap sourceImg) {
//        Matrix matrix = new Matrix();
//        if(sourceImg.getWidth()> this.getWidth()){
//            float multiple = (float)this.getWidth()/sourceImg.getWidth();
//            matrix.setScale(multiple, multiple);
//            return Bitmap.createBitmap(sourceImg, 0, 0, sourceImg.getWidth(), sourceImg.getHeight(), matrix, true);
//        }
//        return sourceImg;
//    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = null;
        if(mBitmap!=null){
            super.onDraw(canvas);
            float scale = 1.0f;
            if(mBitmap.getWidth()> this.getWidth()){
                scale = (float)this.getWidth()/mBitmapWidth;
                matrix = new Matrix();
                matrix.setScale(scale,scale);
                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmapWidth, mBitmapHeight, matrix, true);
                mBitmapWidth = mBitmap.getWidth();
                mBitmapHeight = mBitmap.getHeight();
            }
            //获取镜像图片
            matrix = new Matrix();
            matrix.postScale(1, -1);//图片反转（轴对称）
            Bitmap mirrorBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmapWidth, mBitmapHeight, matrix, true);
            //设置渲染梯度
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setAlpha(50);
            canvas.drawBitmap(mirrorBitmap, null, new RectF(0, mBitmapHeight-(mBitmapHeight/5), mBitmapWidth, mBitmapHeight * 2-(mBitmapHeight/5)), paint);
            canvas.drawBitmap(mBitmap ,0, 0, null);
        }
    }
}

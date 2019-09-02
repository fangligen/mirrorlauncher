package com.shouqiev.mirror.launcher.GofunViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class GofunStyleTextView extends TextView {
    final static String FONT_PATH = "fonts/bebas.ttf";
    private Context mContext;

    public GofunStyleTextView(Context context) {
        super(context);
        mContext = context;
        setTextStyle();
    }


    public GofunStyleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setTextStyle();
    }

    public GofunStyleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setTextStyle();
    }

    public GofunStyleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setTextStyle();
    }

    private void setTextStyle(){
        try {
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), FONT_PATH);
            setTypeface(tf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

package com.shouqiev.mirror.launcher.fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.BaseFragment;

/**
 * Created by zhengyonghui on 2018/9/4.
 */
public class HomeFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        return view;
    }
}


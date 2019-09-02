package com.shouqiev.mirror.launcher.fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shouqiev.mirror.launcher.R;
/**
 * Created by zhengyonghui on 2018/9/4.
 */
public class NavFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav, null);
        return view;
    }
}

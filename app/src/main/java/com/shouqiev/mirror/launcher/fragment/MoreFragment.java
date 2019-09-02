package com.shouqiev.mirror.launcher.fragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shouqiev.mirror.launcher.GofunViews.MirrorImageView;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.adapter.AppListAdapter;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import com.shouqiev.mirror.launcher.model.AppInfo;
import com.shouqiev.mirror.launcher.utils.AppInfoProvider;
import com.shouqiev.mirror.launcher.utils.BlurBitmapUtil;
import com.shouqiev.mirror.launcher.utils.LauncherUtil;
import com.shouqiev.mirror.launcher.utils.LogUtil;

import java.util.List;

import jp.wasabeef.blurry.Blurry;
/**
 * Created by zhengyonghui on 2018/9/4.
 */
public class MoreFragment extends BaseFragment implements AppListAdapter.OnItemClickListener, AppListAdapter.OnItemLongClickListener{


    private static final String TAG = "MoreFragment";

    private static final int RECYCLER_VIEW_SPAN_COUNT = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    private List<AppInfo> mAllAppList;
    private RecyclerView mRecyclerView;
    private Toast mToast;

    private AppListAdapter mAdapter;

    private LinearLayout next_item_layout;
    private TextView next_item_count;
    private ImageView next_item_gauss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        initView(view);
        getData();
        return view;
    }
    private void initView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.more_recycler_view);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        next_item_layout = (LinearLayout)view.findViewById(R.id.next_item_layout);
        next_item_count = (TextView)view.findViewById(R.id.next_item_count);
        next_item_gauss = (ImageView) view.findViewById(R.id.next_item_gauss);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(),StaggeredGridLayoutManager.HORIZONTAL,false));
//        getActivity().getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (!(v instanceof RecyclerView)) {
//                        boolean isDeleting = mAdapter.isDeleting();
//                        if (isDeleting) {
//                            mAdapter.setDeleting(false);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//                return false;
//            }
//        });
        mAdapter = new AppListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
//        mAdapter.setOnItemLongClickListener(this);
        ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int nextIn = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if(position!=nextIn){
                    nextIn = position;
                    flushPostionView(nextIn);
                }
            }
        });
    }




    private void flushPostionView(int position){
        LinearLayoutManager manager = ((LinearLayoutManager)mRecyclerView.getLayoutManager());
        if(position<0){
            position = manager.findLastVisibleItemPosition();
        }
        try{
//        Blurry.with(getActivity()).capture(manager.findViewByPosition(position)).into(next_item_gauss);
            next_item_gauss.setImageBitmap(BlurBitmapUtil.getViewBitmap(manager.findViewByPosition(position)));
        }catch (Exception e){
            e.printStackTrace();
        }
        next_item_count.setText(Html.fromHtml("( <font color=#3df589>"+((position)%mAdapter.getAppCount()+1)+"</font>/"+mAdapter.getAppCount()+")"));
        if(next_item_layout.getVisibility() == View.GONE){
            next_item_layout.setVisibility(View.VISIBLE);
        }
    }

    public void getData() {
        AppInfoProvider.getInstance(getActivity()).queryAppInfo(new AppInfoProvider.QueryAppListener() {
            @Override
            public void onQuery(List<AppInfo> applist) {
                mAllAppList = applist;
                if(mAllAppList.size()>4){
                    mAdapter.setData(mAllAppList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onItemClick(int position, AppInfo appInfo) {
        if (mAdapter == null || appInfo == null) {
            return;
        }
        if (!mAdapter.isDeleting()) {
            LauncherUtil.startActivitySafely(appInfo.getIntent());
        } else if (appInfo.isSystem()) {
            showToast(getResources().getString(R.string.app_cannot_uninstall));
        } else {
            String strUri = "package:" + appInfo.getPackageName();
            Uri uri = Uri.parse(strUri);
            Intent deleteIntent = new Intent();
            deleteIntent.setAction(Intent.ACTION_DELETE);
            deleteIntent.setData(uri);
            startActivityForResult(deleteIntent, 0);
        }
    }
    @Override
    public void onItemLongClick(int position, AppInfo appInfo) {
        if (mAdapter != null) {
            mAdapter.setDeleting(!mAdapter.isDeleting());
            mAdapter.notifyDataSetChanged();
        }
    }
    public void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }
}

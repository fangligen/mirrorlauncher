package com.shouqiev.mirror.launcher.fragment.Car;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ImageView;
import com.shouqiev.mirror.launcher.GofunAdapters.CoverFlowDetailAdapter;
import com.shouqiev.mirror.launcher.GofunViews.FeatureCoverFlow;
import com.shouqiev.mirror.launcher.GofunViews.GofunVideoView;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.activity.MainActivity;
import com.shouqiev.mirror.launcher.base.BaseFragment;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceConstant;
import com.shouqiev.mirror.launcher.params.CommonParams;
import com.shouqiev.mirror.launcher.utils.PreferencesUtils;
import com.shouqiev.mirror.launcher.utils.TutorialUtil;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
import com.shouqiev.mirror.launcher.voice.view.IGFVoiceViewListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zhengyonghui on 2018/9/4.
 */
public class CarTutorialFragment extends BaseFragment{
  private final String CAR_FRAGMENT_PREFERENCE = "CAR_FRAGMENT_PREFERENCE";
  private final String IS_DANMU_ON = "IS_DANMU_ON";
  private FeatureCoverFlow mCoverFlow;
  private Context mContext;


  public GofunVideoView videoView;
  private Button pause;
  private CheckBox danmuCheckBox;
  private ImageView imageCover;
  PreferencesUtils preferencesUtils;

  boolean isDanmuOn;
  //private final String[] textOption = {
  //    "早看到这教程就好了，比客服讲的好多了！", "666~后视镜功能这么多", "教程真香！", "灯光控制那里建议讲细点", "这个后视镜能用来看电影吗？",
  //    "\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80", "好的好的，你讲的这些我都知道了", "我要给GoFun刷一波火箭"
  //};


  Handler handler = new Handler();
  private CoverFlowDetailAdapter mAdapter;
  public ArrayList<CoverFlowDetailAdapter.CardEntity> mData = new ArrayList<>(0);
  private View mView;
  private String defaultPath = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/启动.mp4";
  private int currentEvent = -1;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    mView = inflater.inflate(R.layout.fragment_car_tutorial, null);
    mContext = getActivity();
    //preferencesUtils = PreferencesUtils.getInstance(mContext, CAR_FRAGMENT_PREFERENCE);
    //isDanmuOn = preferencesUtils.getBoolean(IS_DANMU_ON, true);
    return mView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
    addListeners();
    setData();
    //addDanmu();

    videoView.setVideoPath(defaultPath);
    handler.post(new Runnable() {
      @Override public void run() {
        Bitmap bitmap = getNailFrame(defaultPath);
        imageCover.setImageBitmap(bitmap);
        imageCover.setVisibility(View.VISIBLE);
      }
    });

  }

  private void setData() {
    String[] titles = TutorialUtil.getTutorialTitlesWithId(getActivity(),R.drawable.gofun_car_type_ariza5e_white);
    if (titles!=null) {
      for (String title:titles) {
        mData.add(new CoverFlowDetailAdapter.CardEntity(R.drawable.gofun_car_type_ariza5e_white, title,title+".mp4"));
      }
    }
    mAdapter = new CoverFlowDetailAdapter(mContext);
    mAdapter.setData(mData);
    mCoverFlow.clearCache();
    mCoverFlow.setReflectionDisable();
    mCoverFlow.setAdapter(mAdapter);
  }



  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      if (videoView != null) {
        if (videoView.isPlaying()) {
          videoView.pause();
        }
      }

    } else {
      if (mAdapter!= null && mData!= null) {
        setData();
      }
    }
  }

  private void addListeners() {
    pause.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (videoView != null) {
          if (videoView.isPlaying()) {
            videoView.pause();
            pause.setBackgroundResource(R.drawable.video_play_icon);
          } else {
            videoView.start();
            pause.setBackgroundResource(R.drawable.video_pause_icon);
          }
        }
      }
    });

    //back.setOnClickListener(new View.OnClickListener() {
    //  @Override public void onClick(View view) {
    //    CarFragment.carFragment.switchFragment(MainActivity.CAR_FRAGMENT_TAG, 0);
    //    videoView.pause();
    //    videoView.seekTo(1);
    //  }
    //});

    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override public void onCompletion(MediaPlayer mp) {
        pause.setBackgroundResource(R.drawable.video_pause_icon);
      }
    });

    videoView.setPlayStatusListener(new GofunVideoView.PlayStatusListener() {
      @Override public void onPause() {
        pause.setBackgroundResource(R.drawable.video_play_icon);
      }

      @Override public void onStart() {
        pause.setBackgroundResource(R.drawable.video_pause_icon);
        imageCover.setVisibility(View.GONE);
      }
    });

    mCoverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
      @Override public void onScrolledToPosition(int position) {
        String currentPath = CommonParams.VIDEO_ROOT_PATH  +TutorialUtil.getTutorialDirectoryName(mData.get(0).imageResId) + File.separator+
            mData.get(position % mData.size()).videoName;
        if (videoView.isPlaying()) {
          imageCover.setVisibility(View.GONE);
          videoView.setVideoPath(currentPath);
          videoView.start();
        } else {
          videoView.setVideoPath(currentPath);
          final Bitmap bitmap = getNailFrame(currentPath);
          handler.post(new Runnable() {
            @Override public void run() {
              imageCover.setImageBitmap(bitmap);
              imageCover.setVisibility(View.VISIBLE);
            }
          });
        }
      }

      @Override public void onScrolling() {

      }
    });

    mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        videoView.setVideoPath(CommonParams.VIDEO_ROOT_PATH +TutorialUtil.getTutorialDirectoryName(mData.get(0).imageResId) + File.separator+
            mData.get(position % mData.size()).videoName);
        videoView.start();
      }
    });

    //danmuCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    //  @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    //    preferencesUtils.putBoolean(IS_DANMU_ON, isChecked);
    //    mDanMuContainerBroadcast.hideAllDanMuView(!isChecked);
    //  }
    //});
  }

  private Bitmap getNailFrame(String videoUrl){
    return ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Images.Thumbnails.MINI_KIND);
  }


  private void initView(View view) {
    imageCover = view.findViewById(R.id.tutorial_video_cover);
    mCoverFlow = view.findViewById(R.id.coverflow_detail);
    videoView = view.findViewById(R.id.tutorial_video_view);
    //back = view.findViewById(R.id.back_forward);
    pause = view.findViewById(R.id.pause_video);
    //danmuCheckBox = view.findViewById(R.id.danmu_check_box);
    //danmuCheckBox.setChecked(isDanmuOn);
    //mDanMuContainerBroadcast = view.findViewById(R.id.danmaku_container_broadcast);
    //mDanMuContainerBroadcast.prepare();

  }

  //Runnable runnable = new Runnable() {
  //  @Override public void run() {
  //    DanMuModel danMuView = new DanMuModel();
  //    int[] colorOption = { R.color.text_order_green,   R.color.white, R.color.color1, R.color.color2, R.color.color3 };
  //    danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
  //    danMuView.setPriority(DanMuModel.NORMAL);
  //    danMuView.marginLeft = DimensionUtil.dpToPx(mContext, new Random().nextInt(40));
  //    // 显示的文本内容
  //    danMuView.textSize = DimensionUtil.spToPx(mContext, 5 + new Random().nextInt(20));
  //    danMuView.textColor = ContextCompat.getColor(mContext, colorOption[new Random().nextInt(colorOption.length)]);
  //    danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, new Random().nextInt(15));
  //    danMuView.text = textOption[new Random().nextInt(textOption.length)];
  //    // 弹幕文本背景
  //    //danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
  //    danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
  //    danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
  //    danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
  //    danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);
  //    mDanMuContainerBroadcast.add(danMuView);
  //    handler.postDelayed(runnable, new Random().nextInt(3) * 500 + 1000);
  //  }
  //};

  //private void addDanmu() {
  //  handler.postDelayed(runnable, 1500);
  //  mDanMuContainerBroadcast.hideAllDanMuView(!isDanmuOn);
  //}

  @Override public void onDestroy() {
    super.onDestroy();
    if (videoView != null) {
      videoView.stopPlayback();
      //handler.removeCallbacks(runnable);
    }
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void Event(VoiceEvent event) {
    currentEvent = event.status;
  }

  //private int getDataPosition(String keyword){
  //  int position = mCoverFlow.getScrollPosition();
  //  for (int i = 0; i< mData.size(); i++) {
  //    if (mData.get(position).videoName.contains(keyword)) {
  //      return position;
  //    }
  //  }
  //  return 0;
  //}

  @Override public void onResume() {
    super.onResume();
    //<item>车门</item>
    //<item>充电</item>
    //<item>灯光</item>
    //<item>空调</item>
    //<item>启动</item>
    //<item>雨刷</item>
    if (currentEvent >= VoiceEvent.VOICE_TUTORIAL_DOOR && currentEvent <= VoiceEvent.VOICE_TUTORIAL_WIPER) {
      String path = defaultPath;
      if (currentEvent == VoiceEvent.VOICE_TUTORIAL_DOOR) {
        path = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/车门.mp4";
      } else if(currentEvent == VoiceEvent.VOICE_TUTORIAL_CHARGE) {
        path = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/充电.mp4";
      } else if(currentEvent == VoiceEvent.VOICE_TUTORIAL_LIGHT) {
        path = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/灯光.mp4";
      } else if(currentEvent == VoiceEvent.VOICE_TUTORIAL_AIR_CONDITIONER) {
        path = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/空调.mp4";
      } else if(currentEvent == VoiceEvent.VOICE_TUTORIAL_START_ENGINE) {
        path = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/启动.mp4";
      } else if(currentEvent == VoiceEvent.VOICE_TUTORIAL_WIPER) {
        path = CommonParams.VIDEO_ROOT_PATH + CommonParams.CAR_TYPE_TYPE_ARIZA5E + "/雨刷.mp4";
      }
      videoView.setVideoPath(path);
      videoView.start();
    }


    if(currentEvent == VoiceEvent.VOICE_TUTORIAL_START_PLAY) {
      if(!videoView.isPlaying()) {
        videoView.start();
      }
    } else if(currentEvent == VoiceEvent.VOICE_TUTORIAL_STOP_PLAY) {
      if(videoView.isPlaying()) {
        videoView.pause();
      }
    }

    currentEvent = -1;
  }
}


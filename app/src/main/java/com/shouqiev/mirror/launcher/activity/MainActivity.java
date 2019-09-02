package com.shouqiev.mirror.launcher.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.gofun.voice.manager.GFVoiceManager;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.VoiceServices;
import com.shouqiev.mirror.launcher.event.VoiceEvent;
import com.shouqiev.mirror.launcher.fragment.Car.CarTutorialFragment;
import com.shouqiev.mirror.launcher.fragment.MoreFragment;
import com.shouqiev.mirror.launcher.fragment.map.entity.VoiceConstant;
import com.shouqiev.mirror.launcher.fragment.map.util.AMapUtil;
import com.shouqiev.mirror.launcher.fragment.map.GoFunMapContainerFragment;
import com.shouqiev.mirror.launcher.fragment.map.GoFunRouteFragment;
import com.shouqiev.mirror.launcher.fragment.map.dao.ParkingBean;
import com.shouqiev.mirror.launcher.utils.KeyUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity implements View.OnClickListener {

  public static final String CAR_FRAGMENT_TAG = "TAG_CAR";
  public static final String HOME_FRAGMENT_TAG = "TAG_HOME";
  public static final String MORE_FRAGMENT_TAG = "TAG_MORE";
  public static final String NAV_FRAGMENT_TAG = "TAG_NAV";
  public static final String TUTORIAL_FRAGMENT_TAG = "TAG_TUTORIAL";
  private FragmentManager fragmentManager;

  private CarTutorialFragment carTutorialFragment;
  private GoFunRouteFragment homeFragment;
  private MoreFragment moreFragment;
  private GoFunMapContainerFragment navFragment;

  private ImageView homeIcon;
  private ImageView carIcon;
  private ImageView navIcon;
  private ImageView moreIcon;
  private ImageView voiceIcon;

  private GestureDetector gestureDetector;
  private GestureDetector.OnGestureListener onSlideGestureListener = null;
  private int currentEvent = -1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    KeyUtil.hideBottomUIMenu(this);
    setContentView(R.layout.activity_main);
    onSlideGestureListener = new OnSlideGestureListener();
    gestureDetector = new GestureDetector(this, onSlideGestureListener);
    initView();
    fragmentManager = getFragmentManager();
    setTabSelection(R.id.main_imgv_home);
    Intent intent = getIntent();
    if (intent != null) {
      ParkingBean parkingBean = (ParkingBean) intent.getSerializableExtra("park");
      if (parkingBean != null) {
        startNavi(parkingBean);
      }
    }
    VoiceServices.startService(this);
    EventBus.getDefault().register(this);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    //    return gestureDetector.onTouchEvent(event);
    return super.onTouchEvent(event);
  }

  @Override protected void onRestart() {
    KeyUtil.hideBottomUIMenu(this);
    super.onRestart();
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {
    KeyUtil.hideBottomUIMenu(this);
    super.onWindowFocusChanged(hasFocus);
  }

  private void initView() {
    homeIcon = (ImageView) findViewById(R.id.main_imgv_home);
    carIcon = (ImageView) findViewById(R.id.main_imgv_car);
    navIcon = (ImageView) findViewById(R.id.main_imgv_nav);
    moreIcon = (ImageView) findViewById(R.id.main_imgv_more);
    voiceIcon = (ImageView) findViewById(R.id.main_icon_voice);

    homeIcon.setOnClickListener(this);
    carIcon.setOnClickListener(this);
    navIcon.setOnClickListener(this);
    moreIcon.setOnClickListener(this);
    voiceIcon.setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.main_imgv_home:
      case R.id.main_imgv_nav:
      case R.id.main_imgv_car:
      case R.id.main_imgv_more:
        setTabSelection(view.getId());
        break;
      case R.id.main_icon_voice:
        startVoiceActivity();
        break;
    }
  }

  private void defaultToolIcon() {
    homeIcon.setImageResource(R.mipmap.home_default);
    carIcon.setImageResource(R.mipmap.icon_car_default);
    navIcon.setImageResource(R.mipmap.nav_default);
    moreIcon.setImageResource(R.mipmap.more_default);
  }

  public void setTabSelection(int viewId) {
    if (getCurrentFocus() != null) {
      AMapUtil.hideKeyboard(this);
    }
    defaultToolIcon();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    hideFragments(transaction);
    switch (viewId) {
      case R.id.main_imgv_home:
        homeIcon.setImageResource(R.mipmap.home_pressed);
        if (homeFragment == null) {
          homeFragment = new GoFunRouteFragment();
          transaction.add(R.id.main_content, homeFragment, HOME_FRAGMENT_TAG);
        } else {
          transaction.show(homeFragment);
        }
        break;
      case R.id.main_imgv_nav:
        navIcon.setImageResource(R.mipmap.nav_pressed);
        if (navFragment == null) {
          navFragment = new GoFunMapContainerFragment();
          transaction.add(R.id.main_content, navFragment, NAV_FRAGMENT_TAG);
        } else {
          transaction.show(navFragment);
        }
        break;
      case R.id.main_imgv_car:
        carIcon.setImageResource(R.mipmap.car_pressed);
        if (carTutorialFragment == null) {
          carTutorialFragment = new CarTutorialFragment();
          transaction.add(R.id.main_content, carTutorialFragment, CAR_FRAGMENT_TAG);
        } else {
            transaction.show(carTutorialFragment);
        }

        break;
      case R.id.main_imgv_more:
        moreIcon.setImageResource(R.mipmap.more_pressed);
        if (moreFragment == null) {
          moreFragment = new MoreFragment();
          transaction.add(R.id.main_content, moreFragment, MORE_FRAGMENT_TAG);
        } else {
          transaction.show(moreFragment);
        }
        break;
    }

    transaction.commit();
  }

  private void hideFragments(FragmentTransaction transaction) {
    if (homeFragment != null) {
      transaction.hide(homeFragment);
    }
    if (navFragment != null) {
      transaction.hide(navFragment);
    }
    if (moreFragment != null) {
      transaction.hide(moreFragment);
    }
    if (carTutorialFragment != null) {
      transaction.hide(carTutorialFragment);
    }
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    if (intent != null) {
      ParkingBean parkingBean = (ParkingBean) intent.getSerializableExtra("park");
      if (parkingBean != null) {
        startNavi(parkingBean);
      }
    }
  }

  /**
   * 切换导航
   */
  public void startNavi(ParkingBean parkingBean) {
    defaultToolIcon();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    hideFragments(transaction);
    if (navFragment == null) {
      navFragment = GoFunMapContainerFragment.newInstance(true, parkingBean);
      transaction.add(R.id.main_content, navFragment);
    } else {
      navFragment.switchFragment(true, parkingBean, null);
      transaction.show(navFragment);
    }
    navIcon.setImageResource(R.mipmap.nav_pressed);
    transaction.commit();
  }

  public void startVoiceActivity() {
    if (getCurrentFocus() != null) {
      AMapUtil.hideKeyboard(this);
    }
    overridePendingTransition(R.anim.move_left_in, R.anim.move_right_out);
    VoiceServices.getService().wakeup();
  }

  private class OnSlideGestureListener implements GestureDetector.OnGestureListener {
    @Override public boolean onDown(MotionEvent motionEvent) {
      return false;
    }

    @Override public void onShowPress(MotionEvent motionEvent) {
    }

    @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
      return false;
    }

    @Override public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
      return false;
    }

    @Override public void onLongPress(MotionEvent motionEvent) {
    }

    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      // 参数解释：
      // e1：第1个ACTION_DOWN MotionEvent
      // e2：最后一个ACTION_MOVE MotionEvent
      // velocityX：X轴上的移动速度，像素/秒
      // velocityY：Y轴上的移动速度，像素/秒
      // 触发条件 ：
      // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
      if ((e1 == null) || (e2 == null)) {
        return false;
      }
      int FLING_MIN_DISTANCE = 100;
      int FLING_MIN_VELOCITY = 100;
      if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
        // 向左滑动
      } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
        //此处也可以加入对滑动速度的要求
        //			             && Math.abs(velocityX) > FLING_MIN_VELOCITY
          ) {
        // 向右滑动
        startVoiceActivity();
      }
      return false;
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void Event(VoiceEvent event) {
    currentEvent = event.status;
  }

  @Override protected void onResume() {
    super.onResume();
    /*** 教程事件 ***/
    if (currentEvent >= VoiceEvent.VOICE_TUTORIAL_DOOR && currentEvent <= VoiceEvent.VOICE_TUTORIAL_START_PLAY) {
      setTabSelection(R.id.main_imgv_car);
      new Handler(){}.postDelayed(new Runnable() {
        @Override public void run() {
          EventBus.getDefault().post(new VoiceEvent(currentEvent));
        }
      },500);
    }

    if (currentEvent == VoiceEvent.VOICE_ENTER_HOME) {
      homeIcon.performClick();
    } else if(currentEvent == VoiceEvent.VOICE_ENTER_NAVI) {
      navIcon.performClick();
    } else if(currentEvent == VoiceEvent.VOICE_ENTER_CAR) {
      carIcon.performClick();
    } else if(currentEvent == VoiceEvent.VOICE_ENTER_MORE) {
      moreIcon.performClick();
    }
    currentEvent = -1;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }
}

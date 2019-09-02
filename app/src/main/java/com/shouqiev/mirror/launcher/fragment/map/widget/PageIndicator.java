package com.shouqiev.mirror.launcher.fragment.map.widget;

public interface PageIndicator {
  void InitIndicatorItems(int itemsNumber);

  void onPageSelected(int pageIndex);

  void onPageUnSelected(int pageIndex);
}

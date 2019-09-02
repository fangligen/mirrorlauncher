package com.shouqiev.mirror.launcher.fragment.map.widget;

import android.support.v7.widget.RecyclerView;
import com.shouqiev.mirror.launcher.fragment.map.search.OnPageChangeListener;
import java.util.ArrayList;
import java.util.List;

public class PagingScrollListener extends RecyclerView.OnScrollListener {

  private List<OnPageChangeListener> onPageChangeListenerList = new ArrayList<>();
  private PageIndicator pageIndicator;
  int scrollX = 0;
  boolean isAuto = false;
  int Target = 0;
  int currentPage = 0;
  int lastPage = 0;

  @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    if (newState == 0) {
      if (!isAuto) {
        int p = scrollX / recyclerView.getWidth();
        int offset = scrollX % recyclerView.getWidth();
        if (offset > recyclerView.getWidth() / 2) {
          p++;
        }
        Target = p * recyclerView.getWidth();
        isAuto = true;
        currentPage = p;
        if (pageIndicator != null) {
          pageIndicator.onPageUnSelected(lastPage);
          pageIndicator.onPageSelected(currentPage);
        }
        if (onPageChangeListenerList != null) {
          for (OnPageChangeListener listener : onPageChangeListenerList) {
            listener.onPageChanged(currentPage);
          }
        }
        recyclerView.smoothScrollBy(Target - scrollX, 0);
      }
    } else if (newState == 2) {
      isAuto = false;
      lastPage = currentPage;
    }
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    scrollX += dx;
  }

  public void setPageIndicator(PageIndicator pageIndicator) {
    this.pageIndicator = pageIndicator;
  }

  public void addOnPageChangeListener(OnPageChangeListener listener) {
    if (onPageChangeListenerList == null) {
      onPageChangeListenerList = new ArrayList();
    }
    onPageChangeListenerList.add(listener);
  }

  public void removeOnPageChangeListener(OnPageChangeListener listener) {
    if (onPageChangeListenerList != null) {
      onPageChangeListenerList.remove(listener);
    }
  }
}

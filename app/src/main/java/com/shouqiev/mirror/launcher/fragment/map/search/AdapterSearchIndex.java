package com.shouqiev.mirror.launcher.fragment.map.search;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.fragment.map.OnRecyclerItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoqian
 */
public class AdapterSearchIndex extends RecyclerView.Adapter<AdapterSearchIndex.ViewHolderIndex> {

  private List<String> strings = new ArrayList<>();
  private Context context;
  private int curPosition = -1, tmpPosition = -1;

  OnRecyclerItemClickListener<String> onRecyclerItemClickListener;

  public AdapterSearchIndex(Context context) {
    this.context = context;
  }

  public void setStrings(List<String> strings) {
    if (strings != null && !strings.isEmpty()) {
      curPosition=0;
      tmpPosition=0;
      notifyItemRangeRemoved(0,this.strings.size());
      this.strings = strings;
      notifyItemRangeChanged(0, strings.size());
    }
  }

  public void setCurPosition(int curPosition) {
    this.curPosition = curPosition;
    if (curPosition > -1) {
      notifyItemChanged(curPosition);
    }
    if (tmpPosition > -1) {
      notifyItemChanged(tmpPosition);
    }
    tmpPosition = curPosition;
  }

  public int getCurPosition() {
    return curPosition;
  }

  public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<String> onRecyclerItemClickListener) {
    this.onRecyclerItemClickListener = onRecyclerItemClickListener;
  }

  @Override public ViewHolderIndex onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_pager_index, parent, false);
    ViewHolderIndex viewHolderIndex = new ViewHolderIndex(view);
    return viewHolderIndex;
  }

  @Override public void onBindViewHolder(ViewHolderIndex holder, final int position) {
    holder.indexTxt.setText(strings.get(position));
    if (position == curPosition) {
      holder.indexTxt.setBackgroundResource(R.color.color_page_index_txt);
    } else {
      holder.indexTxt.setBackgroundColor(Color.TRANSPARENT);
    }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onRecyclerItemClickListener.onItemClick(strings.get(position), position);
      }
    });
  }

  @Override public int getItemCount() {
    return strings == null ? 0 : strings.size();
  }

  class ViewHolderIndex extends RecyclerView.ViewHolder {
    @BindView(R.id.item_pager_index_txt) TextView indexTxt;

    public ViewHolderIndex(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}

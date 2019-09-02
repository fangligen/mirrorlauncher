package com.shouqiev.mirror.launcher.voice.view;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shouqiev.mirror.launcher.R;
import com.shouqiev.mirror.launcher.base.VoiceBaseFragment;
import com.shouqiev.mirror.launcher.voice.model.BaseVoiceMsg;
public class VoiceFragmentAnswer extends VoiceBaseFragment {

  private TextView questionView,answerView;

  private BaseVoiceMsg voiceMsg;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.view_layout_answer, null);
    initView(view);
    return view;
  }

  private void initView(View view){
    questionView = (TextView)view.findViewById(R.id.txt_show_question);
    answerView = (TextView)view.findViewById(R.id.txt_show_answer);
    if(voiceMsg!=null){
      refreshView(voiceMsg);
    }
  }

  @Override
  public void clearView() {
  }
  @Override
  public void refreshView( BaseVoiceMsg viewMsg) {
    if(viewMsg!=null){
      this.voiceMsg = viewMsg;
      if(questionView!=null){
        questionView.setText(viewMsg.getVoiceMode().getQuestion());
        answerView.setText(viewMsg.getVoiceMode().getAnswer());
      }
    }
  }
  @Override
  public void updateViewBySelect(int num, String type) {
  }
  @Override
  public int getViewType() {
    return IGFViewType.VIEW_TYPE_ANSWER;
  }
}

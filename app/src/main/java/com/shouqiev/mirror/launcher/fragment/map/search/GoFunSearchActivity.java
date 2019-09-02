package com.shouqiev.mirror.launcher.fragment.map.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.shouqiev.mirror.launcher.activity.MainActivity;

/**
 * @author gaoqian
 */
public class GoFunSearchActivity extends Activity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    if (intent != null) {
      String keyWord = intent.getStringExtra("key");
      Intent searchIntent = new Intent(this, MainActivity.class);
      searchIntent.putExtra("keyWord", keyWord);
      startActivity(searchIntent);
      finish();
    }
  }
}

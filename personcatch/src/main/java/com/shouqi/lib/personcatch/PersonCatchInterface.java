package com.shouqi.lib.personcatch;
import java.util.List;
public interface PersonCatchInterface {
  public int status_success = 1;
  public int status_error_initfile = 2;
  public int status_error_none = 3;

  public void onResult(int type, List<Classifier.Recognition> results);


}


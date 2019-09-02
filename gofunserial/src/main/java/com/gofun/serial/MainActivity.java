package com.gofun.serial;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

  /*
   * Notifications from UsbService will be received here.
   */
  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      switch (intent.getAction()) {
        case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
          Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
          break;
        case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
          Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
          break;
        case UsbService.ACTION_NO_USB: // NO USB CONNECTED
          Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
          break;
        case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
          Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
          break;
        case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
          Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
          break;
      }
    }
  };
  private UsbService usbService;
  private TextView display;
  private EditText editText;
  private MyHandler mHandler;
  private final ServiceConnection usbConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName arg0, IBinder arg1) {
      usbService = ((UsbService.UsbBinder) arg1).getService();
      usbService.setHandler(mHandler);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      usbService = null;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mHandler = new MyHandler(this);

    display = (TextView) findViewById(R.id.textView1);
    editText = (EditText) findViewById(R.id.editText1);
    Button testButton = (Button) findViewById(R.id.buttonTest);
    testButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!editText.getText().toString().equals("")) {
          //CMD.sendCMD(CMD.CMD_TYPE.CMD_CAR_DETAIL)
          byte[] data = CMD.sendCMD(CMD.CMD_TYPE.CMD_CAR_DETAIL);
          String txt = ByteUtil.bytesToHexString(data);
          display.setText("");
          editText.setText(txt);
          if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write(data);
          }
        }
      }
    });

    Button sendButton = (Button) findViewById(R.id.buttonSend);
    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!editText.getText().toString().equals("")) {
          //CMD.sendCMD(CMD.CMD_TYPE.CMD_REGISTER)
          byte[] data = CMD.sendCMD(CMD.CMD_TYPE.CMD_REGISTER);
          String txt = ByteUtil.bytesToHexString(data);
          display.setText("");
          editText.setText(txt);
          if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write(data);
          }
        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    setFilters();  // Start listening notifications from UsbService
    startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(mUsbReceiver);
    unbindService(usbConnection);
  }

  private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
    if (!UsbService.SERVICE_CONNECTED) {
      Intent startService = new Intent(this, service);
      if (extras != null && !extras.isEmpty()) {
        Set<String> keys = extras.keySet();
        for (String key : keys) {
          String extra = extras.getString(key);
          startService.putExtra(key, extra);
        }
      }
      startService(startService);
    }
    Intent bindingIntent = new Intent(this, service);
    bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  private void setFilters() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
    filter.addAction(UsbService.ACTION_NO_USB);
    filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
    filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
    filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
    registerReceiver(mUsbReceiver, filter);
  }

  /*
   * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
   */
  private class MyHandler extends Handler {
    private final WeakReference<MainActivity> mActivity;

    public MyHandler(MainActivity activity) {
      mActivity = new WeakReference<>(activity);
    }

    @Override

    public void handleMessage(Message msg) {
      switch (msg.what) {
        case UsbService.MESSAGE_FROM_SERIAL_PORT:
          String data = (String) msg.obj;
          String cmd = data.substring(14,18);
          if (TextUtils.equals(cmd,"1100")){
            display.append("车机版本号：V3.0\n");
            display.append("车机号：3332\n");
            display.append("车型：奇瑞EQ\n");
          }

          if (TextUtils.equals(cmd,"1101")){
            int remainMiles = (15 + new Random().nextInt(180));
            DecimalFormat df = new DecimalFormat(".00");
            display.append("巡航里程："+  remainMiles +"KM\n");
            display.append("总里程："+ (1200 +new Random().nextInt(10000))+"KM\n");
            display.append("Acc："+ (new Random().nextBoolean() ?"开启":"关闭")+"\n");
            display.append("车门1："+ (new Random().nextBoolean() ?"开启":"关闭")+"\n");
            display.append("车门2："+ (new Random().nextBoolean() ?"开启":"关闭")+"\n");
            display.append("车门3："+ (new Random().nextBoolean() ?"开启":"关闭")+"\n");
            display.append("车门4："+ (new Random().nextBoolean() ?"开启":"关闭")+"\n");
            display.append("车门5："+ (new Random().nextBoolean() ?"开启":"关闭")+"\n");
            display.append("剩余电量："+ df.format(remainMiles * 100.00/196) +"%\n");
            display.append("充电模式："+ (new Random().nextBoolean() ?"正在慢充":"正在快充")+"\n");
          }
          display.append("实际获取数据："+data);
          break;
        case UsbService.CTS_CHANGE:
          Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
          break;
        case UsbService.DSR_CHANGE:
          Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
          break;
      }
    }
  }
}
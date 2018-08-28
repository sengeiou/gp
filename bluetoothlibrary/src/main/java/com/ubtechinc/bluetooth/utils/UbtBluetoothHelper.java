package com.ubtechinc.bluetooth.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.bluetooth.event.BleScanFinishedEvent;
import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.event.BleScanFailEvent;
import com.ubtechinc.bluetooth.event.BleScanResultEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 蓝牙助手类----处理单个设备直连需求
 *
 * @author zach.zhang
 */

public class UbtBluetoothHelper {
  private static final int MSG_ONE_DEVICE_SCAN_DELAY = 1;
  private static final int MSG_SCAN_DELEY = 2;
  private static final double AUTOCONNECT_DISTANCE = 1.0;
  public static final String KEY_UBTBLUETOOTH = "key_ubtbluetooth";
  private static final String TAG = "UbtBluetoothHelper";
  //  搜索第一个设备时间
  private static final int ONE_DEVICE_SCAN_DELAY = 1500;
  // 搜索总时间
  private static final int SCAN_DELAY = ONE_DEVICE_SCAN_DELAY + 500;
  private Map<String, UbtBluetoothDevice> map = new HashMap<>();
  private volatile static UbtBluetoothHelper instance;
  private BluetoothScanListener bluetoothScanListener;
  private boolean start;
  private Handler handler;
  private boolean onDeviceCompletely;
  private boolean scanCompletely;

  private UbtBluetoothHelper() {
    handler = new Handler(Looper.getMainLooper()) {
      @Override public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, " what : " + msg.what);
        switch (msg.what) {
          case MSG_ONE_DEVICE_SCAN_DELAY:
            onDeviceCompletely = true;
            if (scanEnd()) {
              notifyScanEnd();
            } else {
              handler.sendEmptyMessageDelayed(MSG_SCAN_DELEY, SCAN_DELAY - ONE_DEVICE_SCAN_DELAY);
            }
            break;
          case MSG_SCAN_DELEY:
            scanCompletely = true;
            notifyScanEnd();
            break;
        }
      }
    };
  }

  private void notifyScanEnd() {
    if (bluetoothScanListener != null) {
      bluetoothScanListener.scanEnd();
    }
  }

  public static UbtBluetoothHelper getInstance() {
    if (instance == null) {
      synchronized (UbtBluetoothHelper.class) {
        if (instance == null) {
          instance = new UbtBluetoothHelper();
        }
      }
    }
    return instance;
  }

  public void setBluetoothScanListener(BluetoothScanListener bluetoothScanListener) {
    this.bluetoothScanListener = bluetoothScanListener;
  }

  public synchronized void startScan(String userId) {
    LogUtils.i("startScan");
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
    map.clear();
    start = true;
    onDeviceCompletely = false;
    scanCompletely = false;
    final UbtBluetoothManager ubtBluetoothManager = UbtBluetoothManager.getInstance();
    ubtBluetoothManager.setAutoConnect(false);
    ubtBluetoothManager.setBleNamePrefix(Constants.ROBOT_TAG);
    ubtBluetoothManager.setCurrentRobotSn(userId);
    ubtBluetoothManager.startScanBluetooth();
    handler.removeMessages(MSG_ONE_DEVICE_SCAN_DELAY);
    handler.sendEmptyMessageDelayed(MSG_ONE_DEVICE_SCAN_DELAY, ONE_DEVICE_SCAN_DELAY);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onSubscribeBleScanResult(BleScanResultEvent event) {
    Log.d(TAG, " scanSuccess -- device: " + event.device);
    map.put(event.device.getSn(), event.device);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onSubscribeBleScanFailed(BleScanFailEvent event) {
    Log.d(TAG, "  scanFailed: " + event.errorCode);
    map.clear();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onSubscribeBleScanFinished(BleScanFinishedEvent event) {
    Log.d(TAG, "  scanFinish: " );
    map.clear();
  }

  /**
   * 查询阶段是否结束,两种情况未结束，1、1s内无设备；2，1s-1.5s内仅有一个设备时表示未未结束
   */
  public boolean scanEnd() {
    // 未开始直接返回true
    return !start || onDeviceCompletely && !(map.size() == 1 && !scanCompletely);
  }

  public boolean hasDevices() {
    return !map.isEmpty();
  }

  /**
   * 获取目标设备
   *
   * @return 目标设备
   */
  public UbtBluetoothDevice getPurposeDevice() {
    UbtBluetoothDevice ubtBluetoothDevice = null;
    if (scanEnd() && map.size() != 0) {
      boolean isOnly = (map.size() == 1);
      boolean rssiOnly = true;
      Set<Map.Entry<String, UbtBluetoothDevice>> entrySet = map.entrySet();
      for (Map.Entry<String, UbtBluetoothDevice> entry : entrySet) {
        if (isOnly) {
          return entry.getValue();
        }
        if (BleUtil.getDistance(entry.getValue().getRssi()) <= AUTOCONNECT_DISTANCE) {
          if (rssiOnly && ubtBluetoothDevice == null) {
            ubtBluetoothDevice = entry.getValue();
          } else if (ubtBluetoothDevice != null) {
            rssiOnly = false;
            ubtBluetoothDevice = null;
          }
        }
      }
    }
    return ubtBluetoothDevice;
  }

  public synchronized void stopScan() {
    LogUtils.i("stopScan");
    EventBus.getDefault().unregister(this);
    map.clear();
    handler.removeMessages(MSG_ONE_DEVICE_SCAN_DELAY);
    handler.removeMessages(MSG_SCAN_DELEY);
    bluetoothScanListener = null;
    if (start) {
      start = false;
    }
  }

  public interface BluetoothScanListener {
    void scanEnd();
  }
}

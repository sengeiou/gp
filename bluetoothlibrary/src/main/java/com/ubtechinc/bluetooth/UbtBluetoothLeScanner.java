package com.ubtechinc.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：wululin
 * @date：2017/10/18 17:17
 * @modifier：ubt
 * @modify_date：2017/10/18 17:17
 * [A brief description]
 * version
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP) class UbtBluetoothLeScanner {
  private BluetoothLeScanner mScanner;
  /** 扫描过滤 UUID  机器人蓝牙广播出来的也是这uuid 通话这个UUid过滤，扫描出来的蓝牙都是机器广播出来的，不会扫描到其他的蓝牙 **/
  private static final String UUID_FILTER = "07303e62-cb70-38d8-8f5c-40c062145442";
  private boolean mScanning = false;
  private final byte[] mLock = new byte[0];
  private ScanCallback mOuterScanCallback;

  private ScanCallback innerScanCallback = new ScanCallback() {
    @Override public void onScanResult(int callbackType, ScanResult result) {
      if (mOuterScanCallback != null) {
        mOuterScanCallback.onScanResult(callbackType, result);
      }
    }

    @Override public void onScanFailed(int errorCode) {
      if (mOuterScanCallback != null) {
        synchronized (mLock) {
          if (mScanning) {
            mScanning = true;
          }
        }
        mOuterScanCallback.onScanFailed(errorCode);
      }
    }
  };

  UbtBluetoothLeScanner() {
    mScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
  }

  boolean isScanning() {
    return mScanning;
  }

  /**
   * 启动扫描
   *
   * @param scanCallback {@link ScanCallback}
   */
  void scanLeDevice(final ScanCallback scanCallback) {
    this.mOuterScanCallback = scanCallback;
    synchronized (mLock) {
      if (mScanning) {
        return;
      }
      Log.w("Logic", "开始扫描蓝牙....");
      mScanner.startScan(new ArrayList<ScanFilter>() {{
                           add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(UUID_FILTER)).build());
                         }}, new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
          innerScanCallback);
      mScanning = true;
    }
  }

  /**
   * 结束扫描
   */
  void stopScan() {
    Log.w("Logic", "停止扫描蓝牙....");
    synchronized (mLock) {
      if (mScanning) {
        mScanning = false;
        mScanner.stopScan(innerScanCallback);
        SystemClock.sleep(10);
        if (mOuterScanCallback != null) {
          mOuterScanCallback.onScanFailed(0);//正常结束
          mOuterScanCallback = null;
        }
      }
    }
  }
}

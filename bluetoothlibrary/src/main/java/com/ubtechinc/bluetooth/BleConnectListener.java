package com.ubtechinc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by logic on 18-6-12.
 */

public interface BleConnectListener {
  void connectFailed();

  void connectSuccess(BluetoothDevice device);

  void receiverDataFromRobot(String data);

  void sendDataFailed(String result);

  void sendDataSuccess(String msg);
}

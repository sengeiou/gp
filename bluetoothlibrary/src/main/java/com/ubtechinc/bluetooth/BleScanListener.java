package com.ubtechinc.bluetooth;

/**
 * Created by logic on 18-6-11.
 */

public interface BleScanListener {

  void scanSuccess(UbtBluetoothDevice device);

  void scanFailed(int errorCode);
}

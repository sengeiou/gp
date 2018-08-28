package com.ubtechinc.bluetooth.event;

import com.ubtechinc.bluetooth.UbtBluetoothDevice;

/**
 * Created by logic on 18-6-12.
 *
 * @author logic
 */

public final class BleScanResultEvent {
  public final UbtBluetoothDevice device;

  public BleScanResultEvent(UbtBluetoothDevice device) {
    this.device = device;
  }
}

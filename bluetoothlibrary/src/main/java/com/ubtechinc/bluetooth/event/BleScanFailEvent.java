package com.ubtechinc.bluetooth.event;

/**
 * Created by logic on 18-6-12.
 *
 * @author logic
 */

public final class BleScanFailEvent {
  public final int errorCode;

  public BleScanFailEvent(int errorCode) {
    this.errorCode = errorCode;
  }
}

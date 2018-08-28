package com.ubtechinc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * @author：wululin
 * @date：2017/10/18 20:49
 * @modifier：ubt
 * @modify_date：2017/10/18 20:49
 * [A brief description]
 * version
 */

public class ConnResult {
    private BluetoothDevice device;
    private boolean lostConnection;
    private int resultCode;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public boolean isLostConnection() {
        return lostConnection;
    }

    public void setLostConnection(boolean lostConnection) {
        this.lostConnection = lostConnection;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}

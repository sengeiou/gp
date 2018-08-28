package com.ubtechinc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * @author：wululin
 * @date：2017/10/20 15:44
 * @modifier：ubt
 * @modify_date：2017/10/20 15:44
 * [A brief description]
 * version
 */

public  class BleConnectAbstract implements BleConnectListener{
    @Override
    public void connectFailed() {

    }

    @Override
    public void connectSuccess(BluetoothDevice device) {

    }

    @Override
    public void receiverDataFromRobot(String data) {

    }

    @Override
    public void sendDataFailed(String result) {

    }

    @Override
    public void sendDataSuccess(String msg) {

    }
}

package com.ubtechinc.goldenpig.pigmanager.widget;

import android.bluetooth.BluetoothDevice;

import com.ubtechinc.bluetooth.UbtBluetoothDevice;

public interface OnPigListItemClickListener {
    void onClick(int pos, UbtBluetoothDevice device);
}

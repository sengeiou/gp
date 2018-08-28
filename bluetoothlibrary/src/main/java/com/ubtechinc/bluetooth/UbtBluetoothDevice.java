package com.ubtechinc.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author：wululin
 * @date：2017/10/18 17:53
 * @modifier：ubt
 * @modify_date：2017/10/18 17:53
 * [A brief description]
 * version
 *
 */

public class UbtBluetoothDevice implements Parcelable {
    BluetoothDevice device;
    String sn;
    boolean needEncrption;
    private int rssi;
    private int retryCount;

    public UbtBluetoothDevice() {
    }

    protected UbtBluetoothDevice(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        sn = in.readString();
        needEncrption = in.readByte() != 0;
    }

    public static final Creator<UbtBluetoothDevice> CREATOR = new Creator<UbtBluetoothDevice>() {
        @Override
        public UbtBluetoothDevice createFromParcel(Parcel in) {
            return new UbtBluetoothDevice(in);
        }

        @Override
        public UbtBluetoothDevice[] newArray(int size) {
            return new UbtBluetoothDevice[size];
        }
    };

    public int getRssi() {
        return rssi;
    }

    public boolean getDeviceNeedEncrp(){
        return needEncrption;
    }

    void setDeviceEncrp(boolean encrp){
        needEncrption = encrp;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    void setRssi(int rssi) {
        this.rssi = rssi;
    }

    synchronized int getRetryCount() {
        return retryCount;
    }

    synchronized void incRetryCount() {
        retryCount++;
    }

    @Override
    public String toString() {
        return "UbtBluetoothDevice{" +
                "device=" + device +
                ", sn='" + sn + '\'' +
                ", needEncrption=" + needEncrption +
                ", rssi=" + rssi +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeString(sn);
        dest.writeBooleanArray(new boolean[]{needEncrption});
    }
}

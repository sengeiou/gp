package com.ubtechinc.goldenpig.comm.wifi;

/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :wifi实体
 *@time          :2019/1/8 16:33
 *@change        :
 *@changetime    :2019/1/8 16:33
*/
public class UbtWifiInfo {

    private String ssid;

    private int rssi;

    private String encryptionKey;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @Override
    public String toString() {
        return "UbtWifiInfo{" +
                "ssid='" + ssid + '\'' +
                ", rssi=" + rssi +
                ", encryptionKey='" + encryptionKey + '\'' +
                '}';
    }

    public boolean isFree() {
        return "off".equals(encryptionKey);
    }
}

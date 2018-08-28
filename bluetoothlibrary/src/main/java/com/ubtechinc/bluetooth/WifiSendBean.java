package com.ubtechinc.bluetooth;

/**
 * 给机器人配网传输的WiFi信息类
 * Created by nixiaoyan on 2017/2/15.
 */

public class WifiSendBean extends BluetoothSendObj{
    private String s;
    private String c;
    private String p;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }
}

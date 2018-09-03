package com.ubtechinc.goldenpig.utils;

import android.net.wifi.ScanResult;

public class NetUtils {
    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";
    public static boolean isFreeWifi(ScanResult result){
        boolean isFreeWifi=false;
        String capabilities=result.capabilities.trim();
        if (capabilities != null && (capabilities.equals(WIFI_AUTH_OPEN) || capabilities.equals(WIFI_AUTH_ROAM))) {
            isFreeWifi=true;
        }
        return isFreeWifi;
    }
}

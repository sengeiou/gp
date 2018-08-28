package com.ubtechinc.goldenpig.pigmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @desc : 存储辅助类
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/13
 */

class SharePreferenceHelper {

    private static final String TAG = SharePreferenceHelper.class.getSimpleName();
    private static final String SHAREPREFERENCE_NETWORK = "sharepreference_network";
    private Context context;

    public SharePreferenceHelper(Context context) {
        this.context = context;
    }

    public void putSsidAndPassword(String ssid, String passwordavisible) {
        Log.d(TAG, " putSsidAndPassword -- ssid : " + ssid + " passwordavisible: " + passwordavisible);
        SharedPreferences sp = context.getSharedPreferences(SHAREPREFERENCE_NETWORK, Context.MODE_PRIVATE);
        sp.edit().putString(ssid,passwordavisible).apply();
    }

    public String getPassword(String ssid) {
        SharedPreferences sp = context.getSharedPreferences(SHAREPREFERENCE_NETWORK, Context.MODE_PRIVATE);
        Log.d(TAG, " getPassword -- ssid : " + ssid + " sp.getString(ssid, null): " + sp.getString(ssid, null));
        return sp.getString(ssid, null);
    }
}

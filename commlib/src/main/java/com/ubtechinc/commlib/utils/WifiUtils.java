package com.ubtechinc.commlib.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :
 *@time          :2018/8/27 18:46
 *@change        :
 *@changetime    :2018/8/27 18:46
*/
public class WifiUtils {
    public static boolean isOpenWifi(Context context){
        WifiManager wifiManager=null;
        if(wifiManager==null)
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        return wifiManager.isWifiEnabled();
    }
    /**
     * 校验wifi信息是否合法
     */
    public static boolean comfirmWifiInfoValidate(String ssidName,String password,String cap) {
        if (TextUtils.isEmpty(cap)){

            return false;
        }
        int Type;
        if (cap.contains("WEP")) {
            Type = 2;
        } else if (cap.contains("WPA2")) {
            Type = 4;
        } else if (cap.contains("WPA")) {
            Type = 3;
        } else {
            Type = 1; //加密类型无需密码
        }
        if (Type != 1){
            return !(password.length() < 8);
        }else{
            return !(password.length() != 0);
        }
    }
}

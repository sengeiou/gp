package com.ubt.imlibv2.bean;

import com.ubtech.utilcode.utils.MD5Utils;

public class Utils {
    public static String getSingal(long time){
        String singa = MD5Utils.md5("IM$SeCrET" + time, 32);
        return singa;
    }
}

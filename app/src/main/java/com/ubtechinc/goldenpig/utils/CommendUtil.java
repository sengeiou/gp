package com.ubtechinc.goldenpig.utils;

import android.text.TextUtils;

public class CommendUtil {

    public static int getMsgLength(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        } else {
            return (str.getBytes().length - (str.getBytes().length - str.length()) / 2) / 2;
        }
    }
}

package com.ubtechinc.goldenpig.utils;

/**
 * @Description: 鉴别快速点击工具类
 * @Author: zhijunzhou
 * @CreateDate: 2019/1/24 15:11
 */
public class FastClickUtils {

    private static long lastClickTime;

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}

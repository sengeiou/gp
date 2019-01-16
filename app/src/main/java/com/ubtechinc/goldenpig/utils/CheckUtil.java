package com.ubtechinc.goldenpig.utils;

import android.content.Context;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;

/**
 * @Description: 校验工具类
 * @Author: zhijunzhou
 * @CreateDate: 2019/1/15 17:20
 */
public class CheckUtil {

    /**
     * 校验手机网络状态
     * @param context
     * @return
     */
    public static boolean checkPhoneNetState(Context context) {
        if (UBTPGApplication.isNetAvailable) {
            return true;
        } else {
            UbtToastUtils.showCustomToast(context, context.getString(R.string.network_error));
            return false;
        }
    }

    public static boolean checkPhoneNetState(Context context, boolean needToast) {
        if (UBTPGApplication.isNetAvailable) {
            return true;
        } else {
            if (needToast) {
                UbtToastUtils.showCustomToast(context, context.getString(R.string.network_error));
            }
            return false;
        }
    }

    /**
     * 校验机器人在线状态
     * @param context
     * @return
     */
    public static boolean checkRobotOnlineState(Context context) {
        if (UBTPGApplication.isRobotOnline) {
            return true;
        } else {
            UbtToastUtils.showCustomToast(context, context.getString(R.string.ubt_robot_offline));
            return false;
        }
    }

}

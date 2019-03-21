package com.ubtechinc.goldenpig.utils;

import android.text.TextUtils;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtrobot.analytics.mobile.AnalyticsKit;

import java.util.HashMap;
import java.util.Map;

public class SCADAHelper {

    public static final String TAG = SCADAHelper.class.getSimpleName();

    /**
     * 绑定机器
     */
    public static final String EVENET_APP_ROBOT_BIND = "app_robot_bind";

//    public static void recordEvent(String eventId, String... params) {
//        switch (eventId) {
//            case "app_robot_bind": {
//                Map<String, String> segmentation = new HashMap<>();
//                segmentation.put("userId", AuthLive.getInstance().getUserId());
//                if (params != null && params.length >= 1) {
//                    segmentation.put("robotId", params[0]);
//                }
//                AnalyticsKit.recordEvent("app_robot_bind", segmentation);
//            }
//            break;
//        }
//
//    }


    /**
     * 必报数据
     *
     * @return
     */
    public static Map<String, String> obligatoryData() {
        Map<String, String> segmentation = new HashMap<>();
        segmentation.put("app_version", ContextUtils.getVerName(UBTPGApplication.getContext()));
        String userId = AuthLive.getInstance().getUserId();
        if (TextUtils.isEmpty(userId)) {
            segmentation.put("user_id", userId);
        }
        String robotSn = AuthLive.getInstance().getRobotUserId();
        if (!TextUtils.isEmpty(robotSn)) {
            segmentation.put("robot_sn", robotSn);
        }
        return segmentation;
    }

    /**
     * 埋点上报
     *
     * @param eventId
     * @param segmentation
     */
    public static void recordEvent(String eventId, Map<String, String> segmentation) {
        Map<String, String> data = obligatoryData();
        if (segmentation != null && !segmentation.isEmpty()) {
            data.putAll(segmentation);
        }
        if (!BuildConfig.DEBUG) {
            printBuryingPointData(eventId, data);
            AnalyticsKit.recordEvent(eventId, data);
        }
    }

    private static void printBuryingPointData(String eventId, Map<String, String> data) {
        String msg = "eventId:" + eventId + "|burying point:" + data;
        LogUtils.d(TAG, msg);
    }


    /**
     * 只传事件id
     */
    public static void recordEvent(String eventId) {
        recordEvent(eventId, null);
    }
}

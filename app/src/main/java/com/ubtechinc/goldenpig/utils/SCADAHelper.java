package com.ubtechinc.goldenpig.utils;

import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtrobot.analytics.mobile.AnalyticsKit;

import java.util.HashMap;
import java.util.Map;

public class SCADAHelper {

    /**
     * 绑定机器
     */
    public static final String EVENET_APP_ROBOT_BIND = "app_robot_bind";

    public static void recordEvent(String eventId, String... params) {
        switch (eventId) {
            case "app_robot_bind": {
                Map<String, String> segmentation = new HashMap<>();
                segmentation.put("userId", AuthLive.getInstance().getUserId());
                if (params != null && params.length >= 1) {
                    segmentation.put("robotId", params[0]);
                }
                AnalyticsKit.recordEvent("app_robot_bind", segmentation);
            }
            break;
        }

    }
}

package com.ubtechinc.goldenpig.utils;

import com.tencent.ai.tvs.ConstantValues;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.UserInfoManager;

public class TvsUtil {

    public static ELoginPlatform currentPlatform() {
        switch (UserInfoManager.getInstance().idType) {
            case ConstantValues.PLATFORM_WX:
                return ELoginPlatform.WX;
            case ConstantValues.PLATFORM_QQOPEN:
                return ELoginPlatform.QQOpen;
            default:
                return ELoginPlatform.QQOpen;
        }
    }

    public static String currentPlatformValue() {
        switch (UserInfoManager.getInstance().idType) {
            case ConstantValues.PLATFORM_WX:
                return "微信";
            case ConstantValues.PLATFORM_QQOPEN:
                return "QQ";
            default:
                return "未知";
        }
    }
}

package com.ubtechinc.goldenpig.net;


import com.ubtech.utilcode.utils.MD5Utils;
import com.ubtechinc.goldenpig.BuildConfig;

/**
 * @author：ubt
 * @date：2018/10/10 11:37
 * @modifier：ubt
 * @modify_date：2018/10/10 11:37
 * [A brief description]
 */

public class URestSigner {

    private static final String SIGN_PART_SEPARATOR = " ";

    public static String sign() {
        long now = System.currentTimeMillis() / 1000;
        // 规则：
        // 1. 计算当前时间戳（单位：秒）拼接 AppKey 形成的字符串的 MD5 值，得到签名段
        // 2. 将签名段与先前的时间戳使用空格连接，得到最终 X-UBT-Sign 内容
        return MD5Utils.md5(now + BuildConfig.APP_KEY, 32) + SIGN_PART_SEPARATOR + now;
    }

}

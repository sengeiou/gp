package com.ubtechinc.goldenpig.utils.share;

/**
 * created by gemvary at 2018/5/16
 */
public interface OnClickShareListener {
    void onShareWXMiniProgram(ShareUtility shareUtility);// 小程序
    void onShareWx(ShareUtility shareUtility);// 微信图片
    void onShareWxTimeline(ShareUtility shareUtility);
    void onShareSMS(ShareUtility shareUtility);// 短信
    void onShareMMS(ShareUtility shareUtility);// 彩信
    void onShareQQ(ShareUtility shareUtility);// QQ
}

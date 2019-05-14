package com.ubtechinc.goldenpig.utils.share;


import com.ubtechinc.goldenpig.R;

/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :分享组件
 *@time          :2019/1/21 20:58
 *@change        :
 *@changetime    :2019/1/21 20:58
*/
public enum ShareItemEnum {

    //    WECHAT(R.drawable.ssdk_oks_classic_wechat, "微信好友"),
    // 小程序App.isN = true时需注释掉
//    WECHATMINIPROGRAM(R.drawable.icon_smallprogram, "小程序"),
    WECHAT(R.drawable.ic_wechat_share, "发送给微信朋友"),
    WECHATMOMENTS(R.drawable.ic_wxfriend_share, "分享到微信朋友圈"),
    QQ(R.drawable.ic_qq_share, "分享到手机QQ"),
//    SMS(R.drawable.icon_shortmessage, "短信"),
    //    MMS(R.drawable.icon_mms,"彩信");
    ;

    private int imageID;

    private final String value;

    //构造器默认也只能是private, 从而保证构造函数只能在内部使用
    ShareItemEnum(int imageID, String value) {
        this.imageID = imageID;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getImageID() {
        return imageID;
    }
}

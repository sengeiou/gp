package com.ubt.robot.dmsdk;

import com.tencent.ai.tvs.env.ELoginPlatform;

/**
 * @Description: TVS->ELoginPlatform
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/19 14:38
 */
public enum TVSWrapPlatform {

    /**
     * 微信
     */
    WX,

    /**
     * QQ
     */
    QQOpen,

    /**
     * 未知
     */
    Unknown;

    public ELoginPlatform toReal() {
        switch (this) {
            case WX:
                return ELoginPlatform.WX;
            case QQOpen:
                return ELoginPlatform.QQOpen;
            default:
                return ELoginPlatform.Unknown;
        }
    }
}

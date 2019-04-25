package com.ubt.robot.dmsdk;

import com.tencent.ai.tvs.env.ELoginEnv;

/**
 * @Description: TVS->ELoginPlatform
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/19 14:38
 */
public enum TVSWrapLoginEnv {

    /**
     * 正式环境
     */
    FORMAL,

    /**
     * 测试环境
     */
    TEST,

    /**
     * 体验环境
     */
    EX;

    public ELoginEnv toReal() {
        return ELoginEnv.valueOf(name());
    }

    public static TVSWrapLoginEnv index(ELoginEnv eLoginEnv) {
        return TVSWrapLoginEnv.valueOf(eLoginEnv.name());
    }
}

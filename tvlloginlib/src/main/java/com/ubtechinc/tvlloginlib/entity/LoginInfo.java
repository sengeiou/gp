package com.ubtechinc.tvlloginlib.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ubt on 2017/8/25.
 */

public class LoginInfo {

    public static final Map<Integer, String> LoginType = new HashMap();

    static {
        LoginType.put(0,"WX");
        LoginType.put(1,"QQ");
        LoginType.put(3,"sina");
    }

    private String accessToken;
    private String appId;
    private String loginType;
    //qbid
    private String miniTvsId;
    private String openId;
    private String tvsId;
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getMiniTvsId() {
        return miniTvsId;
    }

    public void setMiniTvsId(String miniTvsId) {
        this.miniTvsId = miniTvsId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getTvsId() {
        return tvsId;
    }

    public void setTvsId(String tvsId) {
        this.tvsId = tvsId;
    }
}

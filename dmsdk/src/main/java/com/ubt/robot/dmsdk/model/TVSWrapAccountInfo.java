package com.ubt.robot.dmsdk.model;

/**
 * @Description: TVS->AccountInfoManager
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/19 15:32
 */
public class TVSWrapAccountInfo {

    private String loginType;

    private String appID;

    private String openID;

    private String tvsID;

    private String accessToken;

    private String refreshToken;

    private String userID;

    private String clientID;

    public TVSWrapAccountInfo() {

    }

    public TVSWrapAccountInfo(String loginType, String appID, String openID, String tvsID, String accessToken,
                              String refreshToken, String userID, String clientID) {
        this.loginType = loginType;
        this.appID = appID;
        this.openID = openID;
        this.tvsID = tvsID;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userID = userID;
        this.clientID = clientID;
    }

    public TVSWrapAccountInfo(String loginType, String appID, String openID, String tvsID, String accessToken,
                              String refreshToken, String userID) {
        this.loginType = loginType;
        this.appID = appID;
        this.openID = openID;
        this.tvsID = tvsID;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userID = userID;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public String getTvsID() {
        return tvsID;
    }

    public void setTvsID(String tvsID) {
        this.tvsID = tvsID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String currentPlatformValue() {
        switch (loginType) {
            case "WX":
                return "微信";
            case "QQOpen":
                return "QQ";
            default:
                return "未知";
        }
    }

    public String ubtLoginType() {
        switch (loginType) {
            case "QQOpen":
                return "QQ";
            default:
                return loginType;
        }
    }

    @Override
    public String toString() {
        return "TVSWrapAccountInfo{" +
                "loginType='" + loginType + '\'' +
                ", appID='" + appID + '\'' +
                ", openID='" + openID + '\'' +
                ", tvsID='" + tvsID + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", userID='" + userID + '\'' +
                ", clientID='" + clientID + '\'' +
                '}';
    }
}

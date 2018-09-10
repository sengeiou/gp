package com.ubtechinc.goldenpig.login;

import android.support.annotation.Keep;


import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.nets.http.Url;


@Keep
public class ThirdPartLoginModule {


    @Url("/user-service-rest/v2/user/login/third")
    @Keep
    public class LoginRequest {

        private String accessToken;
        private String appId;
        private String loginType;
        private String miniTvsId;
        private String openId;
        private int ubtAppId;

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

        public int getUbtAppId() {
            return ubtAppId;
        }

        public void setUbtAppId(int ubtAppId) {
            this.ubtAppId = ubtAppId;
        }
    }

    @Keep
    public class Response {

        private UserInfo user;

        private Token token;

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }
    }

    @Keep
    public class Token {
        private long expireAt;
        private String token;

        public long getExpireAt() {
            return expireAt;
        }

        public void setExpireAt(long expireAt) {
            this.expireAt = expireAt;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}

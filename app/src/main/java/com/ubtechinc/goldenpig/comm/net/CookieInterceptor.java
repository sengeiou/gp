package com.ubtechinc.goldenpig.comm.net;

import android.text.TextUtils;

import com.ubtechinc.tvlloginlib.entity.LoginInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CookieInterceptor implements Interceptor {

    private String token;

    private Long expireAt;

    private LoginInfo info;

    public static final String AUTHORIZATION = "authorization";
    public static final String COOKIE = "cookie";

    private static class CookieInterceptorHolder {
        private static CookieInterceptor instance = new CookieInterceptor();
    }

    public static CookieInterceptor get() {
        return CookieInterceptorHolder.instance;
    }

    private CookieInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!TextUtils.isEmpty(token)) {
            String cookie = request.header(COOKIE);
            String authHeader = request.header(AUTHORIZATION);
            Request.Builder builder = request.newBuilder();
            if (TextUtils.isEmpty(authHeader)) {
                builder.addHeader(AUTHORIZATION, token);
            }
            if (cookie == null || !cookie.contains(AUTHORIZATION)) {
                builder.addHeader(COOKIE, AUTHORIZATION + "=" + token);
            }
            return chain.proceed(builder.build());
        }
        return chain.proceed(request);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public void setLoginInfo(LoginInfo info) {
        this.info = info;
    }

    public LoginInfo getThridLogin() {
        return info;
    }
}

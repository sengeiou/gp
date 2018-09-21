package com.ubtechinc.goldenpig.login.repository;


import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.ThirdPartLoginModule;

import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.logout.LoginoutModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.tvlloginlib.entity.LoginInfo;

import java.util.HashMap;

/**
 * Created by ubt on 2017/9/30.
 */

public class UBTAuthRepository {
    public static final String TAG = "UBTAuthRepository";

    public static final int UBT_APP_ID = 100020012;//为了后台统计不同产品的注册数据，增肌一个字段ubtAppId

    public void login(LoginInfo info, final UBTAuthCallBack callback) {
        final ThirdPartLoginModule.LoginRequest loginRequest = new ThirdPartLoginModule().new LoginRequest();
        loginRequest.setOpenId(info.getOpenId());
        loginRequest.setLoginType(info.getLoginType());
        loginRequest.setMiniTvsId(info.getMiniTvsId());
        loginRequest.setAccessToken(info.getAccessToken());
        loginRequest.setAppId(info.getAppId());
        loginRequest.setUbtAppId(UBT_APP_ID);//为了后台统计不同产品的注册数据，增肌一个字段ubtAppId
        HttpProxy.get().doPut(loginRequest, new ResponseListener<ThirdPartLoginModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError();
            }

            @Override
            public void onSuccess(ThirdPartLoginModule.Response loginResponse) {

                CookieInterceptor.get().setToken(loginResponse.getToken().getToken());
                CookieInterceptor.get().setExpireAt(loginResponse.getToken().getExpireAt());
                callback.onSuccess(loginResponse.getUser());
            }
        });
    }

    public void logout(final UBTAuthCallBack callback) {
        LoginoutModule.Request request = new LoginoutModule().new Request();
        HashMap<String,String> header=new HashMap<>();
        header.put(CookieInterceptor.AUTHORIZATION,CookieInterceptor.get().getToken());
        HttpProxy.get().doDelete(request,header,new ResponseListener<LoginoutModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                if (callback != null) {
                    callback.onLogoutError();
                }
            }

            @Override
            public void onSuccess(LoginoutModule.Response response) {
                if (callback != null) {
                    callback.onLogout();
                }
            }
        });
    }

    public interface UBTAuthCallBack {

         void onSuccess(UserInfo userInfo);

         void onError();

         void onLogout();

         void onLogoutError();
    }
}

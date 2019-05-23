package com.ubtechinc.goldenpig.repository;

import android.app.Activity;

import com.tencent.ai.tvs.core.common.ErrCode;
import com.tencent.bugly.crashreport.CrashReport;
import com.ubt.robot.dmsdk.TVSWrapBridge;
import com.ubt.robot.dmsdk.TVSWrapPlatform;
import com.ubt.robot.dmsdk.model.TVSWrapAccountInfo;
import com.ubtechinc.goldenpig.login.LoginInfo;

public class TVSAuthRepository {

    public TVSAuthRepository() {
    }

    public void loginWX(Activity activity, final AuthCallBack authCallBack) {
        TVSWrapBridge.tvsLogin(TVSWrapPlatform.WX, activity, new TVSWrapBridge.TVSWrapCallback() {
            @Override
            public void onError(int errCode) {
                callBackError(authCallBack, errCode);
            }

            @Override
            public void onSuccess(Object result) {
                callBackSuccess(authCallBack, getLoginInfo());
            }
        });
    }

    public void loginQQ(Activity activity, final AuthCallBack authCallBack) {
        TVSWrapBridge.tvsLogin(TVSWrapPlatform.QQOpen, activity, new TVSWrapBridge.TVSWrapCallback() {
            @Override
            public void onError(int errCode) {
                callBackError(authCallBack, errCode);
            }

            @Override
            public void onSuccess(Object result) {
                callBackSuccess(authCallBack, getLoginInfo());

            }
        });
    }

    protected LoginInfo getLoginInfo() {
        TVSWrapAccountInfo tvsWrapAccountInfo = TVSWrapBridge.getTVSAccountInfo();
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAppId(tvsWrapAccountInfo.getAppID());
        loginInfo.setAccessToken(tvsWrapAccountInfo.getAccessToken());
        loginInfo.setMiniTvsId(tvsWrapAccountInfo.getUserID());
        loginInfo.setLoginType(tvsWrapAccountInfo.ubtLoginType());
        loginInfo.setOpenId(tvsWrapAccountInfo.getOpenID());
        loginInfo.setTvsId(tvsWrapAccountInfo.getTvsID());
        return loginInfo;
    }

    public boolean isWXInstall() {
        return TVSWrapBridge.isWXAppInstalled();
    }

    public boolean isWXSupport() {
        return TVSWrapBridge.isWXAppSupportAPI();
    }

    public void refreshLogin(Activity activity, final AuthCallBack authCallBack) {
        TVSWrapBridge.tvsTokenVerify(new TVSWrapBridge.TVSWrapCallback() {
            @Override
            public void onError(int errCode) {
                callBackError(authCallBack, errCode);
            }

            @Override
            public void onSuccess(Object o) {
                callBackSuccess(authCallBack, getLoginInfo());
            }
        });
    }

    public void logout() {
        TVSWrapBridge.tvsLogout();
    }

    public boolean isTokenExist(Activity activity) {
        return TVSWrapBridge.isTokenExist();
    }

    public interface AuthCallBack {
        void onTVSLoginSuccess(LoginInfo userInfo);

        void onError();

        void onCancel();
    }

    public interface BindingCallBack {
        void onBindingSuccess();

        void onError();
    }


    private void callBindingSuccess(BindingCallBack bindingCallBack) {
        if (bindingCallBack != null) {
            bindingCallBack.onBindingSuccess();
        }
    }

    private void callBindingError(BindingCallBack bindingCallBack) {
        if (bindingCallBack != null) {
            bindingCallBack.onError();
        }
    }

    private void callBackSuccess(AuthCallBack authCallBack, LoginInfo loginInfo) {
        if (authCallBack != null) {
            authCallBack.onTVSLoginSuccess(loginInfo);
        }
    }

    private void callBackError(AuthCallBack authCallBack, int errCode) {
        if (authCallBack != null) {
            if (errCode == ErrCode.ERR_USER_CANCEL) {
                authCallBack.onCancel();
            } else {
                authCallBack.onError();
            }
        }
        CrashReport.postCatchedException(new Throwable(String.valueOf(errCode)));
    }

    private void callBackCancel(AuthCallBack authCallBack) {
        if (authCallBack != null) {
            authCallBack.onCancel();
        }
    }
}

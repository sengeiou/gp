package com.ubtechinc.goldenpig.login;

import android.app.Activity;
import android.content.Intent;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.repository.TVSAuthRepository;
import com.ubtechinc.tvlloginlib.entity.LoginInfo;

/**
 *@auther        :hqt
 *@description   :这是ILoginModel接口定义，登录MVP中Modle定义
 *@time          :2018/8/21 11:14
 *@change        :
 *@changetime    :2018/8/21 11:14
*/
public class LoginModel implements TVSAuthRepository.AuthCallBack{
    private TVSAuthRepository tvsAuthRepository;
    private AuthLive authLive ;
    public LoginModel(){
        tvsAuthRepository = new TVSAuthRepository(BuildConfig.APP_ID_WX,BuildConfig.APP_ID_QQ);
        authLive = AuthLive.getInstance();
    }

    @Override
    public void onTVSLoginSuccess(LoginInfo userInfo) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onCancel() {

    }
    public boolean isWXInstall(){
        return tvsAuthRepository.isWXInstall();
    }

    public boolean isWXSupport(){
        return tvsAuthRepository.isWXSupport();
    }

    /*检测是否已登录*/
    public boolean checkToken(Activity activity) {
        if (isTokenExist(activity)) {
            tvsAuthRepository.refreshLogin(activity, this);
            return true;
        }
        return false;
    }

    public boolean isTokenExist(Activity activity) {
        return tvsAuthRepository.isTokenExist(activity);
    }

    public void loginWX(Activity activity) {
        authLive.logining();
        tvsAuthRepository.loginWX(activity, this);
    }

    public void loginQQ(Activity activity) {
        authLive.logining();
        tvsAuthRepository.loginQQ(activity, this);
    }
    public void logoutTVS(){
        tvsAuthRepository.logout();
    }
    public void onResume() {
        tvsAuthRepository.onResume();
    }public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tvsAuthRepository.onActivityResult(requestCode, resultCode, data);
    }


}

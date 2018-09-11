package com.ubtechinc.goldenpig.login;

import android.app.Activity;
import android.content.Intent;

import com.ubt.im.UbtTIMManager;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.login.repository.UBTAuthRepository;
import com.ubtechinc.goldenpig.repository.TVSAuthRepository;
import com.ubtechinc.tvlloginlib.entity.LoginInfo;

/**
 *@auther        :hqt
 *@description   :这是ILoginModel接口定义，登录MVP中Modle定义
 *@time          :2018/8/21 11:14
 *@change        :
 *@changetime    :2018/8/21 11:14
*/
public class LoginModel implements TVSAuthRepository.AuthCallBack, UBTAuthRepository.UBTAuthCallBack{
    private final String TAG="LoginModel";
    private TVSAuthRepository tvsAuthRepository;
    private AuthLive authLive ;
    private UBTAuthRepository ubtAuthRepository;
    private UbtTIMManager timManager;
    public LoginModel(){
        tvsAuthRepository = new TVSAuthRepository(BuildConfig.APP_ID_WX,BuildConfig.APP_ID_QQ);
        ubtAuthRepository = new UBTAuthRepository();
        authLive = AuthLive.getInstance();
        timManager=UbtTIMManager.getInstance();
    }

    @Override
    public void onTVSLoginSuccess(LoginInfo userInfo) {
        ubtAuthRepository.login(userInfo, this);
    }


    @Override
    public void onSuccess(UserInfo userInfo) {
        authLive.logined(userInfo);
        timManager.loginTIM(userInfo.getUserId(),BuildConfig.IM_Channel);
    }

    @Override
    public void onError() {
        tvsAuthRepository.logout();
        authLive.error();
        UbtLogger.i(TAG,"onError");
    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onLogoutError() {
        UbtLogger.i(TAG,"onLogoutError");
    }

    @Override
    public void onCancel() {
        UbtLogger.i(TAG,"cancel");
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

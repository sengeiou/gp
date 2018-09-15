package com.ubtechinc.goldenpig.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;


import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.login.repository.UBTAuthRepository;
import com.ubtechinc.goldenpig.net.GetRobotListModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListRepository;
import com.ubtechinc.goldenpig.repository.TVSAuthRepository;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
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
    private GetPigListRepository getPigListRepository; //获取用户当前小猪列表
    private UbtTIMManager timManager;
    public LoginModel(){
        tvsAuthRepository = new TVSAuthRepository(BuildConfig.APP_ID_WX,BuildConfig.APP_ID_QQ);
        ubtAuthRepository = new UBTAuthRepository();
        getPigListRepository=new GetPigListRepository();
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

        timManager.loginTIM(userInfo.getUserId(), com.ubt.imlibv2.BuildConfig.IM_Channel);
        getPigList();
    }
    ///  获取用户绑定的小猪
    private void getPigList(){
        if (AuthLive.getInstance().getCurrentPigList()!=null){
            AuthLive.getInstance().getCurrentPigList().clear();
        }
        getPigListRepository.getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new GetPigListRepository.OnGetPigListLitener() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("getPigList",e.getMessage());
            }

            @Override
            public void onException(Exception e) {
                Log.e("getPigList",e.getMessage());
            }

            @Override
            public void onSuccess(String response) {
                Log.e("getPigList",response);
                PigUtils.getPigList(response,AuthLive.getInstance().getUserId(),AuthLive.getInstance().getCurrentPigList());
            }
        });
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
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tvsAuthRepository.onActivityResult(requestCode, resultCode, data);
    }

    public void setTIMLoingCallback(UbtTIMManager.UbtIMCallBack callback){
        if (timManager!=null){
            timManager.setUbtCallBack(callback);
        }
    }


}

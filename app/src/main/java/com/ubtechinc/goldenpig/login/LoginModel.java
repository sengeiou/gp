package com.ubtechinc.goldenpig.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tencent.ai.tvs.info.UserInfoManager;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.login.repository.UBTAuthRepository;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.repository.TVSAuthRepository;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.tvlloginlib.entity.LoginInfo;

/**
 * @auther :hqt
 * @description :这是ILoginModel接口定义，登录MVP中Modle定义
 * @time :2018/8/21 11:14
 * @change :目前的整个登录模式进入SplashActivityr后一层层调用TVSManager的refreshLoginToken方法（主要是先作tvslogin），这步主要是在TVSManager和BaseClient
 * 类中完成，再层层回调先在TVSManager的onLoginSuccess中知道tvs登陆成功（获取LoginInfo），再回到LoginModel中的onTVSLoginSuccess
 * ，这时再开始第二步登陆，这步主要在UBTAuthRepository中连接UBT后台登陆，最终返回到LoginModel.onSuccess(UserInfo),在LoginModel.onSuccess(UserInfo)
 * 中用TIM登陆和获取绑定八戒列表，下一步作TIM登陆，主要是UbtTIMManager类，这个先作TIMRepository登陆获取数据，再调用UbtTIMManager
 * .dealIMResponse（）方法登陆，LoginModel是总的登陆管理分发类;
 * @changetime :2018/8/21 11:14
 */
public class LoginModel implements TVSAuthRepository.AuthCallBack, UBTAuthRepository.UBTAuthCallBack {
    private final String TAG = "LoginModel";
    private TVSAuthRepository tvsAuthRepository;
    private AuthLive authLive;
    private UBTAuthRepository ubtAuthRepository;
    private GetPigListHttpProxy getPigListRepository; //获取用户当前八戒列表
    private UbtTIMManager timManager;

    public LoginModel() {
        tvsAuthRepository = new TVSAuthRepository(BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ);
        ubtAuthRepository = new UBTAuthRepository();
        getPigListRepository = new GetPigListHttpProxy();
        authLive = AuthLive.getInstance();
        timManager = UbtTIMManager.getInstance();
    }

    @Override
    public void onTVSLoginSuccess(LoginInfo userInfo) {
        ubtAuthRepository.login(userInfo, this);
    }


    @Override
    public void onSuccess(UserInfo userInfo) {
//        authLive.logined(userInfo);
        Log.d(TAG, "onSuccess  " + userInfo.getUserId() + userInfo.getUserImage());
        getPigList(userInfo);
    }

    ///  获取用户绑定的八戒
    private void getPigList(final UserInfo userInfo) {
        if (AuthLive.getInstance().getCurrentPigList() != null) {
            AuthLive.getInstance().getCurrentPigList().clear();
        }
        getPigListRepository.getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new
                GetPigListHttpProxy.OnGetPigListLitener() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.e("getPigList", e.getMessage());
                        authLive.error();
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e("getPigList", e.getMessage());
                        authLive.error();
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e("getPigList", response);
                        authLive.logined(userInfo);
                        PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance()
                                .getCurrentPigList());
                        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();

                        if (pigInfo != null && pigInfo.isAdmin) {
                            UbtTIMManager.avatarURL = UserInfoManager.getInstance().headImgUrl;
                            timManager.loginTIM(AuthLive.getInstance().getUserId(), pigInfo.getRobotName(), com.ubt.imlibv2.BuildConfig.IM_Channel);
//                            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
                        } else {
                            UbtTIMManager.avatarURL = UserInfoManager.getInstance().headImgUrl;
                            timManager.setUserId(AuthLive.getInstance().getUserId());
                            timManager.setPigAccount(pigInfo != null ? pigInfo.getRobotName() : "");
                            timManager.setChannel(com.ubt.imlibv2.BuildConfig.IM_Channel);
                        }
                    }
                });
    }

    @Override
    public void onError() {
        if (tvsAuthRepository != null) {
            tvsAuthRepository.logout();
        }
        authLive.error();
        UbtLogger.i(TAG, "onError");
    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onLogoutError() {
        UbtLogger.i(TAG, "onLogoutError");
    }

    @Override
    public void onCancel() {
        UbtLogger.i(TAG, "cancel");
        authLive.cancel();
    }

    public boolean isWXInstall() {
        return tvsAuthRepository.isWXInstall();
    }

    public boolean isWXSupport() {
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

    public void logoutTVS() {
        tvsAuthRepository.logout();
    }

    public void onResume() {
        tvsAuthRepository.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tvsAuthRepository.onActivityResult(requestCode, resultCode, data);
    }

}

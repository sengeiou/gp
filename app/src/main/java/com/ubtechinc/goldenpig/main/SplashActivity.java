package com.ubtechinc.goldenpig.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :闪屏页
 *@time          :2018/9/15 10:11
 *@change        :
 *@changetime    :2018/9/15 10:11
*/
public class SplashActivity extends BaseActivity {
    private NetworkHelper.NetworkInductor mInductor;
    private LoginModel mLoginModel =null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        checkLogin();
    }
    private void checkLogin(){
        mInductor = new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    ToastUtils.showShortToast(SplashActivity.this,getString(R.string.ubt_network_unconnect));
                }else {

                }
            }
        };
        NetworkHelper.sharedHelper().addNetworkInductor(mInductor);
        registerProxy();
    }
    private void registerProxy() {
        if (mLoginModel==null) {
            mLoginModel = new LoginModel();
        }
        mLoginModel.setTIMLoingCallback(new UbtTIMManager.UbtIMCallBack() {
            @Override
            public void onError(int i, String s) {
                ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class,true);
            }

            @Override
            public void onSuccess() {
                ActivityRoute.toAnotherActivity(SplashActivity.this, MainActivity.class,true);
            }
        });
        if (mLoginModel.checkToken(this)) {

        }else{
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class,true);
                }
            },2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginModel!=null){
            mLoginModel.onCancel();
            mLoginModel.logoutTVS();
        }
    }
}

package com.ubtechinc.goldenpig.main;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.route.ActivityRoute;


/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :闪屏页
 * @time :2018/9/15 10:11
 * @change :
 * @changetime :2018/9/15 10:11
 */
public class SplashActivity extends BaseActivity {

    private NetworkHelper.NetworkInductor mInductor;
    private LoginModel mLoginModel = null;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
//        String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.i("ANDOIRID", m_szAndroidID + "==" + android.os.Build.SERIAL);
        handler = new Handler();
        registerEventObserve();
        checkLogin();
    }

    private void registerEventObserve() {
        AuthLive.getInstance().observe(this, new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                AuthLive.AuthState mState = authLive.getState();
                switch (mState) {
                    case LOGINING:
                        showLoadingDialog();
                        break;
                    case TVSLOGINED:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadDialog();
                                ActivityRoute.toAnotherActivity(SplashActivity.this, MainActivity.class, true);
                            }
                        }, 2000);
                        break;
                    case ERROR:
                        ToastUtils.showShortToast(SplashActivity.this, getString(R.string.ubt_login_failure));
                    case NORMAL:
                    case CANCEL:
                        ///向下传递处理
                    default:
                        dismissLoadDialog();
                        break;
                }
            }
        });
    }

    private void checkLogin() {
//        mInductor = new NetworkHelper.NetworkInductor() {
//            @Override
//            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
//                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
//                    ToastUtils.showShortToast(SplashActivity.this, getString(R.string.ubt_network_unconnect));
//                } else {
//
//                }
//            }
//        };
//        NetworkHelper.sharedHelper().addNetworkInductor(mInductor);
        registerProxy();
    }

    private void registerProxy() {
        if (mLoginModel == null) {
            mLoginModel = new LoginModel();
        }
//        mLoginModel.setTIMLoingCallback(new UbtTIMManager.UbtIMCallBack() {
//            @Override
//            public void onError(int i, String s) {
//                ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class, true);
//            }
//
//            @Override
//            public void onSuccess() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                ActivityRoute.toAnotherActivity(SplashActivity.this, MainActivity.class, true);
//            }
//        });
        if (!mLoginModel.checkToken(this)) {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class, true);
                }
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginModel != null) {
            mLoginModel.onCancel();
            //mLoginModel.logoutTVS();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }
}

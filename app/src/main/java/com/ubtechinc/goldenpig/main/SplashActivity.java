package com.ubtechinc.goldenpig.main;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


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
    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setBackgroundDrawable(null);
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
                        }, 500);
                        break;
                    case ERROR:
                        dismissLoadDialog();
                        ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class, true);
                        break;
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

    private void unRegisterEventObserve() {
        AuthLive.getInstance().removeObservers(this);
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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class, true);
                }
            }, 500);
        } else {
            disposable = Observable.timer(10, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        //TODO 超时
                        ToastUtils.showShortToast("自动登录超时，请重新登录");
                        ActivityRoute.toAnotherActivity(SplashActivity.this, LoginActivity.class, true);
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventObserve();
        if (mLoginModel != null) {
            mLoginModel.onCancel();
            //mLoginModel.logoutTVS();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }
}

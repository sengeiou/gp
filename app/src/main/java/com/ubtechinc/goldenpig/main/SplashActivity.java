package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.ubtech.utilcode.utils.ToastUtils;
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

    private LoginModel mLoginModel = null;

    private Handler handler;

    private Disposable disposable;

    private boolean isTimeOut;

    private Class<? extends Activity> enterClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        checkScanStartup();
        handler = new Handler();
        registerEventObserve();
        checkLogin();
    }

    private void checkScanStartup() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
    }

    private void doEnterProcess() {
        if (isTimeOut) {
            ActivityRoute.toAnotherActivity(SplashActivity.this, enterClass, true);
        }
    }

    private void registerEventObserve() {
        AuthLive.getInstance().observe(this, authLive -> {
            AuthLive.AuthState mState = authLive.getState();
            switch (mState) {
                case LOGINING:
                    showLoadingDialog();
                    break;
                case TVSLOGINED:
                    handler.postDelayed(() -> {
                        dismissLoadDialog();
                        isTimeOut = true;
                        enterClass = MainActivity.class;
                        doEnterProcess();
                    }, 500);
                    break;
                case ERROR:
                    dismissLoadDialog();
                    isTimeOut = true;
                    enterClass = LoginActivity.class;
                    doEnterProcess();
                    break;
                case NORMAL:
                case CANCEL:
                    ///向下传递处理
                default:
                    dismissLoadDialog();
                    break;
            }
        });
    }

    private void unRegisterEventObserve() {
        AuthLive.getInstance().removeObservers(this);
    }

    private void checkLogin() {
        if (mLoginModel == null) {
            mLoginModel = new LoginModel();
        }
        if (!mLoginModel.checkToken(this)) {
            handler.postDelayed(() -> {
                enterClass = LoginActivity.class;
                isTimeOut = true;
                doEnterProcess();
            }, 500);
        } else {
            disposable = Observable.timer(15, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        //TODO 超时
                        ToastUtils.showShortToast("自动登录超时，请重新登录");
                        enterClass = LoginActivity.class;
                        isTimeOut = true;
                        doEnterProcess();
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventObserve();
        if (mLoginModel != null) {
            mLoginModel.onCancel();
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

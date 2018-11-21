package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;
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

    private static final int REQUEST_CODE_PERMISSION_MULTI = 0x101;

    private boolean isPermissionCompleted;

    private boolean isTimeOut;

    private Class<? extends Activity> enterClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        applyPermission();
        handler = new Handler();
        registerEventObserve();
        checkLogin();
    }

    private void applyPermission() {
        // 申请多个权限。
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_MULTI)
                .permission(Permission.LOCATION, Permission.STORAGE, Permission.MICROPHONE, Permission.CAMERA)
                .callback(permissionListener)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        rationale.resume();
                    }
                })
                .start();
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_MULTI:
                    isPermissionCompleted = true;
                    comparePermissResult();
                    break;
                default:
                    break;

            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_MULTI:
                    SplashActivity.this.finish();
                    break;
                default:
                    break;
            }

        }

    };

    private void comparePermissResult() {
        if (isPermissionCompleted && isTimeOut) {
            ActivityRoute.toAnotherActivity(SplashActivity.this, enterClass, true);
        }
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
                                isTimeOut = true;
                                enterClass = MainActivity.class;
                                comparePermissResult();
                            }
                        }, 500);
                        break;
                    case ERROR:
                        dismissLoadDialog();
                        isTimeOut = true;
                        enterClass = LoginActivity.class;
                        comparePermissResult();
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
        if (!mLoginModel.checkToken(this)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterClass = LoginActivity.class;
                    isTimeOut = true;
                    comparePermissResult();
                }
            }, 500);
        } else {
            disposable = Observable.timer(15, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        //TODO 超时
                        ToastUtils.showShortToast("自动登录超时，请重新登录");
                        enterClass = LoginActivity.class;
                        isTimeOut = true;
                        comparePermissResult();
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

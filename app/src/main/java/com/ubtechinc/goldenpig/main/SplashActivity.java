package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.widget.UBTUpdateDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTUpdateProgressDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;
import com.ubtechinc.push.UbtPushManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.APP_UPDATE_CHECK;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CHECK_NO_UPDATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CHECK_UPDATE_ERROR;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_CANCLE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_FAILED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_PROGRESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_STAR;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DOWNLOAD_APK_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NO_NEED_CHECK;


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

    private DownloadUtils downloadUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(null);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        checkScanStartup();
        handler = new Handler();
        registerEventObserve();
        EventBusUtil.register(this);
        downloadUtils = new DownloadUtils();

        handleEnter();

//        checkUpdate();
    }

    private void handleEnter() {
        String message = UbtPushManager.getInstance().getPushClickResultForXG(this);
        if (message != null) {
            if (isTaskRoot()) {
                return;
            }
            switchToFuckActivity(message);
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        disposable = Observable.timer(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> checkUpdate());
    }



    private void switchToFuckActivity(String message) {

        try {
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.optString("t");
            if ("1".equals(type)) {
                String url = jsonObject.optString("v");
                HashMap<String, String> map = new HashMap<>();
                map.put("push_url", url);
                ActivityRoute.toAnotherActivity(this, PushSwitchActivity.class, map, true);
            }
        } catch (Exception e) {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkP();
    }

    private void checkUpdate(){
        if(downloadUtils == null){
            downloadUtils = new DownloadUtils();
        }
        downloadUtils.doCheckUpdate(this);
    }

    private UBTUpdateProgressDialog updialog;
    private void showDialog(Context context){
        if(updialog == null){
            updialog = new UBTUpdateProgressDialog(context);
        }
        if(updialog.isShowing()){
            return;
        }
        updialog.show();
    }

    private UBTUpdateDialog dialog;

    public void showUpdateDialog(Context context, UpdateInfoModel updateInfoModel) {
        if(dialog == null){
            dialog = new UBTUpdateDialog(context);
        }
        if(dialog.isShowing()){
            return;
        }
        dialog.setRightBtnColor(ContextCompat.getColor(context, R.color.ubt_tab_btn_txt_checked_color));
        dialog.setTips("发现新版本" + updateInfoModel.getVersion());
        dialog.setSubTips(updateInfoModel.getVersionInfo());
        dialog.setSubTipGravity(Gravity.CENTER);
        dialog.setLeftButtonTxt("下次再说");
        dialog.setRightButtonTxt("立即更新");
        if (updateInfoModel.getUpdateType().equals("2")) {
            dialog.setOnlyOneButton();
            dialog.showNoTip(false);
        }else{
            dialog.showNoTip(true);
        }
        dialog.setOnUbtDialogContentClickLinsenter(new UBTUpdateDialog.OnUbtDialogContentClickLinsenter() {
            @Override
            public void onNotipClick(View view) {
                //TODO sp记录勾选状态
                if (view.isSelected()) {
                    SharedPreferencesUtils.putString(context, "isNotNeedShow", updateInfoModel.getVersion());
                } else {
                    SharedPreferencesUtils.putString(context, "isNotNeedShow", "");
                }
            }
        });
        dialog.setOnUbtDialogClickLinsenter(new UBTUpdateDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {
                Event event = new Event(DOWNLOAD_APK_CANCLE);
                EventBusUtil.sendEvent(event);
                dialog.dismiss();
            }

            @Override
            public void onRightButtonClick(View view) {

//                new DownloadUtils().downloadApk((BaseActivity) context, updateInfoModel.getUrl());
//                dialog.dismiss();
                ActivityRoute.toAnotherActivity((Activity) context, CommonWebActivity.class,
                        UbtWebHelper.getUpdateInfoWebviewData(SplashActivity.this, updateInfoModel.getUrl()), false);


            }
        });
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null || isFinishing()) {
            return;
        }
        int code = event.getCode();
        switch (code) {
            case NO_NEED_CHECK:
                checkLogin();
                break;
            case APP_UPDATE_CHECK:
                String version = ((UpdateInfoModel) event.getData()).getVersion();
                String  noNeedVersion = SharedPreferencesUtils.getString(this, "isNotNeedShow", "");
                UbtLogger.d("APP_UPDATE_CHECK", "update version:" + version + "local noNeedVersion:" + noNeedVersion);
                if (noNeedVersion.equalsIgnoreCase(version)) {
                    checkLogin();
                }else{
                    showUpdateDialog(this, (UpdateInfoModel) event.getData());
                }

                break;
            case DOWNLOAD_APK_CANCLE:
                checkLogin();
                break;
            case DOWNLOAD_APK_STAR:
                showDialog(this);
                break;
            case DOWNLOAD_APK_PROGRESS:
                if (updialog != null) {
                    UbtLogger.d("DOWNLOAD_APK_PROGRESS", "DOWNLOAD_APK_PROGRESS:" + event.getData());
                    updialog.updateProgress((int) event.getData());
                }
                break;
            case DOWNLOAD_APK_SUCCESS:
                if (updialog != null) {
                    updialog.dismiss();
                }
                ToastUtils.showShortToast("下载成功");
                UbtLogger.d("wmma", "ssssss");
                DownloadUtils.installApk(this);
                break;
            case DOWNLOAD_APK_FAILED:
                if (updialog != null) {
                    updialog.dismiss();
                }
                ToastUtils.showShortToast("下载失败");
                break;
            case CHECK_NO_UPDATE:
                checkLogin();
                break;
            case CHECK_UPDATE_ERROR:
                checkLogin();
                break;
                default:
        }
    }

    private void checkP(){
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(this)
                    .requestCode(0x1111)
                    .permission(Permission.STORAGE)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            //获取权限成功，下载apk
//                            realDownloadApk(activity, url);
                            checkUpdate();
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            showPermissionDialog(Permission.STORAGE);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();

        } else {
            checkUpdate();
        }
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
        EventBusUtil.unregister(this);
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
//        return R.layout.activity_splash;
        return 0;
    }
}

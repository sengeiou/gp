package com.ubtechinc.goldenpig.base;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.tqzhang.stateview.core.LoadManager;
import com.tqzhang.stateview.stateview.BaseStateControl;
import com.ubtechinc.commlib.utils.StatusBarUtil;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PermissionPageUtils;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe : 基础Activity
 * @time :2018/8/17 17:58
 * @change :
 * @changTime :2018/8/17 17:58
 */
public abstract class BaseActivity extends AppCompatActivity {

    Unbinder unbinder;

    private UBTSubTitleDialog dialog;

    protected LoadManager loadManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        StatusBarUtil.setStatusBarTextColor(this, false);
        //设置窗口背景减少过度绘制
//        getWindow().setBackgroundDrawable(null);
//        getWindow().setBackgroundDrawableResource(R.drawable.splash_icon);

        ActivityManager am = ActivityManager.getInstance();
        am.pushActivity(this);
        if (isForbiddenSnapShot()) {
            //防止截屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        int layoutResID = getContentViewId();
        if (layoutResID != 0 && layoutResID != -1) {
            setContentView(layoutResID);
        }
        if (needPreLoad()) {
            initLoadState();
        }
        unbinder = ButterKnife.bind(this);
        EventBusUtil.register(this);
    }

    private void initLoadState() {
        loadManager = new LoadManager.Builder()
                .setViewParams(this)
                .setListener((BaseStateControl.OnRefreshListener) v -> {
                    onStateRefresh();
                })
                .build();
    }

    /**
     * 状态刷新
     */
    protected void onStateRefresh() {
        //TODO

    }

    /**
     * 是否需要预加载
     *
     * @return
     */
    protected boolean needPreLoad() {
        return false;
    }

    protected boolean isForbiddenSnapShot() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager am = ActivityManager.getInstance();
        am.clearRecord(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventBusUtil.unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackCallBack();
        ActivityManager am = ActivityManager.getInstance();
        am.popActivity(this);
    }

    protected void onBackCallBack() {
        //TODO 后退回调
    }

    public void showLoadingDialog() {
        if (!isFinishing() && !isDestroyed()) {
            LoadingDialog.getInstance(this).show();
        }
    }

    /**
     * 设置布局
     * @return
     */
    protected abstract @LayoutRes int getContentViewId();

    public void dismissLoadDialog() {
        LoadingDialog.dissMiss();
    }

    public void showPermissionDialog(String[] permission) {
        String subTip = "";
        if (permission == Permission.LOCATION) {
            subTip = "使用该功能需要定位权限，请前往系统设置开启权限";
        } else if (permission == Permission.CAMERA) {
            subTip = "使用该功能需要拍照权限，请前往系统设置开启权限";
        } else if (permission == Permission.MICROPHONE) {
            subTip = "使用该功能需要录音权限，请前往系统设置开启权限";
        } else if (permission == Permission.STORAGE) {
            subTip = "使用该功能需要存储权限，请前往系统设置开启权限";
        }
        if (dialog == null) {
            dialog = new UBTSubTitleDialog(this);
            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
            dialog.setTips("权限申请");
            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
            dialog.setRightButtonTxt(getString(R.string.go_setting));
            dialog.setSubTips(subTip);
            dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {
                    //TODO
                }

                @Override
                public void onRightButtonClick(View view) {
                    gotoSetting();
                }
            });
            dialog.show();
        }
        if (dialog != null && !dialog.isShowing() && !isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }

    private void gotoSetting() {
        //TODO 去应用管理设置权限页
        PermissionPageUtils.getInstance(this).jumpPermissionPage();
    }

    protected boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        Configuration configuration = res.getConfiguration();
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
        return res;
    }

    public void toBleConfigActivity(boolean closeSlf) {
        ActivityRoute.toAnotherActivity(this, BleConfigReadyActivity.class, closeSlf);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(Event event) {
        if (event != null) {
            onReceive(event, event.getCode());
        }
    }

    /**
     * eventbus
     *
     * @param event
     * @param code
     */
    protected void onReceive(Event event, int code) {
        //TODO
    }

}

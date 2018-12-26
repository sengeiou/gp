package com.ubtechinc.goldenpig.base;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.ubtechinc.commlib.utils.StatusBarUtil;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PermissionPageUtils;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.Permission;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        StatusBarUtil.setStatusBarTextColor(this, false);
        getWindow().setBackgroundDrawable(null);   ///减少过度绘制

        ActivityManager am = ActivityManager.getInstance();
        am.pushActivity(this);
        if (isForbiddenSnapShot()) {
            //防止截屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(getContentViewId());
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
        LoadingDialog.getInstance(this).show();
    }

    protected abstract int getContentViewId();

    public void dismissLoadDialog() {
        LoadingDialog.dissMiss();
    }

    protected void showPermissionDialog(String[] permission) {
        String subTip = "";
        if (permission == Permission.LOCATION) {
            subTip = "使用该功能需要定位权限，请前往系统设置开启权限";
        } else if (permission == Permission.CAMERA) {
            subTip = "使用该功能需要拍照权限，请前往系统设置开启权限";
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

}

package com.ubtechinc.goldenpig.base;


import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ubtechinc.commlib.utils.StatusBarUtil;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.utils.PermissionPageUtils;
import com.ubtechinc.goldenpig.view.StateView;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author : hdf
 * @describe : 基础Activity
 * @time :2018/9/12 16:03
 */
public abstract class BaseNewActivity extends AppCompatActivity {

    Unbinder unbinder;

    private UBTSubTitleDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        StatusBarUtil.setStatusBarTextColor(this, false);
        getWindow().setBackgroundDrawable(null);   ///减少过度绘制
        if (getContentViewId() != 0) {
            setContentView(getContentViewId());
        }
        unbinder = ButterKnife.bind(this);
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showLoadingDialog() {
        LoadingDialog.getInstance(this).show();
    }

    public void dismissLoadDialog() {
        LoadingDialog.dissMiss();
    }

    /**
     * @return activity布局
     */
    protected abstract int getContentViewId();

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event != null) {
            onReceiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyMessageEvent(Event event) {
        if (event != null) {
            onReceiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void onReceiveEvent(Event event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void onReceiveStickyEvent(Event event) {

    }

    protected StateView mStateView;

    public void initStateView(Boolean hasActionbar) {
        mStateView = StateView.inject(this, hasActionbar);
    }

    public void initStateView(View view, Boolean hasActionbar) {
        mStateView = StateView.inject(view, hasActionbar);
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

    protected void showPermissionDialog(String[] permission) {
        String subTip = "";
        if (permission == Permission.LOCATION) {
            subTip = "使用该功能需要定位权限，请前往系统设置开启权限";
        } else if (permission == Permission.CAMERA) {
            subTip = "使用该功能需要拍照权限，请前往系统设置开启权限";
        } else if (permission == Permission.CONTACTS) {
            subTip = "使用该功能需要读取联系人权限，请前往系统设置开启权限";
        } else if (permission.length == 1 && permission[0] == Manifest.permission.READ_CONTACTS) {
            subTip = "使用该功能需要读取联系人权限，请前往系统设置开启权限";
        }
        if (dialog == null) {
            dialog = new UBTSubTitleDialog(this);
            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color,
                    null));
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
}

package com.ubtechinc.goldenpig.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.view.StateView;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author : hdf
 * @describe : 基础Activity
 * @time :2018/9/12 16:03
 */
public abstract class BaseNewActivity extends AppCompatActivity {
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
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

    protected StateView mStateView;

    public void initStateView(Boolean hasActionbar) {
        mStateView = StateView.inject(this, hasActionbar);
    }

    public void initStateView(View view, Boolean hasActionbar) {
        mStateView = StateView.inject(view, hasActionbar);
    }
}

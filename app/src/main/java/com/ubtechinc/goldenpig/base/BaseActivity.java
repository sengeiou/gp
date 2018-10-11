package com.ubtechinc.goldenpig.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   : 基础Activity
 * @time       :2018/8/17 17:58
 * @change     :
 * @changTime  :2018/8/17 17:58
 */
public abstract class BaseActivity extends AppCompatActivity {
    Unbinder unbinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);   ///减少过度绘制

        ActivityManager am = ActivityManager.getInstance();
        am.pushActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE); //防止截屏
        setContentView(getContentViewId());
        unbinder=ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager am = ActivityManager.getInstance();
        am.clearRecord(this);
        if (unbinder!=null){
            unbinder.unbind();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager am = ActivityManager.getInstance();
        am.popActivity(this);
    }
    public void showLoadingDialog() {
        LoadingDialog.getInstance(this).show();
    }
    protected abstract int getContentViewId();
    public void dismissLoadDialog() {
        LoadingDialog.dissMiss();
    }
}

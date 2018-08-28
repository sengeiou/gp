package com.ubtechinc.goldenpig.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;


/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   : 基础Activity
 * @time       :2018/8/17 17:58
 * @change     :
 * @changTime  :2018/8/17 17:58
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);   ///减少过度绘制

        ActivityManager am = ActivityManager.getInstance();
        am.popActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager am = ActivityManager.getInstance();
        am.clearRecord(this);
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

    public void dismissLoadDialog() {
        LoadingDialog.dissMiss();
    }
}

package com.ubtechinc.goldenpig.pigmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;

import pl.droidsonroids.gif.GifImageView;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :配网确认页面
 * @time :2018/8/23 10:03
 * @change :
 * @changetime :2018/8/23 10:03
 */
public class SetNetWorkEnterActivity extends BaseToolBarActivity implements View.OnClickListener {

    private GifImageView ivBoxStartup;

    private View tvStartupNext;

    private View tvStartupOperate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        udQPAB();
    }

    private void udQPAB() {
        boolean firstEnter = SharedPreferencesUtils.getBoolean(this, "firstEnter", true);
        boolean backable = firstEnter ? false : true;
        boolean skipable = firstEnter ? true : false;
        setTitleBack(backable);
        if (skipable) {
            mTvSkip.setVisibility(View.VISIBLE);
        } else {
            mTvSkip.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getConentView() {
        return R.layout.activity_set_net_enter;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle(getString(R.string.voice_box_open));
        initViews();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtils.putBoolean(this, "firstEnter", false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        udQPAB();
    }

    private void initViews() {
        ivBoxStartup = findViewById(R.id.iv_box_startup);
        ivBoxStartup.post(() -> {
            //ImageUtils.showGif(SetNetWorkEnterActivity.this,mTipsImg,R.drawable.open_pig);
            ivBoxStartup.setImageResource(R.drawable.open_pig);
        });

        tvStartupNext = findViewById(R.id.tv_startup_next);
        tvStartupNext.setOnClickListener(this);

        tvStartupOperate = findViewById(R.id.tv_startup_operate);
        tvStartupOperate.setOnClickListener(this);

        mTvSkip = findViewById(R.id.ubt_tv_set_net_skip);
        mTvSkip.setVisibility(View.VISIBLE);
        mTvSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_startup_next:
                ActivityRoute.toAnotherActivity(this, BleNetWorkConfigActivity.class, false);
                break;
            case R.id.tv_startup_operate:
                onStartupChange();
                break;
            case R.id.ubt_tv_set_net_skip:
                ActivityRoute.toAnotherActivity(this, MainActivity.class, true);
                break;
            default:
                break;
        }
    }

    private void onStartupChange() {
        tvStartupOperate.setSelected(!tvStartupOperate.isSelected());
        tvStartupNext.setEnabled(tvStartupOperate.isSelected());
    }
}

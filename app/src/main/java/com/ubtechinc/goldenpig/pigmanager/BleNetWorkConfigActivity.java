package com.ubtechinc.goldenpig.pigmanager;

import android.os.Bundle;
import android.view.View;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import pl.droidsonroids.gif.GifImageView;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :   长按热点进入配网--引导
 * @time :2018/12/19 11:31
 * @change :
 * @changetime :2018/12/19 11:31
 */
public class BleNetWorkConfigActivity extends BaseToolBarActivity implements View.OnClickListener {

    private GifImageView ivBoxStartup;

    private View tvStartupNext;

    @Override
    protected int getConentView() {
        return R.layout.activity_ble_net_config;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle(getString(R.string.ubt_pig_bind));
        setTitleBack(true);
        initViews();
    }

    private void initViews() {
        ivBoxStartup = findViewById(R.id.iv_box_startup);
        ivBoxStartup.post(() -> {
            ivBoxStartup.setImageResource(R.drawable.pig_mute);
        });

        tvStartupNext = findViewById(R.id.tv_startup_next);
        tvStartupNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_startup_next:
                ActivityRoute.toAnotherActivity(this, BleClosePigActivity.class, false);
                break;
            default:
                break;
        }
    }

}

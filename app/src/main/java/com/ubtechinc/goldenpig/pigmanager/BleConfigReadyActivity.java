package com.ubtechinc.goldenpig.pigmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import pl.droidsonroids.gif.GifImageView;
/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :蓝牙配网权限校验页
 *@time          :2018/12/20 13:49
 *@change        :
 *@changetime    :2018/12/20 13:49
*/
public class BleConfigReadyActivity extends BaseToolBarActivity implements View.OnClickListener {

    private GifImageView ivBoxStartup;

    private View tvBleOpen;

    private View tvBleLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBack(true);
        hiddleTitle();
    }

    @Override
    protected int getConentView() {
        return R.layout.activity_ble_ready;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initViews();
    }

    private void initViews() {
        ivBoxStartup = findViewById(R.id.iv_box_startup);
        ivBoxStartup.post(() -> {
            ivBoxStartup.setImageResource(R.drawable.open_pig);
        });

        tvBleOpen = findViewById(R.id.tv_ble_open);
        tvBleOpen.setOnClickListener(this);

        tvBleLocation = findViewById(R.id.tv_ble_location);
        tvBleLocation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ble_open:
                ActivityRoute.toAnotherActivity(this, BleNetWorkConfigActivity.class, false);
                break;
            case R.id.tv_ble_location:
//                onStartupChange();
                break;
            default:
                break;
        }
    }

//    private void onStartupChange() {
//        tvStartupOperate.setSelected(!tvStartupOperate.isSelected());
//        tvStartupNext.setEnabled(tvStartupOperate.isSelected());
//    }
}

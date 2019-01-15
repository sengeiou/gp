package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :机器人离线
 * @time :2019/1/7 20:47
 * @change :
 * @changetime :2019/1/7 20:47
 */
public class RobotOfflineActivity extends BaseToolBarActivity implements View.OnClickListener {


    private TextView tvSwitchWifi;

    @Override
    protected int getConentView() {
        return R.layout.activity_robot_offline;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("八戒Wi-Fi设置");
        setTitleBack(true);

        tvSwitchWifi = findViewById(R.id.tv_switch_wifi);
        tvSwitchWifi.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_switch_wifi:
                toSwitchWifi();
                break;
        }
    }

    private void toSwitchWifi() {
        ActivityRoute.toAnotherActivity(this, BleConfigReadyActivity.class, false);
    }
}

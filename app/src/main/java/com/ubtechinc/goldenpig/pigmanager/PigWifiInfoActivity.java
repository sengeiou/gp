package com.ubtechinc.goldenpig.pigmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import java.util.HashMap;

public class PigWifiInfoActivity extends BaseToolBarActivity implements View.OnClickListener {

    private String mPigWifiName;

    private ImageView ivWifiLogo;

    private TextView tvWifiInfo;

    private TextView btnConnWifi;

    private TextView btnCurrWifi;

    @Override
    protected int getConentView() {
        return R.layout.activity_wifi_pig;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("选择连网方案");
        setTitleBack(true);
        initViews();
    }

    private void initViews() {
        ivWifiLogo = findViewById(R.id.iv_wifi_logo);
        tvWifiInfo = findViewById(R.id.tv_wifi_info);
        btnConnWifi = findViewById(R.id.btn_conn_wifi);
        btnCurrWifi = findViewById(R.id.btn_curr_wifi);

        Intent intent = getIntent();
        if (intent != null) {
            mPigWifiName = intent.getStringExtra("PigWifiName");
        }
        if (!TextUtils.isEmpty(mPigWifiName)) {
            ivWifiLogo.setImageResource(R.drawable.img_wifi);
            tvWifiInfo.setText("八戒当前已连接“" + mPigWifiName + "”");
            btnCurrWifi.setText("使用当前Wi-Fi");
        } else {
            ivWifiLogo.setImageResource(R.drawable.img_4g);
            tvWifiInfo.setText("八戒当前已连接移动数据网络");
            btnCurrWifi.setText("使用移动数据");
        }
        btnConnWifi.setOnClickListener(this);
        btnCurrWifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_conn_wifi:
                HashMap<String, String> map = new HashMap<>();
                map.put("comingSource", "pigwifi");
                ActivityRoute.toAnotherActivity(PigWifiInfoActivity.this, SetPigNetWorkActivity.class, map, false);
                break;
            case R.id.btn_curr_wifi:
                doSkip();
                break;
            default:
                break;
        }
    }

    private void doSkip() {
        ICommandProduce commandProduce = new JsonCommandProduce();
        String message = commandProduce.checkPigNetWorkState();
        UbtBluetoothManager.getInstance().sendMessageToBle(message);
        ActivityRoute.toAnotherActivity(this, MainActivity.class, true);
    }

    @Override
    protected void onBackCallBack() {
        super.onBackCallBack();
        UbtBluetoothManager.getInstance().closeConnectBle();
    }
}

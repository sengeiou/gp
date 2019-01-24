package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtrobot.info.DeviceInfoContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_PIG_DEVICE_INFO;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_PIG_VERSION;

/**
 * @Description: 关于机器人
 * @Author: zhijunzhou
 * @CreateDate: 2018/12/28 15:52
 */
public class AboutBleBJActivity extends BaseToolBarActivity {

    private TextView tvDsnValue;

    private TextView tvMeidValue;

    private TextView tvImeiValue;

    private TextView tvWfmacValue;

    private TextView tvBlemacValue;

    private TextView tvVersionValue;

    @Override
    protected int getConentView() {
        return R.layout.activity_ble_pig_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setToolBarTitle("关于机器人");
        setTitleBack(true);
        initViews();
    }

    private void initViews() {
        tvDsnValue = findViewById(R.id.tv_dsn_value);
        tvMeidValue = findViewById(R.id.tv_meid_value);
        tvImeiValue = findViewById(R.id.tv_imei_value);
        tvWfmacValue = findViewById(R.id.tv_wfmac_value);
        tvBlemacValue = findViewById(R.id.tv_blemac_value);
        tvVersionValue = findViewById(R.id.tv_version_value);

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            String name = pigInfo.getRobotName();
            tvDsnValue.setText(name);
            getPigInfo(name);
        }
        getPigDeviceInfo();
    }

    private void getPigInfo(String pigDsn) {
        DeviceInfoContainer.DeviceInfo deviceInfo = (DeviceInfoContainer.DeviceInfo) SPUtils.get().readObject("piginfo_basic" + pigDsn);
        if (deviceInfo != null) {
            updateUI(deviceInfo);
        } else {
            getPigDeviceInfo();
        }
        String pigVersion = SPUtils.get().getString("piginfo_version" + pigDsn);
        if (!TextUtils.isEmpty(pigVersion)) {
            tvVersionValue.setText(pigVersion);
        } else {
            getPigVersion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    private void getPigVersion() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersion()));
    }

    private void getPigDeviceInfo() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigDeviceInfo()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        String pigDsn = pigInfo != null ? pigInfo.getRobotName() : "";
        switch (code) {
            case RECEIVE_PIG_VERSION:
                String currentVersion = (String) event.getData();
                SPUtils.get().put("piginfo_version" + pigDsn, currentVersion);
                if (tvVersionValue != null) {
                    tvVersionValue.setText(currentVersion);
                }
                break;
            case RECEIVE_PIG_DEVICE_INFO:
                DeviceInfoContainer.DeviceInfo deviceInfo = (DeviceInfoContainer.DeviceInfo) event.getData();
                SPUtils.get().saveObject("piginfo_basic" + pigDsn, deviceInfo);
                updateUI(deviceInfo);
                break;
        }
    }

    private void updateUI(DeviceInfoContainer.DeviceInfo deviceInfo) {
        String serialNumber = deviceInfo.getSerialNumber();
        updateTvValue(tvDsnValue, serialNumber);

        String meid = deviceInfo.getMeid();
        updateTvValue(tvMeidValue, meid);

        String imei = deviceInfo.getImei();
        updateTvValue(tvImeiValue, imei);

        String wifiMac = deviceInfo.getWifiMac();
        updateTvValue(tvWfmacValue, wifiMac);

        String bleMac = deviceInfo.getBleMac();
        updateTvValue(tvBlemacValue, bleMac);
    }

    private void updateTvValue(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }

}

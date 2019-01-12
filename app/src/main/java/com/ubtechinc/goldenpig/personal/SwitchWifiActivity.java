package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.pigmanager.SetPigNetWorkActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtrobot.info.NativeInfoContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_NATIVE_INFO;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :Wi-Fi设置
 * @time :2019/1/7 20:47
 * @change :
 * @changetime :2019/1/7 20:47
 */
public class SwitchWifiActivity extends BaseToolBarActivity implements View.OnClickListener {

    private TextView tvWifiValue;

    private ImageView ivWifiIcon;

    private TextView tvConnWifi;

    @Override
    protected int getConentView() {
        return R.layout.activity_switch_wifi;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("八戒Wi-Fi设置");
        setTitleBack(true);

        EventBusUtil.register(this);
        tvWifiValue = findViewById(R.id.tv_wifi_value);
        ivWifiIcon = findViewById(R.id.iv_wifi_icon);
        tvConnWifi = findViewById(R.id.tv_conn_wifi);
        tvConnWifi.setOnClickListener(this);

        UbtTIMManager.getInstance().queryNativeInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case RECEIVE_NATIVE_INFO:
                updateNativeInfo((NativeInfoContainer.NativeInfo) event.getData());
                break;
        }
    }

    private void updateNativeInfo(NativeInfoContainer.NativeInfo data) {
        NativeInfoContainer.NativeInfo nativeInfo = data;
        try {
            NativeInfoContainer.NetworkStatus networkStatus = nativeInfo.getNetworkStatus().unpack(NativeInfoContainer.NetworkStatus.class);

            if (networkStatus.getWifiState()) {
                ivWifiIcon.setImageResource(R.drawable.ic_wifi);
                tvWifiValue.setText(networkStatus.getSsid());
                tvConnWifi.setText(R.string.switch_wifi);
            } else {
                ivWifiIcon.setImageResource(R.drawable.ic_no_wifi);
                tvWifiValue.setText("未开启");
                tvConnWifi.setText(R.string.connect_wifi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_conn_wifi:
                toSetWifi();
                break;
        }
    }

    private void toSetWifi() {
        HashMap<String, String> map = new HashMap<>();
        map.put("comingSource", "switchwifi");
        ActivityRoute.toAnotherActivity(this, SetPigNetWorkActivity.class, map, false);
    }
}

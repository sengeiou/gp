package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.widget.TextView;

import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_PIG_VERSION;

/**
 * @Description: 关于八戒音箱
 * @Author: zhijunzhou
 * @CreateDate: 2018/12/28 15:52
 */
public class AboutBleBJActivity extends BaseToolBarActivity {

    private TextView tvDsnValue;

    private TextView tvVersionValue;


    @Override
    protected int getConentView() {
        return R.layout.activity_ble_pig_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setToolBarTitle("关于八戒机器人");
        setTitleBack(true);
        initViews();
    }

    private void initViews() {
        tvDsnValue = findViewById(R.id.tv_dsn_value);
        tvVersionValue = findViewById(R.id.tv_version_value);

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            String name = pigInfo.getRobotName();
            tvDsnValue.setText(name);
        }
        getPigVersion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    private void getPigVersion() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersion()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case RECEIVE_PIG_VERSION:
                if (tvVersionValue != null) {
                    tvVersionValue.setText((String) event.getData());
                }
                break;
        }
    }

}

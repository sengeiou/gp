package com.ubtechinc.goldenpig.about;

import android.os.Bundle;
import android.widget.TextView;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.push.PushAppInfo;
import com.ubtechinc.goldenpig.push.PushHttpProxy;
import com.ubtechinc.nets.utils.DeviceUtils;

import java.util.HashMap;
import java.util.Map;

public class EggShellActivtiy extends BaseToolBarActivity {

    private UbtTIMManager mUbtTIMManager;


    @Override
    protected int getConentView() {
        return R.layout.activity_egg_shell;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle("后门");

        findViewById(R.id.ubt_btn_adb_open).setOnClickListener(v -> {
            //TODO 打开adb
            openADB(true);
        });

        findViewById(R.id.ubt_btn_adb_close).setOnClickListener(v -> {
            //TODO 关闭adb
            openADB(false);
        });

        TextView tvDevId = findViewById(R.id.tv_devId);
        tvDevId.setText(getResources().getString(R.string.ubt_devid, DeviceUtils.getDeviceId(this)));

        TextView tvIMStatus = findViewById(R.id.tv_im_status);
        tvIMStatus.setText(getResources().getString(R.string.ubt_im_status, UbtTIMManager.getInstance().isLoginedTIM() ? "在线" : "离线"));

        TextView tvPushStatus = findViewById(R.id.tv_push_status);
        PushAppInfo pushAppInfo = AuthLive.getInstance().getPushAppInfo();
        tvPushStatus.setText(getResources().getString(R.string.ubt_push_channel, pushAppInfo.isBindStatus() ? "正常" : "异常"));

        findViewById(R.id.bt_test_push).setOnClickListener(v -> {
            try {
                PushHttpProxy pushHttpProxy = new PushHttpProxy();
                Map map = new HashMap();
                map.put("app_category", 1);
                String userId = AuthLive.getInstance().getUserId();
                pushHttpProxy.pushToken("Gold Pig", "Test push by myself", userId, map, 1);
            } catch (Exception e) {
                //TODO
            }
        });

    }

    private void openADB(boolean open) {
        if (mUbtTIMManager == null) {
            mUbtTIMManager = UbtTIMManager.getInstance();
        }
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.adbOperate(open));
            TIMConversation pigConversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, pigInfo.getRobotName());
            mUbtTIMManager.sendTIM(selfMessage, pigConversation);
        }
    }

}


package com.ubtechinc.goldenpig.about;

import android.os.Bundle;

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


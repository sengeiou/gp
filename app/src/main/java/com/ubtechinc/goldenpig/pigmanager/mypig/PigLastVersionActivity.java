package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;

public class PigLastVersionActivity extends BaseToolBarActivity {
    @Override
    protected int getConentView() {
        return R.layout.activity_last_version;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_device_version_update);

        Intent intent = getIntent();
        if (intent != null) {
            String latestVersion = intent.getStringExtra("latestVersion");
            if (!TextUtils.isEmpty(latestVersion)) {
                TextView tvVersionValue = findViewById(R.id.tv_pig_version_value);
                tvVersionValue.setText(latestVersion);
            }
        }

        getRobotOTAResult();
    }

    private void getRobotOTAResult() {
        if (!SharedPreferencesUtils.getBoolean(this, "hasTipOTAResult", false)) {
            UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getRobotUpdateResult()));
        }
    }


}

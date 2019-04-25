package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.CheckUtil;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;

import butterknife.ButterKnife;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :设备升级界面
 * @time :2018/9/15 16:48
 * @change :
 * @changetime :2018/9/15 16:48
 */
public class DeviceUpdateActivity extends BaseToolBarActivity implements View.OnClickListener {

    TextView mVersionTv;

    TextView mCurrVersionTv;

    TextView mMsgTv;

    TextView mTvUpdate;

    @Override
    protected int getConentView() {
        return R.layout.activity_dev_update;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_device_version_update);

        mCurrVersionTv = findViewById(R.id.tv_current_version);
        mVersionTv = findViewById(R.id.tv_new_version);
        mMsgTv = findViewById(R.id.ubt_tv_version_msg);
//        mMsgTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvUpdate = findViewById(R.id.tv_ota_update);
        mTvUpdate.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            String currentVersion = intent.getStringExtra("currentVersion");
            String latestVersion = intent.getStringExtra("latestVersion");
            String updateMessage = intent.getStringExtra("updateMessage");
            String status = intent.getStringExtra("status");
            if (!TextUtils.isEmpty(currentVersion)) {
                mCurrVersionTv.setText(currentVersion);
            }
            if (!TextUtils.isEmpty(latestVersion)) {
                mVersionTv.setText(getString(R.string.ubt_latest_version_format, latestVersion));
            }
            if (!TextUtils.isEmpty(updateMessage)) {
                Log.d("updateMessage", updateMessage);
                mMsgTv.setText(updateMessage);
            }
            if ("3".equals(status)) {
                mTvUpdate.setText("升级中...");
                mTvUpdate.setEnabled(false);
            }
        }
        initIM();
        getRobotOTAResult();
    }

    private void initIM() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
    }

    private void sendUpdate() {
        SharedPreferencesUtils.putBoolean(this, "hasTipOTAResult", false);
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.updatePigVersion()));
    }

    private void getRobotOTAResult() {
        if (!SharedPreferencesUtils.getBoolean(this, "hasTipOTAResult", false)) {
            UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getRobotUpdateResult()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ota_update:
                if (!CheckUtil.checkPhoneNetState(this, false)) {
                    showIKnowDialog(getResources().getString(R.string.network_error));
                    return;
                }
                if (!CheckUtil.checkRobotOnlineState(this)) {
                    return;
                }
                mTvUpdate.setText("升级中...");
                mTvUpdate.setEnabled(false);
                sendUpdate();
                break;
            default:
        }
    }

}

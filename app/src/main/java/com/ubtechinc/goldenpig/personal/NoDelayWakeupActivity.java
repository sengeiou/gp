package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.util.Log;

import com.suke.widget.SwitchButton;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @Description: 关机闹钟
 * @Author: hdf
 * @CreateDate: 2019/03/22 14:22
 */
public class NoDelayWakeupActivity extends BaseToolBarActivity implements SwitchButton.OnCheckedChangeListener {

    private static final String TAG = NoDelayWakeupActivity.class.getSimpleName();

    private SwitchButton switchVoice;

    private boolean isIgnoreChange;

    @Override
    protected int getConentView() {
        return R.layout.activity_nodelay_wakeup;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setToolBarTitle("唤醒音零延时");
        setTitleBack(true);
        initViews();
    }

    private void initViews() {
        switchVoice = findViewById(R.id.switch_voice);
        switchVoice.setOnCheckedChangeListener(this);

        if (UBTPGApplication.isNetAvailable) {
            if (UBTPGApplication.isRobotOnline) {
                getContinuousVoiceState();
            } else {
                UbtToastUtils.showCustomToast(this, "八戒处于离线状态\n获取状态失败");
            }
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.network_error));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    private void getContinuousVoiceState() {
        Log.d(TAG, "getContinuousVoiceState" );
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder
                .requestNoDelayWakeupState()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case EventBusUtil.RECEIVE_NO_DELAY_WAKEUP_STATE: {
                boolean result = (boolean) event.getData();
                Log.d(TAG, "get state:" + result);
                updateSwitchBtn(result);
            }
            break;
            case EventBusUtil.RECEIVE_NO_DELAY_WAKEUP_SWITCH_STATE: {
                UbtLogger.d(TAG, "onReceiveEvent RECEIVE_NO_DELAY_WAKEUP_SWITCH_STATE");
                boolean result = (boolean) event.getData();
                if (result) {
                    ToastUtils.showShortToast("设置成功");
                } else {
                    ToastUtils.showShortToast("设置失败");
                    updateSwitchBtn(!switchVoice.isChecked());
                }
            }
            break;
        }
    }

    private void updateSwitchBtn(boolean result) {
        if (switchVoice != null) {
            if (switchVoice.isChecked() != result) {
                isIgnoreChange = true;
                switchVoice.setChecked(result);
            } else {
                isIgnoreChange = false;
            }
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isIgnoreChange) {
            isIgnoreChange = false;
        } else {
            doSwitchContinuousVoice(isChecked);
        }
    }

    private void doSwitchContinuousVoice(boolean isChecked) {
        Log.d(TAG, "set isChecked:" + isChecked);
        if (UBTPGApplication.isNetAvailable) {
            if (UBTPGApplication.isRobotOnline) {
                UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder
                        .requestNoDelayWakeupSwitch(isChecked)));
            } else {
                UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline));
            }
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.network_error));
        }
    }
}

package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.util.Log;

import com.suke.widget.SwitchButton;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_CONTINUOUS_VOICE_RESPONSE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_CONTINUOUS_VOICE_STATE;

/**
 * @Description: 连续语音对话
 * @Author: zhijunzhou
 * @CreateDate: 2018/12/28 15:52
 */
public class ContinuousVoiceActivity extends BaseToolBarActivity implements SwitchButton.OnCheckedChangeListener {

    private SwitchButton switchVoice;

    private boolean isIgnoreChange;

    @Override
    protected int getConentView() {
        return R.layout.activity_continuous_voice;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setToolBarTitle("连续对话模式");
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
                UbtToastUtils.showCustomToast(this, "八戒处于离线状态\n获取连续对话模式开关失败");
            }
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.network_error_toast));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    private void getContinuousVoiceState() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.requestContinuousVoiceState()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case RECEIVE_CONTINUOUS_VOICE_STATE: {
                boolean result = (boolean) event.getData();
                updateSwitchBtn(result);
            }
            break;
            case RECEIVE_CONTINUOUS_VOICE_RESPONSE: {
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
        Log.d("ContinuousVoice", "set isChecked:" + isChecked);
        if (UBTPGApplication.isNetAvailable) {
            if (UBTPGApplication.isRobotOnline) {
                UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.requestContinuousVoiceSwitch(isChecked)));
            } else {
                UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline));
            }
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.network_error_toast));
        }
    }
}

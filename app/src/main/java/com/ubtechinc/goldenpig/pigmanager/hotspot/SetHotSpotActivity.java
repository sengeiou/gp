package com.ubtechinc.goldenpig.pigmanager.hotspot;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.gold.UserContacts;

import java.util.Observable;
import java.util.Observer;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :设置WIFI热点
 * @time :2018/9/18 11:10
 * @change :
 * @changetime :2018/9/18 11:10
 */
public class SetHotSpotActivity extends BaseToolBarActivity implements Observer, View.OnClickListener {

    private EditText etHotName, etHotPwd;

    private String hotSpotName, hotSpotPwd;

    private View ivHotNameClear, ivHotPwdClear;

    @Override
    protected int getConentView() {
        return R.layout.activity_set_wifi_hotspot;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_person_hotspot);
        initViews();
        process();
    }

    private void initViews() {
        mTvSkip = findViewById(R.id.ubt_tv_set_net_skip);
        mTvSkip.setVisibility(View.INVISIBLE);
        mTvSkip.setText(R.string.chat_save);
        mTvSkip.setOnClickListener(this);

        etHotName = findViewById(R.id.et_hot_name);
        etHotPwd = findViewById(R.id.et_hot_pwd);

        ivHotNameClear = findViewById(R.id.iv_hot_name_clear);
        ivHotNameClear.setOnClickListener(this);
        ivHotPwdClear = findViewById(R.id.iv_hot_pwd_clear);
        ivHotPwdClear.setOnClickListener(this);

        etHotName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSaveEnabled();
            }
        });
        etHotPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSaveEnabled();
            }
        });
    }

    private void checkSaveEnabled() {
        String hotName = etHotName.getText().toString();
        String hotPwd = etHotPwd.getText().toString();
        if (TextUtils.isEmpty(hotName)) {
            ivHotNameClear.setVisibility(View.INVISIBLE);
        } else {
            ivHotNameClear.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(hotPwd)) {
            ivHotPwdClear.setVisibility(View.INVISIBLE);
        } else {
            ivHotPwdClear.setVisibility(View.VISIBLE);
        }
        mTvSkip.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(hotName) || TextUtils.isEmpty(hotPwd) || hotPwd.length() < 8) {
            mTvSkip.setTextColor(getResources().getColor(R.color
                    .ubt_skip_txt_unenable_color));
            mTvSkip.setEnabled(false);
        } else {
            mTvSkip.setTextColor(getResources().getColor(R.color
                    .ubt_tab_btn_txt_checked_color));
            mTvSkip.setEnabled(true);
        }
    }

    private void process() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                LoadingDialog.getInstance(SetHotSpotActivity.this).dismiss();
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("八戒未登录");
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定八戒");
                }
            }

            @Override
            public void onSuccess() {
            }
        });
        if (UBTPGApplication.isNetAvailable) {
            if (UBTPGApplication.isRobotOnline) {
                UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getHotSpot()));
            } else {
                UbtToastUtils.showCustomToast(this, "八戒处于离线状态\n获取热点信息失败");
            }
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.network_error));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_hot_name_clear:
                etHotName.getEditableText().clear();
                break;
            case R.id.iv_hot_pwd_clear:
                etHotPwd.getEditableText().clear();
                break;
            case R.id.ubt_tv_set_net_skip:
                sendUpdateHotSpot();
                break;
        }
    }

    private void sendUpdateHotSpot() {
        hotSpotName = etHotName.getText().toString();
        hotSpotPwd = etHotPwd.getText().toString();
        if (TextUtils.isEmpty(hotSpotName) || TextUtils.isEmpty(hotSpotPwd)) {
            ToastUtils.showShortToast("热点名称或密码不允许为空");
            return;
        }
        if (hotSpotName.length() > 10) {
            ToastUtils.showShortToast("热点名称长度不允许超过10个字符");
            return;
        }
        if (hotSpotPwd.length() < 8) {
            ToastUtils.showShortToast("热点密码长度至少需输入8个字符");
            return;
        }
        if (UBTPGApplication.isNetAvailable) {
            if (UBTPGApplication.isRobotOnline) {
                TIMMessage message = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.updateHotSpot(hotSpotName, hotSpotPwd));
                UbtTIMManager.getInstance().sendTIM(message);
            } else {
                UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline));
            }
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.network_error));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        TIMMessage msg = (TIMMessage) arg;
        for (int i = 0; i < msg.getElementCount(); ++i) {
            TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
            try {
                dealMsg(elem.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                ToastUtils.showShortToast(getString(R.string.msg_error_toast));

            }
        }
    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        switch (action) {
            case ContactsProtoBuilder.GET_HOTSPOT:
                unPackHotSpot(msg);
                break;
            case ContactsProtoBuilder.UPAT_HOTSPOT:
                unPackRepsponse(msg);
                break;
        }
    }

    private void unPackHotSpot(ChannelMessageContainer.ChannelMessage msg) {
        UserContacts.AccountRequest request = null;
        try {
            request = msg.getPayload().unpack(UserContacts.AccountRequest.class);
            final String name = request.getSsid();
            final String pwd = request.getPassword();
            if (etHotName != null) {
                etHotName.setText(name);
                etHotName.setSelection(name.length());
            }
            hotSpotName = name;
            if (etHotPwd != null) {
                etHotPwd.setText(pwd);
                etHotPwd.setSelection(pwd.length());
            }
            hotSpotPwd = pwd;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            ToastUtils.showLongToast("获取热点信息失败");
        }
    }

    private void unPackRepsponse(ChannelMessageContainer.ChannelMessage msg) {
        try {
            final boolean result = msg.getPayload().unpack(GPResponse.Response.class).getResult();
            if (result) {
                ToastUtils.showLongToast("修改成功");
                finish();
//                UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getHotSpot()));
            } else {
                ToastUtils.showLongToast("修改失败");
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            ToastUtils.showLongToast("修改失败");
        }
    }

}

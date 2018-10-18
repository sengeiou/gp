package com.ubtechinc.goldenpig.pigmanager.hotspot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UbtEditDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
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
    //    @BindView(R.id.ubt_btn_hotspot_name)
    UbtSubTxtButton mHotspotNameBtn;
    //    @BindView(R.id.ubt_btn_hotspot_pwd)
    UbtSubTxtButton mHotspotPwdBtn;

    private UbtEditDialog dialog;

    private String hotSpotName, hotSpotPwd;

    @Override
    protected int getConentView() {
        return R.layout.activity_set_wifi_hotspot;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_person_hotspot);
//        ButterKnife.bind(this);
        mHotspotNameBtn = findViewById(R.id.ubt_btn_hotspot_name);
        mHotspotNameBtn.setOnClickListener(this);
        mHotspotPwdBtn = findViewById(R.id.ubt_btn_hotspot_pwd);
        mHotspotPwdBtn.setOnClickListener(this);
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
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("小猪未登录");
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定小猪");
                }
            }

            @Override
            public void onSuccess() {
            }
        });
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getHotSpot()));
    }

    @Override
//    @OnClick({R.id.ubt_btn_hotspot_pwd,R.id.ubt_btn_hotspot_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ubt_btn_hotspot_name:
                showUpdatNameDialog();
                break;
            case R.id.ubt_btn_hotspot_pwd:
                showUpdatePwdDialog();
                break;
        }
    }

    /*显示修改热点名称的对话框*/
    private void showUpdatNameDialog() {
        initDialog();
        dialog.setTipsTxt(mHotspotNameBtn.getText().toString());
        dialog.setRawTxt(mHotspotNameBtn.getRightText());
        dialog.setOnEnterClickListener(new UbtEditDialog.OnEnterClickListener() {
            @Override
            public void onEnterClick(View view, String newStr) {
                hotSpotName = dialog.getNewEdtTxt();
                sendUpdateHotSpot();
            }
        });
        dialog.show();
    }

    /**
    * 显示修改热点密码对话框
    * */
    private void showUpdatePwdDialog() {
        initDialog();
        dialog.setTipsTxt(mHotspotPwdBtn.getText().toString());
        dialog.setRawTxt(mHotspotPwdBtn.getRightText());
        dialog.setOnEnterClickListener(new UbtEditDialog.OnEnterClickListener() {
            @Override
            public void onEnterClick(View view, String newStr) {
                hotSpotPwd = dialog.getNewEdtTxt();
                sendUpdateHotSpot();
            }
        });
        dialog.show();
    }

    private void initDialog() {
        dialog = new UbtEditDialog(this);

    }

    private void sendUpdateHotSpot() {
        TIMMessage message = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.updateHotSpot(hotSpotName, hotSpotPwd));
        UbtTIMManager.getInstance().sendTIM(message);
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
                ToastUtils.showShortToast("数据异常，请重试");

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
//            if (request.getRequest()){
            final String name = request.getSsid();
            final String pwd = request.getPassword();
            if (mHotspotNameBtn != null) {
                mHotspotNameBtn.setRightText(name);
            }
            hotSpotName = name;
            if (mHotspotPwdBtn != null) {
                mHotspotPwdBtn.setRightText(pwd);
            }
            hotSpotPwd = pwd;
//            }else {
//                ToastUtils.showLongToast("获取热点信息失败");
//            }
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
            } else {
                ToastUtils.showLongToast("修改失败");
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            ToastUtils.showLongToast("修改失败");
        }
    }

}

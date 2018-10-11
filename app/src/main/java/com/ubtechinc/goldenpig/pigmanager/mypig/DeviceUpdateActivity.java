package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.upgrade.VersionInformation;

import java.util.Observable;
import java.util.Observer;

import butterknife.ButterKnife;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :设备升级界面
 * @time :2018/9/15 16:48
 * @change :
 * @changetime :2018/9/15 16:48
 */
public class DeviceUpdateActivity extends BaseToolBarActivity implements Observer, View.OnClickListener {

    TextView mVersionTv;

    TextView mMsgTv;

    Button mUpdateBtn;

    @Override
    protected int getConentView() {
        return R.layout.activity_dev_update;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_device_version_update);

        mVersionTv = findViewById(R.id.ubt_tv_version_name);
        mMsgTv = findViewById(R.id.ubt_tv_version_msg);
        mUpdateBtn = findViewById(R.id.ubt_btn_dev_update);
        mUpdateBtn.setOnClickListener(this);

        initIM();

    }

    private void initIM() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
//        if (BuildConfig.DEBUG) {
//            UbtTIMManager.getInstance().setPigAccount("84ca93ea6c3fc295"); //
//        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                LoadingDialog.getInstance(DeviceUpdateActivity.this).dismiss();
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("小猪未登录");
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定小猪");
                }
                dismissLoadDialog();
            }

            @Override
            public void onSuccess() {
            }
        });
        getPigVersionInfo();
    }

    private void getPigVersionInfo() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersionState()));
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
                com.ubtech.utilcode.utils.ToastUtils.showShortToast("数据异常，请重试");

            }
        }

    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        if (action.equals(ContactsProtoBuilder.GET_VERSION_ACTION)) {
            VersionInformation.UpgradeInfo info = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class);
            if (info != null) {
                final int status = info.getStatus();
                if (status == 3) {
                    UbtToastUtils.showCustomToast(this, getString(R.string.ubt_pig_updateing));
                }
            }
        } else if (action.equals(ContactsProtoBuilder.UPATE_VERSION_ACTION)) {
            final int result = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class).getStatus();
            switch (result) {
                case 4:
                    UbtToastUtils.showCustomToast(this, getString(R.string.ubt_pig_updateing));
                    mUpdateBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                    break;
                case 5:
                    UbtToastUtils.showCustomToast(this, getString(R.string.ubt_pig_update_failure));
                    break;
                    default:
            }
        }
        dismissLoadDialog();
    }

    private void sendUpdate() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.updatePigVersion()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ubt_btn_dev_update:
                sendUpdate();
                showLoadingDialog();
                break;
                default:
        }
    }
}

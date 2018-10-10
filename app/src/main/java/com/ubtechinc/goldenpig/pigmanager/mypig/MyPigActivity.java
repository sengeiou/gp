package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
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
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.upgrade.VersionInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :我的小猪页面
 * @time :2018/9/15 12:48
 * @change :
 * @changetime :2018/9/15 12:48
 */
public class MyPigActivity extends BaseToolBarActivity implements Observer, View.OnClickListener {
    Button mDevUpateBtn;        //升级按钮
    Button mUnBindBtn;          //解除绑定按钮

    TextView mPigVersionTv;
    TextView mSearialNoTv;
    private PigInfo mPig;

    private boolean isNeedUpdate;
    private UnbindPigProxy.UnBindPigCallback unBindPigCallback;

    @Override
    protected int getConentView() {
        return R.layout.activity_my_pig;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.my_pig);
        unBindPigCallback = new UnbindPigProxy.UnBindPigCallback() {
            @Override
            public void onError(IOException e) {

            }

            @Override
            public void onSuccess(String reponse) {
                if (!TextUtils.isEmpty(reponse)) {
                    try {
                        JSONObject jsonObject = new JSONObject(reponse);
                        int code = jsonObject.has("code") ? jsonObject.getInt("code") : -1;

                        if (code == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShortToast(MyPigActivity.this, R.string.ubt_ubbind_success);
                                    try {
                                        ArrayList<PigInfo> pigInfos = AuthLive.getInstance().getCurrentPigList();
                                        int currentIndex = -1;
                                        for (int index = 0; index < pigInfos.size(); index++) {
                                            PigInfo pigInfo = pigInfos.get(index);
                                            if (pigInfo.getRobotName().equals(AuthLive.getInstance().getCurrentPig().getRobotName())) {
                                                currentIndex = index;
                                                break;
                                            }
                                        }
                                        AuthLive.getInstance().getCurrentPigList().remove(currentIndex);
                                    } catch (RuntimeException e) {
                                        e.printStackTrace();
                                    }
                                    new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", null);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            });
                        } else {
                            final String msg = jsonObject.has("message") ? jsonObject.getString("message") : "返回的结果格式错误";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShortToast(MyPigActivity.this, msg);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        };

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                LoadingDialog.getInstance(MyPigActivity.this).dismiss();
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

        initViews();

        final boolean isMaster = isSingalOrMaster();
        if (isMaster) {
            getPigVersion();
        }
    }

    private void initViews() {
        mDevUpateBtn = findViewById(R.id.ubt_btn_dev_update);
        mDevUpateBtn.setOnClickListener(this);
        mUnBindBtn = findViewById(R.id.ubt_btn_unbind);
        mUnBindBtn.setOnClickListener(this);
        mPigVersionTv = findViewById(R.id.ubt_tv_version);
        mSearialNoTv = findViewById(R.id.ubt_tv_searialno);
    }

    private void getPigVersion() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersion()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPig = AuthLive.getInstance().getCurrentPig();
        showPigNo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ubt_btn_dev_update:
                toDeviceUpdate();
                break;
            case R.id.ubt_btn_unbind:
                showConfirmDialog();
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    /**
     * 显示确认对转权限话框
     */
    private void showConfirmDialog() {
        final boolean isMaster = isSingalOrMaster();
        UBTBaseDialog dialog = new UBTBaseDialog(this);
        if (isMaster) {
            dialog.setTips(getString(R.string.ubt_transfer_admin_tips));
        } else {
            dialog.setTips(getString(R.string.ubt_unbing_tips));
        }
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                if (isMaster) {
                    ActivityRoute.toAnotherActivity(MyPigActivity.this, TransferAdminActivity.class, false);
                } else {
                    UnbindPigProxy pigProxy = new UnbindPigProxy();
                    final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();
                    final String userId = AuthLive.getInstance().getUserId();
                    final String token = CookieInterceptor.get().getToken();
                    pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
                }
            }
        });
        dialog.show();
    }

    private boolean isSingalOrMaster() {
        mPig = AuthLive.getInstance().getCurrentPig();
        boolean result = false;
        if (mPig != null && (mPig.isMaster() || mPig.isAdmin)) {
            result = true;
        }
        return result;
    }

    private void showPigNo() {
        if (mPig != null) {
            mSearialNoTv.setText(String.format(getString(R.string.ubt_pig_serialno), mPig.getRobotName()));
            if (mPig.isAdmin /*&& mPig.isMaster()*/) {
                mPigVersionTv.setVisibility(View.VISIBLE);
                mDevUpateBtn.setVisibility(View.VISIBLE);
            } else {
                mPigVersionTv.setVisibility(View.GONE);
                mDevUpateBtn.setVisibility(View.GONE);
            }
        }

    }

    private void toDeviceUpdate() {
        // if (mPig!=null &&mPig.isAdmin&&mPig.isMaster()){
        if (isNeedUpdate) {
            ActivityRoute.toAnotherActivity(this, DeviceUpdateActivity.class, false);
        } else {
            ActivityRoute.toAnotherActivity(this, PigLastVersionActivity.class, false);
        }
        //}
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
                if (mPigVersionTv != null) {
                    mPigVersionTv.setText(String.format(getString(R.string.ubt_pig_version_format), info.getCurrentVersion()));
                }
                isNeedUpdate = info.getStatus() == 1;
            }
        }
    }
}

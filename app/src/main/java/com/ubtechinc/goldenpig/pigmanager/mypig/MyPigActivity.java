package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.upgrade.VersionInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.app.Constant.SP_LAST_RECORD;

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
            public void onError(String msg) {
                LogUtils.d("onError");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(MyPigActivity.this, msg);
                    }
                });
            }

            @Override
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(MyPigActivity.this, R.string.ubt_ubbind_success);
                        imSyncRelationShip();
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
                            EventBusUtil.sendEvent(new Event<>(EventBusUtil.USER_PIG_UPDATE));
                            SPUtils.get().put(SP_LAST_RECORD, "");
                            SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, 0);
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

    private void imSyncRelationShip() {
        //TODO 给自己的猪发
        if (AuthLive.getInstance().getCurrentPig().isAdmin) {
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(3));
            UbtTIMManager.getInstance().sendTIM(selfMessage);
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

    private void getPigVersionState() {
        showLoadingDialog();
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersionState()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null) {
            mSearialNoTv.setText(String.format(getString(R.string.ubt_pig_serialno), mPig.getRobotName()));
        }
        showPigNo();
        updatePigList();
    }

    private void getMember() {
        new CheckUserRepository().getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new CheckUserRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
//                ToastUtils.showShortToast(MyPigActivity.this, "获取成员列表失败");
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
//                ToastUtils.showShortToast(MyPigActivity.this, "获取成员列表成功");
            }

            @Override
            public void onSuccessWithJson(String jsonStr) {
                List<CheckBindRobotModule.User> bindUsers = jsonToUserList(jsonStr);
                if (bindUsers != null && bindUsers.size() > 1) {
                    showConfirmDialog(true);
                } else {
                    showConfirmDialog(false);
                }
            }
        });
    }

    private void updatePigList() {
        if (AuthLive.getInstance().getCurrentPigList() != null) {
            AuthLive.getInstance().getCurrentPigList().clear();
        }
        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new GetPigListHttpProxy.OnGetPigListLitener() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onException(Exception e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onSuccess(String response) {
                Log.e("getPigList", response);
                PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance().getCurrentPigList());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPig = AuthLive.getInstance().getCurrentPig();
                        showPigNo();
                    }
                });
            }
        });
    }

    private List<CheckBindRobotModule.User> jsonToUserList(String jsonStr) {
        List<CheckBindRobotModule.User> result = null;
        Gson gson = new Gson();
        try {
            result = gson.fromJson(jsonStr, new TypeToken<List<CheckBindRobotModule.User>>() {
            }.getType());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ubt_btn_dev_update:
                getPigVersionState();
                break;
            case R.id.ubt_btn_unbind:
                doCheck();
                break;
            default:
        }
    }

    private void doCheck() {
        final boolean isMaster = isSingalOrMaster();
        if (isMaster) {
            getMember();
        } else {
            showConfirmDialog(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    /**
     * 显示确认转权限对话框
     */
    private void showConfirmDialog(boolean needTransfer) {
        runOnUiThread(() -> {
            UBTSubTitleDialog dialog = new UBTSubTitleDialog(MyPigActivity.this);
            if (needTransfer) {
                dialog.setTips(getString(R.string.ubt_transfer_admin_tips));
            } else {
                dialog.setTips(getString(R.string.ubt_unbing_tips));
                dialog.setSubTips(getString(R.string.ubt_transfer_tips));
            }
            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
            dialog.setRightButtonTxt(getString(R.string.ubt_enter));
            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
            dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    if (needTransfer) {
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
        });
    }

    private boolean isSingalOrMaster() {
        mPig = AuthLive.getInstance().getCurrentPig();
        boolean result = false;
        if (mPig != null && (mPig.isAdmin)) {
            result = true;
        }
        return result;
    }

    private void showPigNo() {
        if (isSingalOrMaster()) {
            mPigVersionTv.setVisibility(View.VISIBLE);
            mDevUpateBtn.setVisibility(View.VISIBLE);
        } else {
            mPigVersionTv.setVisibility(View.GONE);
            mDevUpateBtn.setVisibility(View.GONE);
        }
    }

    private void toDeviceUpdate() {
        dismissLoadDialog();
        if (isNeedUpdate) {
            ActivityRoute.toAnotherActivity(this, DeviceUpdateActivity.class, false);
        } else {
            ActivityRoute.toAnotherActivity(this, PigLastVersionActivity.class, false);
        }
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
                com.ubtech.utilcode.utils.ToastUtils.showShortToast(getString(R.string.msg_error_toast));

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
            }
        } else if (action.equals(ContactsProtoBuilder.GET_VERSION_STATE_ACTION)) {
            VersionInformation.UpgradeInfo info = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class);
            if (info != null) {
                isNeedUpdate = info.getStatus() == 1;
                toDeviceUpdate();
            }
        }
    }
}

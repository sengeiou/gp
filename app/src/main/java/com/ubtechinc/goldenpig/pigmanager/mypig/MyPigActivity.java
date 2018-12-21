package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMManager;
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
import com.ubtechinc.goldenpig.comm.entity.PairPig;
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
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.upgrade.VersionInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.app.Constant.SP_LAST_RECORD;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :我的八戒页面
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
                dismissLoadDialog();
//                if (AuthLive.getInstance().getCurrentPig() != null) {
//                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("八戒未登录");
//                } else {
//                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定八戒");
//                }
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
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            //TODO 给自己的猪发
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(3));
            UbtTIMManager.getInstance().sendTIM(selfMessage);

            //TODO 如果有配对关系
            PairPig pairPig = AuthLive.getInstance().getPairPig();
            if (pairPig != null) {
                //TODO 给配对的用户发
                TIMConversation pairUserConversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C, String.valueOf(pairPig.getPairUserId()));
                TIMMessage pairUserMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
                UbtTIMManager.getInstance().sendTIM(pairUserMessage, pairUserConversation);

                //TODO 给配对的猪发
                TIMConversation pairPigConversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C, pairPig.getPairSerialNumber());
                TIMMessage pairPigMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
                UbtTIMManager.getInstance().sendTIM(pairPigMessage, pairPigConversation);

            }
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
        showLoadingDialog();
        new CheckUserRepository().getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new CheckUserRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                runOnUiThread(() -> {
                    LoadingDialog.dissMiss();
                    if (e != null) {
                        String errorMsg = e.getMessage();
                        if (!TextUtils.isEmpty(errorMsg)) {
                            ToastUtils.showShortToast(MyPigActivity.this, errorMsg);
                        }
                    }

                });
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
            LoadingDialog.dissMiss();
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
                        //TODO 先转让管理员再退出群组
                        ActivityRoute.toAnotherActivity(MyPigActivity.this, TransferAdminActivity.class,
                                0x01, false);
                    } else {
                        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                        if (pigInfo != null) {
                            UnbindPigProxy pigProxy = new UnbindPigProxy();
                            final String serialNo = pigInfo.getRobotName();
                            final String userId = AuthLive.getInstance().getUserId();
                            final String token = CookieInterceptor.get().getToken();
                            pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
                        }
                    }
                }
            });
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0x01:
                if (resultCode == RESULT_OK) {
                    doExitGroup();
                }
                break;
        }
    }

    private void doExitGroup() {
        UnbindPigProxy pigProxy = new UnbindPigProxy();
        final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();

        final String token = CookieInterceptor.get().getToken();
        pigProxy.unbindPig(serialNo, AuthLive.getInstance().getUserId(), token, BuildConfig.APP_ID, unBindPigCallback);
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

    @Override
    public void update(Observable o, Object arg) {
        TIMMessage msg = (TIMMessage) arg;
        try {
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMElem tIMElem = msg.getElement(i);
                if (tIMElem != null && tIMElem instanceof TIMCustomElem) {
                    TIMCustomElem elem = (TIMCustomElem) tIMElem;
                    dealMsg(elem.getData());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.ubtech.utilcode.utils.ToastUtils.showShortToast(getString(R.string.msg_error_toast));
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
                dismissLoadDialog();
                int status = info.getStatus();
                String updateMessage = info.getUpdateMessage();
                String latestVersion = info.getLatestVersion();
                switch (status) {
                    case 1:
                        HashMap<String, String> map = new HashMap<>();
                        map.put("latestVersion", latestVersion);
                        map.put("updateMessage", updateMessage);
                        ActivityRoute.toAnotherActivity(this, DeviceUpdateActivity.class, map, false);
                        break;
                    case 2:
                        ActivityRoute.toAnotherActivity(this, PigLastVersionActivity.class, false);
                        break;
                    case 3:
                        UbtToastUtils.showCustomToast(this, getString(R.string.ubt_ota_status_3));
                        break;
                    default:
                        UbtToastUtils.showCustomToast(this, "异常错误，请重试");
                        break;
                }
            }
        }
    }
}

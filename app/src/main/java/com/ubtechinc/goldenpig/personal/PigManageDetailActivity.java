package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.hotspot.SetHotSpotActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.DeviceUpdateActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PigLastVersionActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PigMemberActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.TransferAdminActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.UnbindPigProxy;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.info.NativeInfoContainer;
import com.ubtrobot.upgrade.VersionInformation;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.app.Constant.SP_LAST_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

public class PigManageDetailActivity extends BaseNewActivity implements Observer, View.OnClickListener {

    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindViews({R.id.tv_4g, R.id.tv_hot_point, R.id.tv_continuity_voice, R.id.tv_about, R.id.tv_update})
    List<TextView> listTv;
    @BindViews({R.id.rl_4g, R.id.rl_hotpoint, R.id.rl_continuity_voice, R.id.rl_about, R.id.rl_update})
    List<RelativeLayout> listRl;
    @BindView(R.id.tv_manager)
    TextView tv_manager;
    @BindView(R.id.tv_manager_state)
    TextView tv_manager_state;
    @BindView(R.id.tv_dsn)
    TextView tv_dsn;

    @BindView(R.id.tv_beehive_close)
    TextView tvBeehiveClose;

    @BindView(R.id.tv_no_sim)
    TextView tvNoSim;

    private PigInfo mPig;
    private UnbindPigProxy.UnBindPigCallback unBindPigCallback;
    CheckBindRobotModule.User ManagerUser;

    private boolean isNoSim;
    private boolean isBeeHiveOpen;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_device_manage_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        rl_titlebar.setTitleText("八戒详情");
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EventBusUtil.register(this);
        tv_manager.setText(AuthLive.getInstance().getCurrentUser().getNickName());
        unBindPigCallback = new UnbindPigProxy.UnBindPigCallback() {

            @Override
            public void onError(String msg) {
                LogUtils.d("onError");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(PigManageDetailActivity.this, msg);
                    }
                });
            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(PigManageDetailActivity.this, R.string.ubt_ubbind_success);
                        imSyncRelationShip();
                        try {
                            ArrayList<PigInfo> pigInfos = AuthLive.getInstance().getCurrentPigList();
                            int currentIndex = -1;
                            for (int index = 0; index < pigInfos.size(); index++) {
                                PigInfo pigInfo = pigInfos.get(index);
                                if (pigInfo.getRobotName().equals(AuthLive.getInstance().getCurrentPig().getRobotName
                                        ())) {
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
                        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID,
                                "", null);
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
                LoadingDialog.getInstance(PigManageDetailActivity.this).dismiss();
                if (AuthLive.getInstance().getCurrentPig() != null) {
//                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("八戒未登录");
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定八戒");
                }
            }

            @Override
            public void onSuccess() {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null) {
            if (mPig.isAdmin) {
                UbtTIMManager.getInstance().queryNativeInfo();
                tv_manager_state.setText("管理员：");
                for (RelativeLayout rl : listRl) {
                    rl.setEnabled(true);
                }
                for (TextView tv : listTv) {
                    tv.setTextColor(getResources().getColor(R.color.ubt_tips_txt_color));
                }
            } else {
                tv_manager_state.setText("普通成员：");
                for (RelativeLayout rl : listRl) {
                    rl.setEnabled(false);
                }
                for (TextView tv : listTv) {
                    tv.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                }
            }
            tv_dsn.setText(mPig.getRobotName());
        } else {
            finish();
        }
        //getmanager();
    }

    @OnClick({R.id.rl_wifi, R.id.rl_4g, R.id.rl_hotpoint, R.id.rl_continuity_voice, R.id.rl_member_group, R.id
            .rl_about, R.id.rl_update, R.id.tv_unbind})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_wifi:
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(this, SwitchWifiActivity.class, false);
                } else {
                    ActivityRoute.toAnotherActivity(this, BleConfigReadyActivity.class, false);
                }
                break;
            case R.id.rl_4g:
                if (isNoSim) {
                    ActivityRoute.toAnotherActivity(this, NoSimActivity.class, false);
                } else {
                    ActivityRoute.toAnotherActivity(this, BeeHiveMobileActivity.class, false);
                }
                break;
            case R.id.rl_hotpoint:
                if (isNoSim) {
                    ActivityRoute.toAnotherActivity(this, NoSimActivity.class, false);
                } else {
                    ActivityRoute.toAnotherActivity(this, SetHotSpotActivity.class, false);
                }
                break;
            case R.id.rl_continuity_voice:
                ActivityRoute.toAnotherActivity(this, ContinuousVoiceActivity.class, false);
                break;
            case R.id.rl_member_group:
                ActivityRoute.toAnotherActivity(this, PigMemberActivity.class, false);
                break;
            case R.id.rl_about:
                ActivityRoute.toAnotherActivity(this, AboutBleBJActivity.class, false);
                break;
            case R.id.rl_update:
                //TODO 系统升级
                getPigVersionState();
                break;
            case R.id.tv_unbind:
                doCheck();
                break;
        }
    }

    private void getPigVersionState() {
        showLoadingDialog();
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersionState()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case USER_PIG_UPDATE:
                break;
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

    private boolean isSingalOrMaster() {
        mPig = AuthLive.getInstance().getCurrentPig();
        boolean result = false;
        if (mPig != null && (mPig.isAdmin)) {
            result = true;
        }
        return result;
    }

    private void getMember() {
        new CheckUserRepository().getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(),
                BuildConfig.APP_ID, "", new CheckUserRepository.ICheckBindStateCallBack() {
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

    /**
     * 显示确认转权限对话框
     */
    private void showConfirmDialog(boolean needTransfer) {
        runOnUiThread(() -> {
            UBTSubTitleDialog dialog = new UBTSubTitleDialog(PigManageDetailActivity.this);
            if (needTransfer) {
                dialog.setTips(getString(R.string.ubt_transfer_admin_tips));
            } else {
                dialog.setTips(getString(R.string.ubt_unbing_tips));
                dialog.setSubTips(getString(R.string.ubt_transfer_tips));
            }
            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
            dialog.setRightButtonTxt(getString(R.string.ubt_enter));
            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color,
                    null));
            dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    if (needTransfer) {
                        //TODO 先转让管理员再退出群组
                        ActivityRoute.toAnotherActivity(PigManageDetailActivity.this, TransferAdminActivity.class,
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        if (action.equals(ContactsProtoBuilder.GET_VERSION_ACTION)) {
            VersionInformation.UpgradeInfo info = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class);
//            if (info != null) {
//                if (mPigVersionTv != null) {
//                    mPigVersionTv.setText(String.format(getString(R.string.ubt_pig_version_format), info
// .getCurrentVersion()));
//                }
//            }
        } else if (action.equals(ContactsProtoBuilder.GET_VERSION_STATE_ACTION)) {
            VersionInformation.UpgradeInfo info = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class);
            if (info != null) {
                dismissLoadDialog();
                int status = info.getStatus();
                String updateMessage = info.getUpdateMessage();
                String latestVersion = info.getLatestVersion();
                HashMap<String, String> map = new HashMap<>();
                map.put("latestVersion", latestVersion);
                map.put("updateMessage", updateMessage);
                switch (status) {
                    case 1:
                        ActivityRoute.toAnotherActivity(this, DeviceUpdateActivity.class, map, false);
                        break;
                    case 2:
                        ActivityRoute.toAnotherActivity(this, PigLastVersionActivity.class, map, false);
                        break;
                    case 3:
                        UbtToastUtils.showCustomToast(this, getString(R.string.ubt_ota_status_3));
                        break;
                    default:
                        UbtToastUtils.showCustomToast(this, "异常错误，请重试");
                        break;
                }
            }
        } else if (action.equals(ContactsProtoBuilder.GET_NATIVE_INFO)) {
            NativeInfoContainer.NativeInfo nativeInfo = msg.getPayload().unpack(NativeInfoContainer.NativeInfo.class);
            UpdateNativeInfo(nativeInfo);
        }
    }

    private void UpdateNativeInfo(NativeInfoContainer.NativeInfo data) {
        NativeInfoContainer.NativeInfo nativeInfo = data;
        try {
            NativeInfoContainer.SimStatus simStatus = nativeInfo.getSimStatus().unpack(NativeInfoContainer.SimStatus.class);
            NativeInfoContainer.NetworkStatus networkStatus = nativeInfo.getNetworkStatus().unpack(NativeInfoContainer.NetworkStatus.class);
            if (networkStatus.getMobileState() == 0) {
                isBeeHiveOpen = false;
            } else {
                isBeeHiveOpen = true;
            }
            if (simStatus.getInserted()) {
                isNoSim = false;
            } else {
                isNoSim = true;
            }

            if (isNoSim) {
                tvBeehiveClose.setVisibility(View.VISIBLE);
                tvNoSim.setVisibility(View.VISIBLE);
            } else {
                tvNoSim.setVisibility(View.GONE);
                if (isBeeHiveOpen) {
                    tvBeehiveClose.setVisibility(View.GONE);
                } else {
                    tvBeehiveClose.setText("已关闭");
                    tvBeehiveClose.setVisibility(View.VISIBLE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
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

    public void getmanager() {
        CheckUserRepository repository = new CheckUserRepository();
        repository.getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID,
                "1", new CheckUserRepository.ICheckBindStateCallBack() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                        ToastUtils.showShortToast(PigManageDetailActivity.this, "获取管理员失败");
                    }

                    @Override
                    public void onSuccess(CheckBindRobotModule.Response response) {
                        ToastUtils.showShortToast(PigManageDetailActivity.this, "获取管理员成功");
                    }

                    @Override
                    public void onSuccessWithJson(String jsonStr) {
                        LoadingDialog.getInstance(PigManageDetailActivity.this).dismiss();
                        final List<CheckBindRobotModule.User> bindUsers = jsonToUserList(jsonStr);
                        if (bindUsers != null && bindUsers.size() > 0) {
                            ManagerUser = bindUsers.get(0);
                            tv_manager.setText(ManagerUser.getNickName());
                        }
                    }
                });
    }

}

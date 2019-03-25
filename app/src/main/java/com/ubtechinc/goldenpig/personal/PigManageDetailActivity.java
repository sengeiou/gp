package com.ubtechinc.goldenpig.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.Constant;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTFunctionDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.hotspot.SetHotSpotActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.DeviceUpdateActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PigLastVersionActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PigMemberActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.TransferAdminActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.UnbindAllMemberProxy;
import com.ubtechinc.goldenpig.pigmanager.mypig.UnbindPigProxy;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.CheckUtil;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtrobot.clear.ClearContainer;
import com.ubtrobot.info.NativeInfoContainer;
import com.ubtrobot.upgrade.VersionInformation;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.ubtechinc.goldenpig.personal.BeeHiveMobileActivity.KEY_BEE_HIVE_OPEN;
import static com.ubtechinc.goldenpig.personal.NoSimActivity.KEY_TOOL_BAR_TITLE;

public class PigManageDetailActivity extends BaseToolBarActivity implements View.OnClickListener {

    private View rl_wifi, rl_member_group;

    private View rl_4g, rl_hotpoint, rl_continuity_voice, rl_about, rl_update, rl_shutdown_alarm;

    private View ivRedPoint;

    private TextView tv_4g, tv_hot_point, tv_continuity_voice, tv_about, tv_update;

    private TextView tv_manager;

    private TextView tv_manager_state;

    private TextView tv_dsn;

    private TextView tvBeehiveClose;

    private TextView tvNoSim;

    private TextView tvWifiName;

    private TextView tv_unbind;

    private PigInfo mPig;

    private UnbindPigProxy.UnBindPigCallback unBindPigCallback;

    CheckBindRobotModule.User ManagerUser;

    private boolean isNoSim;

    private boolean isBeeHiveOpen;

    private boolean needHandleUpdate;

    private boolean needUnBindByClear;

    private boolean isUnbindAll;

    private Disposable imOutDisposable;

    @Override
    protected int getConentView() {
        return R.layout.activity_device_manage_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setToolBarTitle("机器人详情");
        setTitleBack(true);
        initView();
    }

    private void initView() {
        rl_wifi = findViewById(R.id.rl_wifi);
        rl_member_group = findViewById(R.id.rl_member_group);
        rl_wifi.setOnClickListener(this);
        rl_member_group.setOnClickListener(this);

        rl_4g = findViewById(R.id.rl_4g);
        rl_hotpoint = findViewById(R.id.rl_hotpoint);
        rl_continuity_voice = findViewById(R.id.rl_continuity_voice);
        rl_shutdown_alarm = findViewById(R.id.rl_shutdown_alarm);
        rl_about = findViewById(R.id.rl_about);
        rl_update = findViewById(R.id.rl_update);
        rl_4g.setOnClickListener(this);
        rl_hotpoint.setOnClickListener(this);
        rl_continuity_voice.setOnClickListener(this);
        rl_shutdown_alarm.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_update.setOnClickListener(this);


        ivRedPoint = findViewById(R.id.iv_red_point);
        tv_4g = findViewById(R.id.tv_4g);
        tv_hot_point = findViewById(R.id.tv_hot_point);
        tv_continuity_voice = findViewById(R.id.tv_continuity_voice);
        tv_about = findViewById(R.id.tv_about);
        tv_update = findViewById(R.id.tv_update);

        tv_manager = findViewById(R.id.tv_manager);
        tv_manager_state = findViewById(R.id.tv_manager_state);
        tv_dsn = findViewById(R.id.tv_dsn);
        tvBeehiveClose = findViewById(R.id.tv_beehive_close);
        tvNoSim = findViewById(R.id.tv_no_sim);
        tvWifiName = findViewById(R.id.tv_wifi_name);
        tv_unbind = findViewById(R.id.tv_unbind);
        tv_unbind.setOnClickListener(this);

        UserInfo userInfo = AuthLive.getInstance().getCurrentUser();
        if (userInfo != null) {
            tv_manager.setText(userInfo.getNickName());
        }
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
                            SPUtils.get().put(Constant.SP_LAST_RECORD, "");
                            SPUtils.get().put(Constant.SP_HAS_LOOK_LAST_RECORD, 0);
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

        Intent intent = getIntent();
        if (intent != null) {
            boolean hasNewVersion = intent.getBooleanExtra("robotNewVersion", false);
            if (hasNewVersion) {
                ivRedPoint.setVisibility(View.VISIBLE);
            } else {
                ivRedPoint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRobotView();
    }

    private void updateRobotView() {
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null) {
            tv_dsn.setText(mPig.getRobotName());
            if (mPig.isAdmin) {
                UbtTIMManager.getInstance().queryNativeInfo();
                updateTopTip();
                tv_manager_state.setText("管理员：");
                refreshUI(true);
            } else {
                tv_manager_state.setText("普通成员：");
                refreshUI(false);
                ivRedPoint.setVisibility(View.GONE);
            }
        } else {
            finish();
        }
    }

    private void refreshUI(boolean isAdmin) {
        rl_4g.setEnabled(isAdmin);
        rl_hotpoint.setEnabled(isAdmin);
        rl_continuity_voice.setEnabled(isAdmin);
        rl_about.setEnabled(isAdmin);
        rl_update.setEnabled(isAdmin);

        tv_4g.setEnabled(isAdmin);
        tv_hot_point.setEnabled(isAdmin);
        tv_continuity_voice.setEnabled(isAdmin);
        tv_about.setEnabled(isAdmin);
        tv_update.setEnabled(isAdmin);
    }

    public void onClick(View v) {
        if (!CheckUtil.checkPhoneNetState(this)) {
            return;
        }
        switch (v.getId()) {
            case R.id.rl_wifi:
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig.isAdmin) {
                    if (UBTPGApplication.isRobotOnline) {
                        ActivityRoute.toAnotherActivity(this, SwitchWifiActivity.class, false);
                    } else {
                        ActivityRoute.toAnotherActivity(this, RobotOfflineActivity.class, false);
                    }
                } else {
                    ActivityRoute.toAnotherActivity(this, BleConfigReadyActivity.class, false);
                }
                break;
            case R.id.rl_4g:
                if (isNoSim) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(KEY_TOOL_BAR_TITLE, getResources().getString(R.string.ubt_mobile_bee_hive));
                    enterFunction(NoSimActivity.class, map);
                } else {
                    HashMap<String, Boolean> map = new HashMap<>();
                    map.put(KEY_BEE_HIVE_OPEN, isBeeHiveOpen);
                    enterFunction(BeeHiveMobileActivity.class, map);
                }
                break;
            case R.id.rl_hotpoint:
                if (checkOnlineState()) {
                    if (isNoSim) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(KEY_TOOL_BAR_TITLE, getResources().getString(R.string.ubt_person_hotspot));
                        enterFunction(NoSimActivity.class, map);
                    } else if (isBeeHiveOpen) {
                        enterFunction(SetHotSpotActivity.class, null);
                    } else {
                        UbtToastUtils.showCustomToast(this, getString(R.string.open_beehive_mobile));
                    }
                }
                break;
            case R.id.rl_continuity_voice:
                enterFunction(ContinuousVoiceActivity.class, null);
                break;
            case R.id.rl_shutdown_alarm:
                //enterFunction(ShutdownAlarmActivity.class, null);
                break;
            case R.id.rl_member_group:
                ActivityRoute.toAnotherActivity(this, PigMemberActivity.class, false);
                break;
            case R.id.rl_about:
                ActivityRoute.toAnotherActivity(this, AboutBleBJActivity.class, false);
                break;
            case R.id.rl_update:
                //TODO 系统升级
                if (checkOnlineState()) {
                    getPigVersionState();
                }
                break;
            case R.id.tv_unbind:
                unBindCheck();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0x01:
                if (resultCode == RESULT_OK) {
                    //TODO 先转让管理员再退出群组
                    doUnbind();
                }
                break;
        }
    }

    private void enterFunction(Class clazz, HashMap<String, ? extends Object> hashMap) {
        PigInfo myPig = AuthLive.getInstance().getCurrentPig();
        if (myPig == null) {
            //TODO
        } else if (myPig.isAdmin) {
            if (checkOnlineState()) {
                ActivityRoute.toAnotherActivity(this, clazz, hashMap, false);
            }
        } else {
            com.ubtech.utilcode.utils.ToastUtils.showShortToast(R.string.only_admin_operate);
        }
    }

    private boolean checkOnlineState() {
        if (UBTPGApplication.isRobotOnline) {
            return true;
        } else {
            UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline));
            return false;
        }
    }

    private void getPigVersionState() {
        showLoadingDialog();
        needHandleUpdate = true;
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersionState
                ()));
    }

    private void getRobotOTAResult() {
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder
                .getRobotUpdateResult()));
    }

    private void unBindCheck() {
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null && mPig.isAdmin) {
            //TODO 管理员
            getMember();
        } else {
            //TODO 普通成员
            showUnbindDialog();
        }
    }

    private void showUnbindDialog() {
        UBTBaseDialog dialog = new UBTBaseDialog(PigManageDetailActivity.this);
        dialog.setTips(getString(R.string.unbind_confirm));
        dialog.setLeftButtonTxt(getString(R.string.ubt_close));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color,
                null));
        dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                doUnbind();
            }
        });
        dialog.show();
    }

    private void doUnbind() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UnbindPigProxy pigProxy = new UnbindPigProxy();
            final String serialNo = pigInfo.getRobotName();
            final String userId = AuthLive.getInstance().getUserId();
            final String token = CookieInterceptor.get().getToken();
            pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
        }
    }

    private void getMember() {
        new CheckUserRepository().getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(),
                BuildConfig.APP_ID, "", new CheckUserRepository.ICheckBindStateCallBack() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                    }

                    @Override
                    public void onSuccess(CheckBindRobotModule.Response response) {
                    }

                    @Override
                    public void onSuccessWithJson(String jsonStr) {
                        List<CheckBindRobotModule.User> bindUsers = jsonToUserList(jsonStr);
                        if (bindUsers != null && bindUsers.size() > 1) {
                            showUnBindConfirmDialog(false);
                        } else {
                            showUnBindConfirmDialog(true);
                        }
                    }
                });
    }

    private void showUnBindConfirmDialog(boolean onlySelf) {
        if (onlySelf) {
            UBTSubTitleDialog unBindConfirmDialog = new UBTSubTitleDialog(this);
            unBindConfirmDialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color
                    .ubt_tab_btn_txt_checked_color, null));
            unBindConfirmDialog.setSubTipColor(ContextCompat.getColor(this, R.color.ubt_tips_txt_color));
            unBindConfirmDialog.setTips(getString(R.string.unbind_confirm));
            unBindConfirmDialog.setRadioText(getString(R.string.unbind_confirm_tip2));
            unBindConfirmDialog.setRadioSelected(true);
            unBindConfirmDialog.setRightButtonTxt(getString(R.string.ubt_enter));
            unBindConfirmDialog.setSubTips(getString(R.string.unbind_confirm_tip));
            unBindConfirmDialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    if (unBindConfirmDialog.isRadioSelected()) {
                        doClearInfoByIM();
                    } else {
                        if (isUnbindAll) {
                            doUnbindAllMember();
                        } else {
                            doUnbind();
                        }
                    }
                }
            });
            unBindConfirmDialog.show();
        } else {
            UBTFunctionDialog dialog = new UBTFunctionDialog(this);
            dialog.setFunc1Txt(getString(R.string.unbind_only_self));
            dialog.setFunc2Txt(getString(R.string.unbind_all));
            dialog.showCloseIcon(true);
            dialog.setTips(getString(R.string.ubt_unbind_group_tips));

            dialog.setOnUbtDialogClickLinsenter(new UBTFunctionDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onFunc1Click(View view) {
                    ActivityRoute.toAnotherActivity(PigManageDetailActivity.this, TransferAdminActivity.class, 0x01,
                            false);
                }

                @Override
                public void onFunc2Click(View view) {
                    isUnbindAll = true;
                    showUnBindConfirmDialog(true);
//                    doUnbindAllMember();
                }

                @Override
                public void onClose(View view) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    }

    /**
     * 全部成员解绑
     */
    private void doUnbindAllMember() {
        UnbindAllMemberProxy proxy = new UnbindAllMemberProxy();
        final String serialNo = AuthLive.getInstance().getRobotUserId();
        if (!TextUtils.isEmpty(serialNo)) {
            proxy.unbind(serialNo, new UnbindAllMemberProxy.UnBindPigCallback() {
                @Override
                public void onError(String msg) {
                    runOnUiThread(() -> ToastUtils.showShortToast(PigManageDetailActivity.this, msg));
                }

                @Override
                public void onSuccess() {
                    imSyncRelationShip();
                    runOnUiThread(() -> updatePigList());
                }
            });
        }
    }

    private void updatePigList() {
        if (AuthLive.getInstance().getCurrentPigList() != null) {
            AuthLive.getInstance().getCurrentPigList().clear();
        }
        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new
                GetPigListHttpProxy.OnGetPigListLitener() {
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
                        PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance()
                                .getCurrentPigList());
                        ArrayList<PigInfo> list = AuthLive.getInstance().getCurrentPigList();
                        if (list == null || list.isEmpty()) {
                            finish();
                        }
                    }
                });
    }

    private void doClearInfoByIM() {
        if (!UBTPGApplication.isRobotOnline) {
            UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline_clear_tip));
            return;
        }
        showLoadingDialog();
        imOutDisposable = Observable.timer(15, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //TODO IM receive timeout
                    dismissLoadDialog();
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除失败，请重试");
                });
        needUnBindByClear = true;
        List<ClearContainer.Categories.Builder> categorys = new ArrayList<>();
        ClearContainer.Categories.Builder categoryBuilder1 = ClearContainer.Categories.newBuilder();
        categoryBuilder1.setName("Contact.deleteContact");
        ClearContainer.Categories.Builder categoryBuilder2 = ClearContainer.Categories.newBuilder();
        categoryBuilder2.setName("Record.deleteData");
        categorys.add(categoryBuilder1);
        categorys.add(categoryBuilder2);
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.clearInfo
                (categorys)));
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
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        if (imOutDisposable != null) {
            imOutDisposable.dispose();
        }
    }

    private void updateNativeInfo(NativeInfoContainer.NativeInfo data) {
        NativeInfoContainer.NativeInfo nativeInfo = data;
        try {
            NativeInfoContainer.SimStatus simStatus = nativeInfo.getSimStatus().unpack(NativeInfoContainer.SimStatus
                    .class);
            NativeInfoContainer.NetworkStatus networkStatus = nativeInfo.getNetworkStatus().unpack
                    (NativeInfoContainer.NetworkStatus.class);

            if (simStatus.getInserted()) {
                isNoSim = false;
                MainActivity.isNoSim = false;
            } else {
                isNoSim = true;
                MainActivity.isNoSim = true;
            }

            if (!isNoSim && simStatus.getOpen()) {
                isBeeHiveOpen = true;
                MainActivity.isBeeHiveOpen = true;
            } else {
                isBeeHiveOpen = false;
                MainActivity.isBeeHiveOpen = false;
            }

            tvWifiName.setText(networkStatus.getSsid());

            if (isNoSim) {
                tvBeehiveClose.setText(R.string.ubt_no_sim);
                tvBeehiveClose.setVisibility(View.VISIBLE);
                tvNoSim.setVisibility(View.VISIBLE);
            } else {
                tvNoSim.setVisibility(View.GONE);
                if (isBeeHiveOpen) {
                    tvBeehiveClose.setVisibility(View.GONE);
                } else {
                    tvBeehiveClose.setText(R.string.ubt_is_close);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case EventBusUtil.USER_PIG_UPDATE:
                updateRobotView();
                break;
            case EventBusUtil.RECEIVE_NATIVE_INFO:
                updateNativeInfo((NativeInfoContainer.NativeInfo) event.getData());
                break;
            case EventBusUtil.RECEIVE_ROBOT_VERSION_STATE:
                if (!needHandleUpdate) return;
                needHandleUpdate = false;
                VersionInformation.UpgradeInfo info = (VersionInformation.UpgradeInfo) event.getData();
                if (info != null) {
                    dismissLoadDialog();
                    int status = info.getStatus();
                    String currentVersion = info.getCurrentVersion();
                    String updateMessage = info.getUpdateMessage();
                    String latestVersion = info.getLatestVersion();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("currentVersion", currentVersion);
                    map.put("latestVersion", latestVersion);
                    map.put("updateMessage", updateMessage);
                    map.put("status", String.valueOf(status));
                    switch (status) {
                        case 1:
                            ActivityRoute.toAnotherActivity(this, DeviceUpdateActivity.class, map, false);
                            break;
                        case 2:
                            ActivityRoute.toAnotherActivity(this, PigLastVersionActivity.class, map, false);
                            break;
                        case 3:
                            ActivityRoute.toAnotherActivity(this, DeviceUpdateActivity.class, map, false);
//                        UbtToastUtils.showCustomToast(this, getString(R.string.ubt_ota_status_3));
                            break;
                        default:
                            showIKnowDialog("异常错误，请重试");
//                        UbtToastUtils.showCustomToast(this, "异常错误，请重试");
                            break;
                    }
                }
                break;
            case EventBusUtil.RECEIVE_CLEAR_PIG_INFO:
                if (imOutDisposable != null) {
                    imOutDisposable.dispose();
                }
                if (!needUnBindByClear) return;
                if ((boolean) event.getData()) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除成功");
                    if (isUnbindAll) {
                        doUnbindAllMember();
                    } else {
                        doUnbind();
                    }
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除失败，请重试");
                }
                break;
            case EventBusUtil.RECEIVE_ROBOT_ONLINE_STATE:
                updateTopTip();
                break;
            case EventBusUtil.NETWORK_STATE_CHANGED:
                updateTopTip();
                break;
        }
    }

    private void updateTopTip() {
        if (UBTPGApplication.isNetAvailable) {
            mPig = AuthLive.getInstance().getCurrentPig();
            if (mPig != null) {
                if (mPig.isAdmin) {
                    if (UBTPGApplication.isRobotOnline) {
                        hideNotify();
                    } else {
                        showNotify("机器人离线");
                    }
                } else {
                    hideNotify();
                }
            } else {
                finish();
            }
        } else {
            showNotify("手机无网络连接");
        }
    }

}

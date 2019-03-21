package com.ubtechinc.goldenpig.main.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.widget.CustomPopupWindow;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTUpdateDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.CheckUpdateHttpProxy;
import com.ubtechinc.goldenpig.main.CommonWebActivity;
import com.ubtechinc.goldenpig.main.FunctionModel;
import com.ubtechinc.goldenpig.main.HomeDataHttpProxy;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.main.UbtWebHelper;
import com.ubtechinc.goldenpig.main.UpdateInfoModel;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.AnimUtil;
import com.ubtechinc.goldenpig.utils.SCADAHelper;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;
import com.ubtrobot.info.NativeInfoContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.APP_UPDATE_CHECK;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DO_GET_NATIVE_INFO;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.INVISE_RECORD_POINT;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NETWORK_STATE_CHANGED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEW_CALL_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEW_MESSAGE_NOTIFICATION;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_NATIVE_INFO;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_ROBOT_ONLINE_STATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.UPDATE_HOME_FUNCTION_CARD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :八戒分页Fragment
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class PigNewFragment extends BaseFragment {

    private static final String TAG = PigNewFragment.class.getSimpleName();

    private static final int GET_NATIVE_INFO = 1;

    @BindView(R.id.tv_pig_sn)
    TextView tvPigSn;

    @BindView(R.id.btn_bt_binding)
    TextView btnBinding;

    @BindView(R.id.tv_net_tip)
    TextView tvNetTip;

    @BindView(R.id.recycler_skill)
    RecyclerView recyclerView;

    @BindView(R.id.rv_function_card)
    RecyclerView rvFunctionCard;

    @BindView(R.id.iv_ble)
    ImageView ivBle;
    @BindView(R.id.iv_signal)
    ImageView ivSignal;
    @BindView(R.id.iv_battery)
    ImageView ivBattery;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.iv_up_down)
    ImageView ivUpDown;
    @BindView(R.id.rl_wifi)
    RelativeLayout rlWifi;
    @BindView(R.id.rl_native_info)
    RelativeLayout rlNativeInfo;
    @BindView(R.id.iv_sim_net)
    ImageView ivSimNet;
    @BindView(R.id.iv_wifi)
    ImageView ivWifi;
    Unbinder unbinder;

    @BindView(R.id.tv_statement_title)
    TextView tvStatementTitle;

    @BindView(R.id.tv_pig_tip)
    TextView tvPigTip;

    @BindView(R.id.btn_config_wifi)
    View btnConfigWifi;

    @BindView(R.id.tv_change)
    TextView tvChange;

    @BindView(R.id.iv_pull)
    ImageView ivPull;

    PigFragmentAdapter statementAdapter;

    private CustomPopupWindow mCustomPopupWindow = null;
    MainFunctionAdapter functionAdapter;

    private boolean hasRefreshFromServer;

    private UBTSubTitleDialog mMobileFlowDialog;

    private List<String> list = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_NATIVE_INFO:
                    PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                    Log.d(TAG, "queryNativeInfo pigInfo = " + pigInfo);
                    if (pigInfo != null && pigInfo.isAdmin) {
                        UbtTIMManager.getInstance().queryNativeInfo();
                        if (mHandler.hasMessages(GET_NATIVE_INFO)) {
                            mHandler.removeMessages(GET_NATIVE_INFO);
                        }
                        mHandler.sendEmptyMessageDelayed(GET_NATIVE_INFO, 60 * 1000);
                    } else {
                        hideNativeInfo();
                    }
                    break;
            }
        }
    };

    private FunctionModel mFunctionModel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pig_new, container, false);
        EventBusUtil.register(this);
        initSkillData();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initSkillData() {
        //TODO 后续该数据从后台获取
        list.add("“八戒八戒，放一首歌”");
        list.add("“八戒八戒，发送留言”");
        list.add("“八戒八戒，我好喜欢你”");
        list.add("“八戒八戒，打电话”");
        list.add("“八戒八戒，今天天气怎么样”");
        list.add("“八戒八戒，提醒我下午四点喝水”");
    }

    /**
     * 功能卡片更新
     */
    private void showFunctionRedPoint(MainFunctionAdapter.FunctionEnum functionEnum, boolean isShow) {
        if (functionAdapter != null) {
            functionEnum.hasRedPoint = isShow;
            functionAdapter.notifyItemChanged(functionEnum);
        }
    }

    List<MainFunctionAdapter.FunctionEnum> tempList;

    private void initFunctionCard() {
        tempList = new ArrayList<>(Arrays.asList(MainFunctionAdapter.FunctionEnum.values()));
        if (functionAdapter == null) {
            functionAdapter = new MainFunctionAdapter(getActivity(), new ArrayList<>(Arrays.asList(MainFunctionAdapter.FunctionEnum.values())));
        }
        rvFunctionCard.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rvFunctionCard.setAdapter(functionAdapter);

    }


    private boolean hiden = true;
    private int baseHeight = 100; //后续看兼容性通过计算itemView高度或者在布局设置控件高度

    private void changeFunctionCardListHeight() {
//        int count = tempList.size();
        int count = mFunctionModel.catetory.categorys.size();
        if (count <= 8) {
            return;  //增加异常判断，按照正常逻辑8个是不会显示下拉按钮
        }


        if (hiden) {
            hiden = false;
            UbtLogger.d(TAG, "hide:" + false);
            ivPull.setImageResource(R.drawable.ic_xiala);
            if (count <= 12) {
                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight * 3), DensityUtil.dp2px(baseHeight * 2));
            } else {
                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight * 4), DensityUtil.dp2px(baseHeight * 2));
            }

        } else {
            hiden = true;
            UbtLogger.d(TAG, "hide:" + hiden);
            ivPull.setImageResource(R.drawable.ic_shangshou);
            if (count <= 12) {
                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight * 2), DensityUtil.dp2px(baseHeight * 3));
            } else {
                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight * 2), DensityUtil.dp2px(baseHeight * 4));
            }

        }

        UbtLogger.d(TAG, "sp hide:" + hiden);
        SharedPreferencesUtils.putBoolean(getActivity(), "hiden", hiden);

    }


    private void restoreCard() {
        boolean state = SharedPreferencesUtils.getBoolean(getActivity(), "hiden", true);
        hiden = state;
//        int count = tempList.size();
        int count = mFunctionModel.catetory.categorys.size();
        if (state) {
            UbtLogger.d(TAG, "restoreCard 不做处理");
//            ivPull.setImageResource(R.drawable.ic_shangshou);
//            if(count<=12){
//                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight*2), DensityUtil.dp2px(baseHeight * 3));
//            }else{
//                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight*2), DensityUtil.dp2px(baseHeight * 4));
//            }
        } else {
            UbtLogger.d(TAG, "restoreCard 收起");
            ivPull.setImageResource(R.drawable.ic_xiala);
            if (count <= 12) {
                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight * 3), DensityUtil.dp2px(baseHeight * 2));
            } else {
                AnimUtil.changeViewHeightAnimatorStart(rvFunctionCard, DensityUtil.dp2px(baseHeight * 4), DensityUtil.dp2px(baseHeight * 2));
            }
        }
    }


    private void initRecycleList() {
        if (statementAdapter == null) {
            statementAdapter = new PigFragmentAdapter(getActivity(), list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(statementAdapter);
    }

    private void unReadVoiceMail(String setOnUbtTIMConver) {
        Log.e(setOnUbtTIMConver, "unRead message " + UbtTIMManager.getInstance().unReadVoiceMailMessage());
        if (UbtTIMManager.getInstance().unReadVoiceMailMessage() >= 1) {
            Log.e(setOnUbtTIMConver, "unRead message " + UbtTIMManager.getInstance().unReadVoiceMailMessage());
            showFunctionRedPoint(MainFunctionAdapter.FunctionEnum.VOICE_MAIL, true);
        } else {
            showFunctionRedPoint(MainFunctionAdapter.FunctionEnum.VOICE_MAIL, false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        updateUserPig();
        initFunctionCard();
        initRecycleList();
        fetchFunctionCardData();
        refreshData();
        checkUpdate();
    }

    private void fetchFunctionCardData() {
        String md5_category = SharedPreferencesUtils.getString(getActivity(), "md5_category", "");
        String md5_statement = SharedPreferencesUtils.getString(getActivity(), "md5_statement", "");
        new HomeDataHttpProxy().getData(getActivity(), md5_category, md5_statement, new HomeDataHttpProxy.GetFunctionCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "fetchFunctionCardData error:" + error);
            }

            @Override
            public void onSuccess(FunctionModel functionModel) {
                UbtLogger.d(TAG, "fetchFunctionCardData onSuccess");
                Event<FunctionModel> event = new Event<>(EventBusUtil.UPDATE_HOME_FUNCTION_CARD);
                event.setData(functionModel);
                EventBusUtil.sendEvent(event);
            }
        });
    }

    @OnClick({R.id.btn_bt_binding, R.id.rl_wifi, R.id.tv_change, R.id.iv_pull})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bt_binding:
                toBleConfigActivity(null, false);
                break;
            case R.id.rl_wifi:
                mCustomPopupWindow = new CustomPopupWindow(getActivity())
                        .showAtBottom(rlWifi)
                        .setShowVal(tvWifiName.getText().toString())
                        .setCallback(new CustomPopupWindow.Callback() {
                            @Override
                            public void onDismiss() {
                                ivUpDown.setImageResource(R.drawable.ic_arrow_down);
                                mCustomPopupWindow = null;
                            }
                        });
                ivUpDown.setImageResource(R.drawable.ic_arrow_up);
                break;
            case R.id.tv_change:
                SCADAHelper.recordEvent("app_clickbtn_home_another");
                if (mFunctionModel != null) {
                    statementAdapter.updateData(mFunctionModel);
                }
                break;
            case R.id.iv_pull:
                changeFunctionCardListHeight();
                break;
        }
    }

    /**
     * 处理 tvNetTip（手机网络/机器人离线）；btnConfigWifi（重新配网）；btnBinding（开始绑定与配网）；tvPigTip（你的智能语音小伙伴）；
     * rlNativeInfo（机器人信息）
     */
    private void updateUserPig() {
        if (!UBTPGApplication.isNetAvailable) {
            tvNetTip.setText("手机无网络连接");
            tvNetTip.setVisibility(View.VISIBLE);

            btnConfigWifi.setVisibility(View.GONE);
            btnBinding.setVisibility(View.GONE);
            tvPigTip.setVisibility(View.GONE);
            hideNativeInfo();
        } else {
            tvNetTip.setVisibility(View.GONE);
            btnConfigWifi.setVisibility(View.GONE);
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo != null) {
                btnBinding.setVisibility(View.GONE);
                String name = pigInfo.getRobotName();
                if (!TextUtils.isEmpty(name) && name.length() >= 4) {
                    name = name.substring(name.length() - 4, name.length());
                }
                tvPigSn.setText(getString(R.string.ubt_bajie, name));
                tvPigSn.setVisibility(View.VISIBLE);
                if (pigInfo.isAdmin) {
                    //TODO 管理员
                    if (UBTPGApplication.isRobotOnline) {
                        //TODO 在线
                        tvPigTip.setVisibility(View.GONE);
//                        rlNativeInfo.setVisibility(View.VISIBLE);
                    } else {
                        //TODO 离线
                        tvPigTip.setVisibility(View.GONE);
                        hideNativeInfo();
                        tvNetTip.setText("机器人离线");
                        tvNetTip.setVisibility(View.VISIBLE);
                        btnConfigWifi.setVisibility(View.VISIBLE);
                        btnConfigWifi.setOnClickListener(v -> ActivityRoute.toAnotherActivity(getActivity(), BleConfigReadyActivity.class, false));
                    }
                } else {
                    //TODO 普通成员
                    tvPigTip.setText("你的智能语音小伙伴");
                    tvPigTip.setVisibility(View.VISIBLE);
                }
            } else {
                btnBinding.setVisibility(View.VISIBLE);
                tvPigSn.setVisibility(View.GONE);
                tvPigTip.setVisibility(View.GONE);
                hideNativeInfo();
            }
        }
    }

    /**
     * 隐藏小猪基本状态信息栏
     */
    private void hideNativeInfo() {
        rlNativeInfo.setVisibility(View.GONE);
        if (mCustomPopupWindow != null) {
            mCustomPopupWindow.dismiss();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint = " + isVisibleToUser);
        if (isVisibleToUser) {
            mHandler.sendEmptyMessage(GET_NATIVE_INFO);
        } else {
            if (mHandler.hasMessages(GET_NATIVE_INFO)) {
                mHandler.removeMessages(GET_NATIVE_INFO);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            unReadVoiceMail("setOnUbtTIMConver-DEBUG");
        }

//        restoreCard();

    }


    private void checkUpdate() {
        if (SharedPreferencesUtils.getBoolean(getActivity(), "isNotNeedShow", false)) {
            return;
        }
        UbtLogger.d(TAG, "start checkUpdate");
        new CheckUpdateHttpProxy().checkUpdate(new CheckUpdateHttpProxy.GetFunctionCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "updateInfoModel onError:" + error);
            }

            @Override
            public void onSuccess(UpdateInfoModel updateInfoModel) {
                UbtLogger.d(TAG, "updateInfoModel:" + updateInfoModel.toString());
                Event<UpdateInfoModel> event = new Event<>(APP_UPDATE_CHECK);
                event.setData(updateInfoModel);
                EventBusUtil.sendEvent(event);
            }
        });
    }

    @Override
    protected void onNoPig() {

    }

    @Override
    protected void onNoSetNet() {

    }

    @Override
    protected void onHasPig() {

    }

    @Override
    protected void onSetedNet() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        if (mHandler.hasMessages(GET_NATIVE_INFO)) {
            mHandler.removeMessages(GET_NATIVE_INFO);
        }

        EventBusUtil.unregister(this);
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null || isDetached() || isRemoving() || !isAdded()) return;
        int code = event.getCode();
        switch (code) {
            case INVISE_RECORD_POINT:
                SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, 0);
                showFunctionRedPoint(MainFunctionAdapter.FunctionEnum.CALL_RECORD, false);
                break;
            case USER_PIG_UPDATE:
                updateUserPig();
                break;
            case NEW_MESSAGE_NOTIFICATION:
                LogUtils.d("PigNewFragment", "new voice message");
                showFunctionRedPoint(MainFunctionAdapter.FunctionEnum.VOICE_MAIL, true);
                break;
            case NEW_CALL_RECORD:
                showFunctionRedPoint(MainFunctionAdapter.FunctionEnum.CALL_RECORD, true);
                break;
            case NETWORK_STATE_CHANGED:
                updateUserPig();
                break;
            case RECEIVE_NATIVE_INFO:
                UpdateNativeInfo((NativeInfoContainer.NativeInfo) event.getData());
                updateUserPig();
                break;
            case DO_GET_NATIVE_INFO:
                mHandler.sendEmptyMessage(GET_NATIVE_INFO);
                break;
            case UPDATE_HOME_FUNCTION_CARD:
                updateFunctionCard((FunctionModel) event.getData());
                break;
            case RECEIVE_ROBOT_ONLINE_STATE:
                updateUserPig();
                break;
            case APP_UPDATE_CHECK:
                showUpdateDialog((UpdateInfoModel) event.getData());
                break;
        }
    }


    private void showUpdateDialog(UpdateInfoModel updateInfoModel) {
        UBTUpdateDialog dialog = new UBTUpdateDialog(getActivity());
        dialog.setRightBtnColor(ContextCompat.getColor(getActivity(), R.color.ubt_tab_btn_txt_checked_color));
        dialog.setTips("发现新版本" + updateInfoModel.getVersion());
        dialog.setSubTips(updateInfoModel.getVersionInfo());
        dialog.setSubTipGravity(Gravity.CENTER);
        dialog.setLeftButtonTxt("下次再说");
        dialog.setRightButtonTxt("立即更新");
        dialog.showNoTip(true);
        if (updateInfoModel.getUpdateType().equals("2")) {
            dialog.setOnlyOneButton();
        }
        dialog.setOnUbtDialogContentClickLinsenter(new UBTUpdateDialog.OnUbtDialogContentClickLinsenter() {
            @Override
            public void onNotipClick(View view) {
                //TODO sp记录勾选状态
                UbtLogger.d(TAG, "view:" + view.isSelected());
                if (view.isSelected()) {
                    SharedPreferencesUtils.putBoolean(getActivity(), "isNotNeedShow", true);
                } else {
                    SharedPreferencesUtils.putBoolean(getActivity(), "isNotNeedShow", false);
                }
            }
        });
        dialog.setOnUbtDialogClickLinsenter(new UBTUpdateDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO goto ble bind config
//                HashMap<String, String> map = new HashMap<>();
//                map.put("url", updateInfoModel.getUrl());
                ActivityRoute.toAnotherActivity(getActivity(), CommonWebActivity.class, UbtWebHelper.getUpdateInfoWebviewData(getActivity(), updateInfoModel.getUrl()),
                        false);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    /**
     * 更新小猪基本信息
     *
     * @param data
     */
    private void UpdateNativeInfo(NativeInfoContainer.NativeInfo data) {
        NativeInfoContainer.NativeInfo nativeInfo = data;
        try {
            NativeInfoContainer.BleStatus bleStatus = nativeInfo.getBleStatus().unpack(NativeInfoContainer.BleStatus.class);
            NativeInfoContainer.SimStatus simStatus = nativeInfo.getSimStatus().unpack(NativeInfoContainer.SimStatus.class);
            NativeInfoContainer.BatteryStatus batteryStatus = nativeInfo.getBatteryStatus().unpack(NativeInfoContainer.BatteryStatus.class);
            NativeInfoContainer.NetworkStatus networkStatus = nativeInfo.getNetworkStatus().unpack(NativeInfoContainer.NetworkStatus.class);

            if (rlNativeInfo.getVisibility() != View.VISIBLE) {
                rlNativeInfo.setVisibility(View.VISIBLE);
                tvPigTip.setVisibility(View.GONE);
            }

            //更新蓝牙
            if (bleStatus.getOpen()) {
                if (bleStatus.getConnected()) {
                    ivBle.setImageResource(R.drawable.ic_bt_connected_connect);
                } else {
                    ivBle.setImageResource(R.drawable.ic_bt_connected);
                }
            } else {
                ivBle.setImageResource(R.drawable.ic_bt_disconnected);
            }

            //更新sim卡信号
            if (simStatus.getInserted()) {
                MainActivity.isNoSim = false;
                if (simStatus.getOpen()) {
                    MainActivity.isBeeHiveOpen = true;
                } else {
                    MainActivity.isBeeHiveOpen = false;
                }
                ((MainActivity) getActivity()).pigPhoneNumber = simStatus.getPhoneNumber();
                int level = simStatus.getLevel();
                if (level > 4) {
                    level = 4;
                }
                if (level < 1) {
                    level = 1;
                }
                ivSignal.setImageLevel(level);
                if (!networkStatus.getWifiState() && networkStatus.getMobileState() != 0) {
                    showMobileFlowDialog();
                }
            } else {
                MainActivity.isNoSim = true;
                ((MainActivity) getActivity()).pigPhoneNumber = "";
                ivSignal.setImageLevel(0);
            }

            //更新电量
            int electricity = batteryStatus.getElectricity();
            tvBattery.setText(electricity + "%");

            ivBattery.setImageLevel((int) Math.ceil(electricity / 10f));


            //更新网络连接信息
            if (networkStatus.getWifiState()) {
                rlWifi.setVisibility(View.VISIBLE);
                ivSimNet.setVisibility(View.GONE);

                ivWifi.setImageLevel(networkStatus.getLevel());
                tvWifiName.setText(networkStatus.getSsid());
            } else {
                if (networkStatus.getMobileState() == 0) {
                    rlWifi.setVisibility(View.VISIBLE);
                    ivSimNet.setVisibility(View.GONE);
                } else {
                    rlWifi.setVisibility(View.GONE);
                    ivSimNet.setVisibility(View.VISIBLE);
                    int simNetLevel = networkStatus.getMobileState() - 2;
                    if (simNetLevel < 0) {
                        simNetLevel = 0;
                    }
                    ivSimNet.setImageLevel(simNetLevel);
                }
            }

            Log.d(TAG, "batteryStatus " + batteryStatus + " bleStatus = " + bleStatus + " simStatus = " + simStatus + " networkStatus = " + networkStatus);
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, "e = " + e.getMessage());
            e.printStackTrace();
            ToastUtils.showLongToast("获取基本信息失败");
        }
    }

    private void updateFunctionCard(FunctionModel functionModel) {
        this.mFunctionModel = functionModel;
        if (statementAdapter != null && functionAdapter != null) {
            hasRefreshFromServer = false;
            refreshData();
        } else {
            hasRefreshFromServer = false;
        }
    }

    private void refreshData() {
        try {
            if (!hasRefreshFromServer && mFunctionModel != null) {
                statementAdapter.updateData(mFunctionModel);
                functionAdapter.updateData(mFunctionModel);
                tvStatementTitle.setText(mFunctionModel.statement.title);
                hasRefreshFromServer = true;
                changeLayout();
            }
        } catch (Exception e) {
            LogUtils.d("PigNewFragment", "refreshData:" + e.getMessage());
        }
    }

    private void changeLayout() {
        if (mFunctionModel.catetory.categorys.size() > 8) {
            ivPull.setVisibility(View.VISIBLE);
            restoreCard();
        } else {
            ivPull.setVisibility(View.GONE);
        }

    }

    private void showMobileFlowDialog() {
        if (!UBTPGApplication.hasShowedMobileFlowTip && !SharedPreferencesUtils.getBoolean(getActivity(), "mobileFlowTip", false)) {
            if (mMobileFlowDialog == null) {
                mMobileFlowDialog = new UBTSubTitleDialog(getActivity());
                mMobileFlowDialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
                mMobileFlowDialog.setSubTipColor(ContextCompat.getColor(getActivity(), R.color.ubt_tips_txt_color));
                mMobileFlowDialog.setTips(getString(R.string.mobile_flow_title));
                mMobileFlowDialog.showNoTip(true);
                mMobileFlowDialog.setOnlyOneButton();
                mMobileFlowDialog.setRightButtonTxt(getString(R.string.i_know_text));
                mMobileFlowDialog.setSubTips(getString(R.string.mobile_flow_tip));
                mMobileFlowDialog.setSubTipGravity(Gravity.LEFT);
                mMobileFlowDialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                    @Override
                    public void onLeftButtonClick(View view) {

                    }

                    @Override
                    public void onRightButtonClick(View view) {
                    }
                });
                mMobileFlowDialog.setOnUbtDialogContentClickLinsenter(view -> SharedPreferencesUtils.putBoolean(getActivity(), "mobileFlowTip", view.isSelected()));
            }
            Activity activity = getActivity();
            if (activity != null && !activity.isDestroyed() && !activity.isFinishing() && !mMobileFlowDialog.isShowing()) {
                mMobileFlowDialog.show();
                UBTPGApplication.hasShowedMobileFlowTip = true;
            }
        }
    }

}

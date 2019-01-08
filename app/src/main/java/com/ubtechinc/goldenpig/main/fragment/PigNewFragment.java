package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.widget.CustomPopupWindow;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.FunctionModel;
import com.ubtechinc.goldenpig.main.HomeDataHttpProxy;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtrobot.info.NativeInfoContainer;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;

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
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DO_GET_NATIVE_INFO;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.INVISE_RECORD_POINT;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NETWORK_STATE_CHANGED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEW_CALL_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEW_MESSAGE_NOTIFICATION;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_NATIVE_INFO;
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
    Button btnBinding;

    @BindView(R.id.tv_net_tip)
    View tvNetTip;

    @BindView(R.id.recycler_skill)
    RecyclerView recyclerView;

    @BindView(R.id.rv_function_card)
    RecyclerView rvFunctionCard;

    @BindView(R.id.iv_ble)
    ImageView ivBle;
    @BindView(R.id.iv_signal)
    ImageView ivSignal;
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

    PigFragmentAdapter adapter;
    @BindView(R.id.tv_statement_title)
    TextView tvStatementTitle;

    @BindView(R.id.tv_pig_tip)
    TextView tvPigTip;

    PigFragmentAdapter catetoryAdapter;

    MainFunctionAdapter mainFunctionAdapter;
    private CustomPopupWindow mCustomPopupWindow = null;
    MainFunctionAdapter statementAdapter;

    private boolean hasRefreshFromServer;

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
                        if (rlNativeInfo != null && rlNativeInfo.getVisibility() == View.VISIBLE) {
                            tvPigTip.setVisibility(View.GONE);
                        } else {
                            tvPigTip.setVisibility(View.VISIBLE);
                        }
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
        if (statementAdapter != null) {
            functionEnum.hasRedPoint = isShow;
            statementAdapter.notifyItemChanged(functionEnum);
        }
    }

    private void initFunctionCard() {
        if (statementAdapter == null) {
            statementAdapter = new MainFunctionAdapter(getActivity(), new ArrayList<>(Arrays.asList(MainFunctionAdapter.FunctionEnum.values())));
        }
        rvFunctionCard.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rvFunctionCard.setAdapter(statementAdapter);
    }

    private void initRecycleList() {
        if (catetoryAdapter == null) {
            catetoryAdapter = new PigFragmentAdapter(getActivity(), list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(catetoryAdapter);
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
    }

    private void fetchFunctionCardData() {
        String md5_category = SharedPreferencesUtils.getString(getActivity(), "md5_category", "");
        String md5_statement = SharedPreferencesUtils.getString(getActivity(), "md5_statement", "");
        new HomeDataHttpProxy().getData(getActivity(), md5_category, md5_statement, new HomeDataHttpProxy.GetFunctionCallback() {
            @Override
            public void onError(String error) {

            }

            @Override
            public void onSuccess(FunctionModel functionModel) {
                Event<FunctionModel> event = new Event<>(EventBusUtil.UPDATE_HOME_FUNCTION_CARD);
                event.setData(functionModel);
                EventBusUtil.sendEvent(event);
            }
        });
    }

    @OnClick({R.id.btn_bt_binding, R.id.rl_wifi})
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
        }
    }

    private void updateUserPig() {
        if (!UBTPGApplication.isNetAvailable) {
            tvNetTip.setVisibility(View.VISIBLE);
            btnBinding.setVisibility(View.GONE);
            tvPigTip.setVisibility(View.GONE);
            hideNativeInfo();
        } else {
            tvNetTip.setVisibility(View.GONE);
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo != null) {
                btnBinding.setVisibility(View.GONE);
                String name = pigInfo.getRobotName();
                if (!TextUtils.isEmpty(name) && name.length() >= 4) {
                    name = name.substring(name.length() - 4, name.length());
                }
                tvPigSn.setText(getString(R.string.ubt_bajie, name));
                tvPigSn.setVisibility(View.VISIBLE);
                tvPigTip.setVisibility(View.VISIBLE);
            } else {
                btnBinding.setVisibility(View.VISIBLE);
                tvPigTip.setVisibility(View.GONE);
                tvPigSn.setVisibility(View.GONE);
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
        if (event == null) return;
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
                break;
            case DO_GET_NATIVE_INFO:
                mHandler.sendEmptyMessage(GET_NATIVE_INFO);
                break;
            case UPDATE_HOME_FUNCTION_CARD:
                updateFunctionCard((FunctionModel) event.getData());
                break;
        }
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

            tvPigTip.setVisibility(View.GONE);
            if (rlNativeInfo.getVisibility() != View.VISIBLE) {
                rlNativeInfo.setVisibility(View.VISIBLE);
            }

            //更新蓝牙
            if (bleStatus.getOpen()) {
                ivBle.setImageResource(R.drawable.ic_bt_connected);
            } else {
                ivBle.setImageResource(R.drawable.ic_bt_disconnected);
            }

            //更新sim卡信号
            if (simStatus.getInserted()) {
                ((MainActivity)getActivity()).isNoSim = false;
                int level = simStatus.getLevel();
                if (level > 4) {
                    level = 4;
                }
                if (level < 1) {
                    level = 1;
                }
                ivSignal.setImageLevel(level);
            } else {
                ((MainActivity)getActivity()).isNoSim = true;
                ivSignal.setImageLevel(0);
            }

            //更新电量
            tvBattery.setText(batteryStatus.getElectricity() + "%");

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
        if (catetoryAdapter != null && statementAdapter != null) {
            hasRefreshFromServer = false;
            refreshData();
        } else {
            hasRefreshFromServer = false;
        }
    }

    private void refreshData() {
        try {
            if (!hasRefreshFromServer && mFunctionModel != null) {
                catetoryAdapter.updateData(mFunctionModel);
                statementAdapter.updateData(mFunctionModel);
                tvStatementTitle.setText(mFunctionModel.statement.title);
                hasRefreshFromServer = true;
            }
        } catch (Exception e) {
            LogUtils.d("PigNewFragment", "refreshData:" + e.getMessage());
        }
    }

}

package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
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
import android.widget.TextView;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.INVISE_RECORD_POINT;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NETWORK_STATE_CHANGED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEW_CALL_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEW_MESSAGE_NOTIFICATION;
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

    PigFragmentAdapter adapter;

    MainFunctionAdapter mainFunctionAdapter;

    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pig_new, container, false);
        EventBusUtil.register(this);
        initSkillData();
        return view;
    }

    private void initSkillData() {
        //TODO 后续该数据从后台获取
        list.add("“小猪小猪，放一首歌”");
        list.add("“小猪小猪，发送留言”");
        list.add("“小猪小猪，我好喜欢你”");
        list.add("“小猪小猪，打电话”");
        list.add("“小猪小猪，今天天气怎么样”");
        list.add("“小猪小猪，提醒我下午四点喝水”");
    }

    private void initFunctionCard() {
        if (mainFunctionAdapter == null) {
            mainFunctionAdapter = new MainFunctionAdapter(getActivity(), new ArrayList<>(Arrays.asList(MainFunctionAdapter.FunctionEnum.values())));
        }
        rvFunctionCard.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rvFunctionCard.setAdapter(mainFunctionAdapter);
    }

    /**
     * 功能卡片更新
     */
    private void showFunctionRedPoint(MainFunctionAdapter.FunctionEnum functionEnum, boolean isShow) {
        if (mainFunctionAdapter != null) {
            functionEnum.hasRedPoint = isShow;
            mainFunctionAdapter.notifyItemChanged(functionEnum);
        }
    }

    private void initRecycleList() {
        adapter = new PigFragmentAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
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
        updateUserPig();
        initFunctionCard();
        initRecycleList();
    }

    @OnClick({R.id.btn_bt_binding})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bt_binding:
                toBleConfigActivity(null, false);
                break;
        }
    }

    private void updateUserPig() {
        if (!UBTPGApplication.isNetAvailable) {
            tvNetTip.setVisibility(View.VISIBLE);
            btnBinding.setVisibility(View.GONE);
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
            } else {
                btnBinding.setVisibility(View.VISIBLE);
                tvPigSn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        EventBusUtil.unregister(this);
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
        }
    }

}

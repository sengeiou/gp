package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.UserRecords;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.SkillActivity;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairQRScannerActivity;
import com.ubtechinc.goldenpig.pigmanager.register.GetPairPigQRHttpProxy;
import com.ubtechinc.goldenpig.pigmanager.register.UnpairHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CONTACT_PIC_SUCCESS;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :小猪分页Fragment
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class PigFragment extends BaseFragment implements Observer {

    @BindView(R.id.ubt_layout_tips)
    View mTipsView;

    @BindView(R.id.ubt_bind_tv)
    View mBindClickTv;

    @BindView(R.id.view_pig_pair_add)
    View viewPigPairAdd;

    @BindView(R.id.view_pig_pair_info)
    View viewPigPairInfo;

    @BindView(R.id.rl_pair_pig)
    View rlPairPig;

    @BindView(R.id.ll_voicechat)
    LinearLayout llVoiceChat;

    @BindView(R.id.ll_record)
    View llRecord;

    RelativeLayout llVoiceChat;
    @BindView(R.id.ubt_tv_call_sub_title)
    TextView ubt_tv_call_sub_title;
    @BindView(R.id.iv_unreadvoice)
    ImageView mVoiceUnRead;

    int pairUserId;
    String serialNumber;
    String pairSerialNumber;
    int userId;

    public PigFragment() {
        super();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pig, container, false);
        EventBusUtil.register(this);
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setMsgObserve(this);
            UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
                @Override
                public void onError(int i, String s) {
                    Log.e("setOnUbtTIMConver", s);
//                    ToastUtils.showShortToast(s);
                }

                @Override
                public void onSuccess() {
                    Log.e("setOnUbtTIMConver", "sss");
                }
            });
            UbtTIMManager.getInstance().queryLatestRecord();
            //unReadVoiceMail("setOnUbtTIMConver");
        }
        return view;
    }

    private void unReadVoiceMail(String setOnUbtTIMConver) {
        Log.e(setOnUbtTIMConver, "unRead message " + UbtTIMManager.getInstance().unReadVoiceMailMessage());
        if (UbtTIMManager.getInstance().unReadVoiceMailMessage() >= 1) {
            Log.e(setOnUbtTIMConver, "unRead message " + UbtTIMManager.getInstance().unReadVoiceMailMessage());
            mVoiceUnRead.setVisibility(View.VISIBLE);
        }else{
            mVoiceUnRead.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin && pigInfo.isOnline()) {
            llVoiceChat.setAlpha(1.0f);
            rlPairPig.setAlpha(1.0f);
            unReadVoiceMail("setOnUbtTIMConver-DEBUG");
            llRecord.setAlpha(1.0f);
        } else {
            llVoiceChat.setAlpha(0.5f);
            rlPairPig.setAlpha(0.5f);
            llRecord.setAlpha(0.5f);
        }
        updatePigPair();
    }

    private void updatePigPair() {
        new GetPairPigQRHttpProxy().getPairPigQR(getActivity(), CookieInterceptor.get().getToken(), BuildConfig
                .APP_ID, new GetPairPigQRHttpProxy.GetPairPigQRCallBack() {
            @Override
            public void onError(String error) {

                //TODO 获取配对数据失败
                showPigPair(false);
            }

            @Override
            public void onSuccess(String response) {

                //TODO 刷新配对信息
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject != null) {
                            JSONObject pairData = new JSONObject(jsonObject.optString("pairData"));
                            if (pairData != null) {
                                pairUserId = pairData.optInt("pairUserId");
                                serialNumber = pairData.optString("serialNumber");
                                pairSerialNumber = pairData.optString("pairSerialNumber");
                                userId = pairData.optInt("userId");
                                showPigPair(true);
                            }
                        }
                    } catch (JSONException e) {
                        UBTLog.e("pig", e.getMessage());
                    }
                }
            }
        });
    }

    private void showPigPair(boolean hasPair) {
        viewPigPairInfo.setVisibility(hasPair ? View.VISIBLE : View.GONE);
        viewPigPairAdd.setVisibility(!hasPair ? View.VISIBLE : View.GONE);
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

    @OnClick({R.id.ubt_bind_tv, R.id.ll_record, R.id.ll_voicechat, R.id.view_pig_pair_add, R.id.view_pig_pair_info, R
            .id.view_skill})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.ll_voicechat:
                PigInfo pigInfo0 = AuthLive.getInstance().getCurrentPig();
                if (pigInfo0 != null && pigInfo0.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), ChatActivity.class, false);
                }
                if (UBTPGApplication.voiceMail_debug) {
                    ActivityRoute.toAnotherActivity(getActivity(), ChatActivity.class, false);
                }
                break;
            case R.id.view_pig_pair_add:
                PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                if (pigInfo != null && pigInfo.isAdmin) {
                    //TODO 配对小猪
                    ActivityRoute.toAnotherActivity(getActivity(), PairQRScannerActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.view_pig_pair_info:
                //TODO 解除配对
//                doUnPair();
                HashMap<String, String> map = new HashMap<>();
                map.put("unPairUserId", String.valueOf(pairUserId));
                ActivityRoute.toAnotherActivity(getActivity(), PairPigActivity.class, map, false);
                break;
            case R.id.ubt_bind_tv:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, false);
                break;
            case R.id.ll_record:
            {
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), RecordActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }

            }
                break;
            case R.id.view_skill:
                ActivityRoute.toAnotherActivity(getActivity(), SkillActivity.class, false);
                break;
            default:
        }
    }

    private void doUnPair() {
        UnpairHttpProxy httpProxy = new UnpairHttpProxy();
        httpProxy.doUnpair(CookieInterceptor.get().getToken(),
                BuildConfig.APP_ID, String.valueOf(pairUserId), new UnpairHttpProxy.UnpairCallBack() {
                    @Override
                    public void onError() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(R.string.ubt_ubpair_filure);
                            }
                        });

                    }

                    @Override
                    public void onSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(R.string.ubt_ubpair_success);
                                showPigPair(false);
                            }
                        });
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusUtil.unregister(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            TIMMessage msg = (TIMMessage) arg;
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
                dealMsg(elem.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        switch (action) {
            case "/im/record/latest":
                List<UserRecords.Record> list = msg.getPayload().unpack(UserRecords.UserRecord
                        .class).getRecordList();
                List<RecordModel> ss = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    RecordModel mo = new RecordModel();
                    mo.name = list.get(j).getName();
                    mo.number = list.get(j).getNumber();
                    mo.id = list.get(j).getId();
                    mo.type = list.get(j).getType();
                    mo.dateLong = list.get(j).getDateLong() * 1000;
                    mo.duration = list.get(j).getDuration();
                    ss.add(mo);
                }
                if (ss.size() == 0) {
                    ubt_tv_call_sub_title.setText("无");
                } else if (!TextUtils.isEmpty(ss.get(0).name)) {
                    ubt_tv_call_sub_title.setText(ss.get(0).name);
                } else {
                    ubt_tv_call_sub_title.setText(ss.get(0).number);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event != null && event.getCode() == CONTACT_PIC_SUCCESS) {
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo != null) {
                UbtTIMManager.getInstance().setMsgObserve(this);
                UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e("setOnUbtTIMConver", s);
                        ToastUtils.showShortToast(s);
                    }

                    @Override
                    public void onSuccess() {
                        Log.e("setOnUbtTIMConver", "sss");
                    }
                });
                UbtTIMManager.getInstance().queryLatestRecord();
            }
        }
    }
}

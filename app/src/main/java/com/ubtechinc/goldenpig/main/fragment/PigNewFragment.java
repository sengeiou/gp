package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
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
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionActivity;
import com.ubtechinc.goldenpig.personal.remind.RemindActivity;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.pigmanager.register.GetPairPigQRHttpProxy;
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

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.app.Constant.SP_LAST_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CONTACT_PIC_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.INVISE_RECORD_POINT;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.PAIR_PIG_UPDATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :八戒分页Fragment
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class PigNewFragment extends BaseFragment implements Observer {

    @BindView(R.id.btn_bt_binding)
    Button btnBinding;
    @BindView(R.id.rl_voice_message)
    RelativeLayout rlVoiceMessage;
    @BindView(R.id.rl_bajie_pair)
    RelativeLayout rlBaJiePair;
    @BindView(R.id.rl_alarm)
    RelativeLayout rlAlarm;
    @BindView(R.id.rl_schedule)
    RelativeLayout rlSchedule;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;
    @BindView(R.id.rl_call_record)
    RelativeLayout rlCallRecord;
    @BindView(R.id.rl_bt_speaker)
    RelativeLayout rlBtSpeaker;
    @BindView(R.id.recycler_skill)
    RecyclerView recyclerView;

    PigFragmentAdapter adapter;
    private List<String> list = new ArrayList<String>();


    public PigNewFragment() {
        super();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pig_new, container, false);
        EventBusUtil.register(this);
        sendRecordMsg();
        initSkillData();
        return view;
    }

    private void initSkillData() {
        //TODO 后续该数据从后台获取
        list.add("小猪小猪，放一首歌");
        list.add("小猪小猪，放一首歌");
        list.add("小猪小猪，放一首歌");
        list.add("小猪小猪，放一首歌");
        list.add("小猪小猪，放一首歌");
        list.add("小猪小猪，放一首歌");

    }

    private void initRecycleList(){
        adapter = new PigFragmentAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void unReadVoiceMail(String setOnUbtTIMConver) {
   /*     if (mVoiceUnRead == null) return;
        Log.e(setOnUbtTIMConver, "unRead message " + UbtTIMManager.getInstance().unReadVoiceMailMessage());
        if (UbtTIMManager.getInstance().unReadVoiceMailMessage() >= 1) {
            Log.e(setOnUbtTIMConver, "unRead message " + UbtTIMManager.getInstance().unReadVoiceMailMessage());
            mVoiceUnRead.setVisibility(View.VISIBLE);
        } else {
            mVoiceUnRead.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUserPig();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecycleList();
        updatePigPair();
    }

    @OnClick({R.id.btn_bt_binding, R.id.rl_voice_message, R.id.rl_bajie_pair, R.id.rl_alarm, R.id.rl_schedule,
                R.id.rl_answer, R.id.rl_call_record, R.id.rl_bt_speaker})
    public void onClick(View view){
        PigInfo myPig = AuthLive.getInstance().getCurrentPig();
        switch (view.getId()){
            case R.id.btn_bt_binding:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, false);
                break;
            case R.id.rl_voice_message:

                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), ChatActivity.class, false);
                }
                if (UBTPGApplication.voiceMail_debug) {
                    ActivityRoute.toAnotherActivity(getActivity(), ChatActivity.class, false);
                }
                break;
            case R.id.rl_bajie_pair:
                if (myPig != null && myPig.isAdmin) {
                    //TODO 配对二维码
                    HashMap<String, Boolean> param = new HashMap<>();
                    param.put("isPair", true);
                    ActivityRoute.toAnotherActivity(getActivity(), QRCodeActivity.class, param, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.rl_alarm:
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), AlarmListActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.rl_schedule:
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), RemindActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.rl_answer:
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), InterlocutionActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.rl_call_record:
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), RecordActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.rl_bt_speaker:
                ToastUtils.showShortToast(R.string.main_bt_speaker);
                break;

        }
    }

    private void updateUserPig() {
    /*    PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin && pigInfo.isOnline()) {
            llVoiceChat.setAlpha(1.0f);
            rlPairPig.setAlpha(1.0f);
            unReadVoiceMail("setOnUbtTIMConver-DEBUG");
            llRecord.setAlpha(1.0f);
            viewAnswer.setAlpha(1.0f);
            viewAlarm.setAlpha(1.0f);
            viewRemind.setAlpha(1.0f);
            //TODO
            ubt_tv_pig_title.setAlpha(1.0f);
            ubt_tv_pig_sub_title.setAlpha(1.0f);
            ubt_tv_answer_title.setAlpha(1.0f);
            ubt_tv_answer_sub_title.setAlpha(1.0f);
            ubt_tv_alarm_title.setAlpha(1.0f);
            ubt_tv_alarm_sub_title.setAlpha(1.0f);
            ubt_tv_remind_title.setAlpha(1.0f);
            ubt_tv_remind_sub_title.setAlpha(1.0f);
            ubt_tv_call_title.setAlpha(1.0f);
            ubt_tv_call_sub_title.setAlpha(1.0f);
            viewPigPairAdd.setAlpha(1.0f);

            String lastRecord = SPUtils.get().getString(SP_LAST_RECORD);
            ubt_tv_call_sub_title.setText((TextUtils.isEmpty(lastRecord) ? "无" : lastRecord));
            int type = SPUtils.get().getInt(SP_HAS_LOOK_LAST_RECORD);
            if (type == 3) {
                iv_unreadrecord.setVisibility(View.VISIBLE);
            } else {
                iv_unreadrecord.setVisibility(View.INVISIBLE);
            }
        } else {
            llVoiceChat.setAlpha(0.5f);
            rlPairPig.setAlpha(0.5f);
            llRecord.setAlpha(0.5f);
            viewAnswer.setAlpha(0.5f);
            viewAlarm.setAlpha(0.5f);
            viewRemind.setAlpha(0.5f);
            //TODO
            ubt_tv_pig_title.setAlpha(0.5f);
            ubt_tv_pig_sub_title.setAlpha(0.5f);
            ubt_tv_answer_title.setAlpha(0.5f);
            ubt_tv_answer_sub_title.setAlpha(0.5f);
            ubt_tv_alarm_title.setAlpha(0.5f);
            ubt_tv_alarm_sub_title.setAlpha(0.5f);
            ubt_tv_remind_title.setAlpha(0.5f);
            ubt_tv_remind_sub_title.setAlpha(0.5f);
            ubt_tv_call_title.setAlpha(0.5f);
            ubt_tv_call_sub_title.setAlpha(0.5f);
            viewPigPairAdd.setAlpha(0.5f);

            ubt_tv_call_sub_title.setText("无");
            iv_unreadrecord.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo != null && pigInfo.isAdmin && pigInfo.isOnline()) {
                unReadVoiceMail("setOnUbtTIMConver-DEBUG");
            }
        }
    }

    private void updatePigPair() {
        new GetPairPigQRHttpProxy().getPairPigQR(getActivity(), CookieInterceptor.get().getToken(), BuildConfig
                .APP_ID, new GetPairPigQRHttpProxy.GetPairPigQRCallBack() {
            @Override
            public void onError(String error) {

                //TODO 获取配对数据失败
                showPigPair(false);
                AuthLive.getInstance().setPairPig(null);
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
                      /*          pairUserId = pairData.optInt("pairUserId");
                                serialNumber = pairData.optString("serialNumber");
                                pairSerialNumber = pairData.optString("pairSerialNumber");
                                userId = pairData.optInt("userId");
                                PairPig pairPig = new PairPig();
                                pairPig.setPairUserId(pairUserId);
                                pairPig.setSerialNumber(serialNumber);
                                pairPig.setPairSerialNumber(pairSerialNumber);
                                pairPig.setUserId(userId);
                                AuthLive.getInstance().setPairPig(pairPig);
                                showPigPair(true);*/
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
/*        if (hasPair) {
            UBTPGApplication.mPairSerialNumber = pairSerialNumber;
        } else {
            UBTPGApplication.mPairSerialNumber = null;
        }
        if (viewPigPairInfo != null) {
            viewPigPairInfo.setVisibility(hasPair ? View.VISIBLE : View.GONE);
        }
        if (viewPigPairAdd != null) {
            viewPigPairAdd.setVisibility(!hasPair ? View.VISIBLE : View.GONE);
        }*/
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

   /* @OnClick({R.id.ubt_bind_tv, R.id.ll_record, R.id.ll_voicechat, R.id.view_pig_pair_add, R.id.view_pig_pair_info, R
            .id.view_skill, R.id.view_answer, R.id.view_alarm, R.id.view_remind})
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
                    //TODO 配对二维码
                    HashMap<String, Boolean> param = new HashMap<>();
                    param.put("isPair", true);
                    ActivityRoute.toAnotherActivity(getActivity(), QRCodeActivity.class, param, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.view_pig_pair_info:
                //TODO 配对列表
                HashMap<String, String> map = new HashMap<>();
                map.put("serialNumber", serialNumber);
                map.put("pairSerialNumber", pairSerialNumber);
                map.put("pairUserId", String.valueOf(pairUserId));
                ActivityRoute.toAnotherActivity(getActivity(), PairPigActivity.class, map, false);
                break;
            case R.id.ubt_bind_tv:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, false);
                break;
            case R.id.ll_record: {
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), RecordActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }

            }
            break;
            case R.id.view_answer: {
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), InterlocutionActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
            }
            break;
            case R.id.view_alarm: {
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), AlarmListActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }

            }
            break;
            case R.id.view_remind: {
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig != null && myPig.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), RemindActivity.class, false);
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
*/
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
        /*ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
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
                    iv_unreadrecord.setVisibility(View.INVISIBLE);
                } else if (!TextUtils.isEmpty(ss.get(0).name)) {
                    ubt_tv_call_sub_title.setText(ss.get(0).name);
                    if (ss.get(0).type == 3) {
                        iv_unreadrecord.setVisibility(View.VISIBLE);
                    } else {
                        iv_unreadrecord.setVisibility(View.INVISIBLE);
                    }
                    SPUtils.get().put(SP_LAST_RECORD, ss.get(0).name);
                    SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, ss.get(0).type);
                } else {
                    ubt_tv_call_sub_title.setText(ss.get(0).number);
                    if (ss.get(0).type == 3) {
                        iv_unreadrecord.setVisibility(View.VISIBLE);
                    } else {
                        iv_unreadrecord.setVisibility(View.INVISIBLE);
                    }
                    SPUtils.get().put(SP_LAST_RECORD, ss.get(0).number);
                    SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, ss.get(0).type);
                }
                break;
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
  /*      if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case CONTACT_PIC_SUCCESS:
                LogUtils.d("hdf", "CONTACT_PIC_SUCCESS");
                sendRecordMsg();
                break;
            case INVISE_RECORD_POINT:
                iv_unreadrecord.setVisibility(View.INVISIBLE);
                SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, 0);
                break;
            case USER_PIG_UPDATE:
                showTips();
                updateUserPig();
                break;
            case PAIR_PIG_UPDATE:
                updatePigPair();
                break;
        }*/
    }

    public void sendRecordMsg() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            UbtTIMManager.getInstance().setMsgObserve(this);
        }
    }
}

package com.ubtechinc.goldenpig.personal.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tencent.ai.tvs.business.UniAccessInfo;
import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.DeviceManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AlarmModel;
import com.ubtechinc.goldenpig.personal.remind.SetRemindRepeatActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.weigan.loopview.LoopView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_REPEAT_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_REPEAT_SUCCESS;

public class AddAlarmActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.loopView_date)
    LoopView loopView_date;
    @BindView(R.id.loopView_hour)
    LoopView loopView_hour;
    @BindView(R.id.loopView_minute)
    LoopView loopView_minute;
    @BindView(R.id.tv_cycle)
    TextView tv_cycle;
    private MyHandler mHandler;
    private List<String> dateList;
    private List<String> hourList;
    private List<String> minList;
    private String repeatType;

    private class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ToastUtils.showShortToast("请求超时，请重试");
                if (mWeakReference.get() != null) {
                }
            }
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_alarm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
        initData();
        loopView_date.setItems(dateList);
        loopView_date.setItemsVisibleCount(0);
        loopView_date.setTextSize(18);
        loopView_date.setNotLoop();
        loopView_hour.setItems(hourList);
        loopView_hour.setItemsVisibleCount(0);
        loopView_hour.setTextSize(18);
        loopView_minute.setItems(minList);
        loopView_minute.setItemsVisibleCount(0);
        loopView_minute.setTextSize(18);

    }


    @OnClick({R.id.rl_recount, R.id.tv_left, R.id.tv_right})
    void rl_recount(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.rl_recount:
                Intent it = new Intent(this, SetRemindRepeatActivity.class);
                it.putExtra("type", 1);
                startActivity(it);
                //ActivityRoute.toAnotherActivity(this, SetRepeatActivity.class, false);
                break;
            case R.id.tv_right:
                if (TextUtils.isEmpty(repeatType)) {
                    ToastUtils.showShortToast("请先设置重复模式");
                    return;
                }
                addAlarm();
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public Handler getHandler() {
        return mHandler;
    }

    private void initData() {
        dateList = new ArrayList<>();
        hourList = new ArrayList<>();
        minList = new ArrayList<>();
        dateList.add("上午");
        dateList.add("下午");
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minList.add("0" + i);
            } else {
                minList.add("" + i);
            }
        }
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                hourList.add("0" + i);
            } else {
                hourList.add("" + i);
            }
        }
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == SET_REPEAT_SUCCESS) {
            repeatType = event.getData().toString();
            tv_cycle.setText(repeatType);
        } else if (event.getCode() == ADD_REMIND_REPEAT_SUCCESS) {
            repeatType = event.getData().toString();
            tv_cycle.setText(repeatType);
        }
    }

    public void addAlarm() {
        LoadingDialog.getInstance(this).show();
        int acctType = 0;
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            acctType = 0;
            platform = ELoginPlatform.WX;
        } else {
            acctType = 1;
            platform = ELoginPlatform.QQOpen;
        }
        DeviceManager deviceManager = new DeviceManager();
        deviceManager.productId = BuildConfig.PRODUCT_ID;
        deviceManager.dsn = AuthLive.getInstance()
                .getCurrentPig() == null ? "hdfeng" : AuthLive.getInstance()
                .getCurrentPig().getRobotName();
        UniAccessInfo info = new UniAccessInfo();
        info.domain = "alarm";
        info.intent = "cloud_manager";
        JSONObject obj = new JSONObject();
        try {
            obj.put("eType", 0);
            JSONObject stCloudAlarmReq = new JSONObject();
            JSONObject stAccountBaseInfo = new JSONObject();
            stAccountBaseInfo.put("eAcctType", acctType);
            stAccountBaseInfo.put("strAcctId", AuthLive.getInstance().getCurrentUser().getUserId());
            stCloudAlarmReq.put("stAccountBaseInfo", stAccountBaseInfo);
            stCloudAlarmReq.put("eCloud_type", 1);//0,为查看; 1为添加;2为删除;3为更新
            stCloudAlarmReq.put("sPushInfo", "推什么");
            JSONArray vCloudAlarmData = new JSONArray();
            JSONObject vCloudAlarmData0 = new JSONObject();
            JSONObject stAIDeviceBaseInfo = new JSONObject();
            stAIDeviceBaseInfo.put("strGuid", AuthLive.getInstance()
                    .getCurrentPig() == null ? "hdfeng" : AuthLive.getInstance()
                    .getCurrentPig().getRobotName());
            stAIDeviceBaseInfo.put("strAppKey", BuildConfig.APP_KEY);
            vCloudAlarmData0.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            switch (repeatType) {
                case "永不":
                    vCloudAlarmData0.put("eRepeatType", 1);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                case "每天":
                    vCloudAlarmData0.put("eRepeatType", 2);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                case "每周":
                    vCloudAlarmData0.put("eRepeatType", 3);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                case "每月":
                    vCloudAlarmData0.put("eRepeatType", 4);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                case "每年":
                    vCloudAlarmData0.put("eRepeatType", 1);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                case "工作日":
                    vCloudAlarmData0.put("eRepeatType", 5);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                case "节假日":
                    vCloudAlarmData0.put("eRepeatType", 6);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
                default:
                    vCloudAlarmData0.put("eRepeatType", 1);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                    break;
            }
            vCloudAlarmData0.put("lAlarmId", 0);
            String date = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE);
            int hour = 0;
            if (loopView_date.getSelectedItem() == 1) {
                hour += 12;
            }
            hour += Integer.parseInt(hourList.get(loopView_hour.getSelectedItem()));
            date = date + " " + hour + ":" + minList.get(loopView_minute.getSelectedItem()) + ":00";
            vCloudAlarmData0.put("lStartTimeStamp", TimeUtils.string2Millis(date));
            vCloudAlarmData0.put("vRingId", null);
            vCloudAlarmData.put(vCloudAlarmData0);
            stCloudAlarmReq.put("vCloudAlarmData", vCloudAlarmData);
            obj.put("stCloudAlarmReq", stCloudAlarmReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        info.jsonBlobInfo = obj.toString();
        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
                .requestTskmUniAccess(platform, deviceManager, info, new TVSManager
                        .TVSAlarmListener() {
                    @Override
                    public void onSuccess(CommOpInfo msg) {
                        String str = msg.errMsg;
                        ToastUtils.showShortToast("新建闹钟成功");
                        Event<String> event = new Event<>(SET_ALARM_SUCCESS);
                        EventBusUtil.sendEvent(event);
                        finish();
                    }

                    @Override
                    public void onError(String code) {
                        ToastUtils.showShortToast(code);
                        LogUtils.d("code:" + code);
                    }
                });
    }
}

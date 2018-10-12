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
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.weigan.loopview.LoopView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_REPEAT_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_REPEAT_SUCCESS;

public class AddAlarmActivity extends BaseNewActivity {
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
    private String repeatType = "单次";
    private AlarmModel model;
    Date today = new Date();

    private class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
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
        model = getIntent().getParcelableExtra("item");
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
        if (model != null) {
            if (model.amOrpm.equals("下午")) {
                loopView_date.setCurrentPosition(1);
            }
            String[] str = model.time.split(":");
            int hour = Integer.parseInt(str[0]);
            loopView_hour.setCurrentPosition(hour - 1);
            int minutie = Integer.parseInt(str[1]);
            loopView_minute.setCurrentPosition(minutie);
            switch (model.eRepeatType) {
                case 1:
                    repeatType = "单次";
                    break;
                case 2:
                    repeatType = "每天";
                    break;
                case 3:
                    repeatType = "每周";
                    break;
                case 4:
                    repeatType = "每月";
                    break;
                case 5:
                    repeatType = "工作日";
                    break;
                case 6:
                    repeatType = "节假日";
                    break;
            }
            tv_cycle.setText(repeatType);
        }

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
                if (model == null) {
                    addAlarm(1, 0);
                } else {
                    addAlarm(3, model.lAlarmId);
                }
                break;
        }
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

    public void addAlarm(int eCloud_type, long lAlarmId) {
        LoadingDialog.getInstance(this).show();
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
        }

        String date = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE);
        int hour = 0;
        if (loopView_date.getSelectedItem() == 1) {
            hour += 12;
        }
        hour += Integer.parseInt(hourList.get(loopView_hour.getSelectedItem()));
        date = date + " " + hour + ":" + minList.get(loopView_minute.getSelectedItem()) + ":00";
        long timeMill = TimeUtils.string2Millis(date);
        if (System.currentTimeMillis() > timeMill) {
            switch (repeatType) {
                case "单次":
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
                case "每天":
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
                case "每周":
                    timeMill += 7 * 24 * 60 * 60 * 1000;
                    break;
                case "每月":
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
                case "每年":
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
                case "工作日":
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
                case "节假日":
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
                default:
                    timeMill += 24 * 60 * 60 * 1000;
                    break;
            }
        }
        int eRepeatType = 0;
        switch (repeatType) {
            case "单次":
                eRepeatType = 1;
                break;
            case "每天":
                eRepeatType = 2;
                break;
            case "每周":
                eRepeatType = 3;
                break;
            case "每月":
                eRepeatType = 4;
                break;
            case "每年":
                eRepeatType = 1;
                break;
            case "工作日":
                eRepeatType = 5;
                break;
            case "节假日":
                eRepeatType = 6;
                break;
            default:
                eRepeatType = 1;
                break;
        }
        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
                .requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils
                        .getAlarmUniAccessinfo(eCloud_type, eRepeatType, lAlarmId, timeMill), new TVSManager
                        .TVSAlarmListener() {
                    @Override
                    public void onSuccess(CommOpInfo msg) {
                        LoadingDialog.getInstance(AddAlarmActivity.this).dismiss();
                        if (model == null) {
                            ToastUtils.showShortToast("新建闹钟成功");
                        } else {
                            ToastUtils.showShortToast("更新闹钟成功");
                        }
                        Event<String> event = new Event<>(SET_ALARM_SUCCESS);
                        EventBusUtil.sendEvent(event);
                        finish();
                    }

                    @Override
                    public void onError(String code) {
                        LoadingDialog.getInstance(AddAlarmActivity.this).dismiss();
                        ToastUtils.showShortToast(code);
                        LogUtils.d("code:" + code);
                    }
                });
    }
}

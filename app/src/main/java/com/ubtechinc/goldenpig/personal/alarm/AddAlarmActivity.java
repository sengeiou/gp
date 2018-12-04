package com.ubtechinc.goldenpig.personal.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
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
import com.ubtechinc.goldenpig.model.AlarmModel;
import com.ubtechinc.goldenpig.personal.remind.SetRemindRepeatActivity;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.weigan.loopview.LoopView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_REPEAT_SUCCESS;

public class AddAlarmActivity extends BaseNewActivity {
    @BindView(R.id.loopView_date)
    LoopView loopView_am;
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
    /**
     * 0为单次，1为每周一，2为每周二，3为每周三，4为每周四，5为每周五，6为每周六,7为每周天，8为每天
     */
    private int repeatType = 0;
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
        loopView_am.setItems(dateList);
        loopView_am.setInitPosition(0);
        loopView_am.setTextSize(18);
        loopView_am.setNotLoop();
        loopView_hour.setItems(hourList);
        loopView_hour.setInitPosition(0);
        loopView_hour.setTextSize(18);
        loopView_minute.setItems(minList);
        loopView_minute.setInitPosition(0);
        loopView_minute.setTextSize(18);
        try {
            if (model != null) {
                String[] str = model.time.split(":");
                int hour = Integer.parseInt(str[0]);
                loopView_hour.setCurrentPosition((hour - 1 + 12) % 12);
                int minutie = Integer.parseInt(str[1]);
                loopView_minute.setCurrentPosition(minutie % 60);
                if (hour == 0) {
                    loopView_am.setInitPosition(1);
                } else if (model.amOrpm.equals("下午")) {
                    loopView_am.setCurrentPosition(1);
                }
                switch (model.eRepeatType) {
                    case 1:
                        repeatType = 0;
                        tv_cycle.setText("单次");
                        break;
                    case 2:
                        repeatType = 8;
                        tv_cycle.setText("每天");
                        break;
                    case 3:
                        long lStartTimeStamp = model.lStartTimeStamp * 1000;
                        String week = TimeUtils.getWeek(lStartTimeStamp);
                        break;
                    default:

                        break;
                }
            } else {
                int hour = TimeUtils.getHourFromDate(new Date());
                int minute = TimeUtils.getMinuteFromDate(new Date());
                if (hour == 0) {
                    loopView_am.setInitPosition(1);
                } else if (hour > 12) {
                    loopView_am.setInitPosition(1);
                    hour -= 12;
                }
                loopView_hour.setInitPosition((hour - 1 + 12) % 12);
                loopView_minute.setInitPosition(minute % 60);
            }
        } catch (Exception e) {
        }
    }


    @OnClick({R.id.rl_recount, R.id.tv_left, R.id.tv_right})
    void rl_recount(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.rl_recount:
                Intent it = new Intent(this, SetRepeatActivity.class);
                it.putExtra("repeatType", repeatType);
                startActivity(it);
                //ActivityRoute.toAnotherActivity(this, SetRepeatActivity.class, false);
                break;
            case R.id.tv_right:
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
//            if (i < 10) {
//                hourList.add("0" + i);
//            } else {
//            }
            hourList.add("" + i);
        }
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == SET_REPEAT_SUCCESS) {
            repeatType = (int) event.getData();
            switch (repeatType) {
                case 0:
                    tv_cycle.setText("单次");
                    break;
                case 1:
                    tv_cycle.setText("每周日");
                    break;
                case 2:
                    tv_cycle.setText("每周一");
                    break;
                case 3:
                    tv_cycle.setText("每周二");
                    break;
                case 4:
                    tv_cycle.setText("每周三");
                    break;
                case 5:
                    tv_cycle.setText("每周四");
                    break;
                case 6:
                    tv_cycle.setText("每周五");
                    break;
                case 7:
                    tv_cycle.setText("每周六");
                    break;
                case 8:
                    tv_cycle.setText("每天");
                    break;
                case 9:
                    tv_cycle.setText("每工作日");
                    break;
                default:
                    break;
            }
        }
//        else if (event.getCode() == ADD_REMIND_REPEAT_SUCCESS) {
//            repeatType = event.getData().toString();
//            tv_cycle.setText(repeatType);
//        }
    }

    public void addAlarm(int eCloud_type, long lAlarmId) {
        LoadingDialog.getInstance(this).show();
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
        }

        long timnow = System.currentTimeMillis();
        String date = TimeUtils.getTime(timnow, TimeUtils.DATE_FORMAT_DATE);
        //当前星期几:周日的Index才是1，周六为7
        int week = TimeUtils.getWeekIndex(timnow);
        int hour = Integer.parseInt(hourList.get(loopView_hour.getSelectedItem()));
        if (hour == 12) {
            if (loopView_am.getSelectedItem() == 0) {
                hour = 0;
            }
        } else {
            if (loopView_am.getSelectedItem() == 1) {
                hour += 12;
            }
        }
        // hour += Integer.parseInt(hourList.get(loopView_hour.getSelectedItem()));
        date = date + " " + hour + ":" + minList.get(loopView_minute.getSelectedItem()) + ":00";
        long timeMill = TimeUtils.string2Millis(date);
        int eRepeatType = 0;
        switch (repeatType) {
            case 0://单次
                if (timnow > timeMill) {
                    timeMill += 24 * 60 * 60 * 1000;
                }
                eRepeatType = 1;
                break;
            case 8://每天
                if (timnow > timeMill) {
                    timeMill += 24 * 60 * 60 * 1000;
                }
                eRepeatType = 2;
                break;
            case 9:
                if (timnow > timeMill) {
                    timeMill += 24 * 60 * 60 * 1000;
                }
                eRepeatType = 5;
                break;
            case 1://每周日
            case 2://每周一
            case 3://每周二
            case 4://每周三
            case 5://每周四
            case 6://每周五
            case 7://每周六
                if (repeatType - week == 0) {//同一天
                    if (timnow > timeMill) {
                        timeMill += 7 * 24 * 60 * 60 * 1000;
                    }
                } else {
                    timeMill += ((repeatType - week + 7) % 7) * 24 * 60 * 60 * 1000;
                }
                eRepeatType = 3;
                break;
//            case 2://每周一
//                break;
//            case 3://每周二
//                break;
//            case 4://每周三
//                break;
//            case 5://每周四
//                break;
//            case 6://每周五
//                break;
//            case 7://每周六
//                timeMill += 7 * 24 * 60 * 60 * 1000;
//                eRepeatType = 3;
//                break;
//            default:
//                timeMill += 24 * 60 * 60 * 1000;
//                eRepeatType = 1;
//                break;
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

package com.ubtechinc.goldenpig.personal.remind;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.ubtechinc.goldenpig.model.RemindModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

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

import static com.ubtech.utilcode.utils.TimeUtils.DATE_FORMAT_DATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_REPEAT_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_REPEAT_SUCCESS;

public class AddRemindActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.loopView_date)
    LoopView loopView_date;
    @BindView(R.id.loopView_am)
    LoopView loopView_am;
    @BindView(R.id.loopView_hour)
    LoopView loopView_hour;
    @BindView(R.id.loopView_minute)
    LoopView loopView_minute;
    @BindView(R.id.ed_msg)
    EditText ed_msg;
    @BindView(R.id.tv_cycle)
    TextView tv_cycle;
    @BindView(R.id.tv_time)
    TextView tv_time;
    private List<String> dateList;
    private List<String> dateList2;
    private List<String> amList;
    private List<String> hourList;
    private List<String> minList;
    private String repeatType = "单次";
    Date today = new Date();
    private RemindModel model;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_remind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = getIntent().getParcelableExtra("item");
        initData();
        loopView_date.setItems(dateList);
        loopView_date.setItemsVisibleCount(0);
        loopView_date.setTextSize(18);
        loopView_date.setNotLoop();
        loopView_am.setItems(amList);
        loopView_am.setItemsVisibleCount(0);
        loopView_am.setTextSize(18);
        loopView_am.setNotLoop();
        loopView_hour.setItems(hourList);
        loopView_hour.setItemsVisibleCount(0);
        loopView_hour.setInitPosition(8);
        loopView_hour.setTextSize(18);
        loopView_minute.setItems(minList);
        loopView_minute.setItemsVisibleCount(0);
        loopView_minute.setTextSize(18);
        loopView_minute.setInitPosition(30);
        loopView_date.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        loopView_am.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        loopView_hour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        loopView_minute.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                getMsgTime();
            }
        });
        StringBuffer sb = new StringBuffer();
        Date da = TimeUtils.addDate(today, loopView_date.getSelectedItem());
        int year = TimeUtils.getYearFromDate(da);
        int month = TimeUtils.getMonthFromDate(da);
        int day = TimeUtils.getDayFromDate(da);
        sb.append(year);
        sb.append("年");
        sb.append(month);
        sb.append("月");
        sb.append(day);
        sb.append("日");
        sb.append(amList.get(loopView_am.getSelectedItem()));
        sb.append(hourList.get(8));
        sb.append(":");
        sb.append(minList.get(30));
        tv_time.setText(sb.toString());

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
            ed_msg.setText(TextUtils.isEmpty(model.sNote) ? "" : model.sNote);
        }

    }


    @OnClick({R.id.rl_recount, R.id.tv_left, R.id.tv_right})
    void rl_recount(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.rl_recount:
                ActivityRoute.toAnotherActivity(this, SetRemindRepeatActivity.class, false);
                break;
            case R.id.tv_right:
                String sNote = ed_msg.getText().toString();
                if (TextUtils.isEmpty(sNote)) {
                    ToastUtils.showShortToast("请先填写提醒事项");
                    return;
                }
                if (model == null) {
                    addRemind(sNote, 1, 0);
                } else {
                    addRemind(sNote, 3, model.lReminderId);
                }
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private void initData() {
        dateList = new ArrayList<>();
        dateList2 = new ArrayList<>();
        amList = new ArrayList<>();
        hourList = new ArrayList<>();
        minList = new ArrayList<>();
        amList.add("上午");
        amList.add("下午");
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
        dateList.add("今天");
        dateList2.add(TimeUtils.getTime(System.currentTimeMillis(), DATE_FORMAT_DATE));
        for (int i = 1; i < 7; i++) {
            Date da = TimeUtils.addDate(today, i);
            int month = TimeUtils.getMonthFromDate(da);
            int day = TimeUtils.getDayFromDate(da);
            String sb = month + "月" + day + "日" + TimeUtils.getWeekFromIn(TimeUtils
                    .getWeekFromDate(da));
            dateList.add(sb);
            dateList2.add(TimeUtils.getTime(da.getTime(), DATE_FORMAT_DATE));
        }
    }

    public String getMsgTime() {
        StringBuffer sb = new StringBuffer();
        Date da = TimeUtils.addDate(today, loopView_date.getSelectedItem());
        int year = TimeUtils.getYearFromDate(da);
        int month = TimeUtils.getMonthFromDate(da);
        int day = TimeUtils.getDayFromDate(da);
        sb.append(year);
        sb.append("年");
        sb.append(month);
        sb.append("月");
        sb.append(day);
        sb.append("日");
        sb.append(amList.get(loopView_am.getSelectedItem()));
        sb.append(hourList.get(loopView_hour.getSelectedItem()));
        sb.append(":");
        sb.append(minList.get(loopView_minute.getSelectedItem()));
        tv_time.setText(sb.toString());
        return sb.toString();
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == ADD_REMIND_REPEAT_SUCCESS) {
            repeatType = event.getData().toString();
            tv_cycle.setText(repeatType);
        }
    }

    public void addRemind(String sNote, int eCloud_type, long lReminderId) {
        LoadingDialog.getInstance(this).show();
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
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
                eRepeatType = 0;
                break;
        }
        String ymd = dateList2.get(loopView_date.getSelectedItem());
        int hour = 0;
        if (loopView_am.getSelectedItem() == 1) {
            hour += 12;
        }
        hour += Integer.parseInt(hourList.get(loopView_hour.getSelectedItem()));
        String date = ymd + " " + hour + ":" + minList.get(loopView_minute.getSelectedItem()) + ":00";
        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
                .requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils.getRemindUniAccessinfo
                        (sNote, eCloud_type, eRepeatType, lReminderId, TimeUtils.string2Millis(date)), new TVSManager
                        .TVSAlarmListener() {
                    @Override
                    public void onSuccess(CommOpInfo msg) {
                        if (model == null) {
                            ToastUtils.showShortToast("新建提醒成功");
                        } else {
                            ToastUtils.showShortToast("更新提醒成功");
                        }
                        Event<String> event = new Event<>(ADD_REMIND_SUCCESS);
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

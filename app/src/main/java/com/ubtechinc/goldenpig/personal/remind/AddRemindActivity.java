package com.ubtechinc.goldenpig.personal.remind;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

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

public class AddRemindActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.loopView_date)
    LoopView loopView_date;
    @BindView(R.id.loopView_am)
    LoopView loopView_am;
    @BindView(R.id.loopView_hour)
    LoopView loopView_hour;
    @BindView(R.id.loopView_minute)
    LoopView loopView_minute;
    @BindView(R.id.tv_cycle)
    TextView tv_cycle;
    @BindView(R.id.tv_time)
    TextView tv_time;
    private MyHandler mHandler;
    private List<String> dateList;
    private List<String> amList;
    private List<String> hourList;
    private List<String> minList;
    private String repeatType;
    Date today = new Date();

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
        return R.layout.activity_add_remind;
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
                if (TextUtils.isEmpty(repeatType)) {
                    ToastUtils.showShortToast("请先设置重复模式");
                    return;
                }
                ToastUtils.showShortToast("新建提醒成功");
                Event<String> event = new Event<>(SET_ALARM_SUCCESS);
                EventBusUtil.sendEvent(event);
                finish();
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
        for (int i = 1; i < 7; i++) {
            Date da = TimeUtils.addDate(today, i);
            int month = TimeUtils.getMonthFromDate(da);
            int day = TimeUtils.getDayFromDate(da);
            String sb = month + "月" + day + "日" + TimeUtils.getWeekFromIn(TimeUtils
                    .getWeekFromDate(da));
            dateList.add(sb);
        }
    }

    public String getMsgTime(){
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
}

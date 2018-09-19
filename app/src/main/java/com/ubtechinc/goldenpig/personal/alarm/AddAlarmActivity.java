package com.ubtechinc.goldenpig.personal.alarm;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.weigan.loopview.LoopView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

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
                ActivityRoute.toAnotherActivity(this, SetRepeatActivity.class, false);
                break;
            case R.id.tv_right:
                if (TextUtils.isEmpty(repeatType)) {
                    ToastUtils.showShortToast("请先设置重复模式");
                    return;
                }
                ToastUtils.showShortToast("新建闹钟成功");
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
        }
    }
}

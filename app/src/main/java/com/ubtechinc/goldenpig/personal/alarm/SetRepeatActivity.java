package com.ubtechinc.goldenpig.personal.alarm;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.RepeatModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.view.Divider;
import com.weigan.loopview.LoopView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

public class SetRepeatActivity extends BaseNewActivity {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private BaseQuickAdapter<RepeatModel, BaseViewHolder> adapter;
    private List<RepeatModel> mList;
    /**
     * 0为单次，1为每周一，2为每周二，3为每周三，4为每周四，5为每周五，6为每周六,7为每周天，8为每天
     */
    private int repeatType = 0;

    private class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mWeakReference.get() != null) {
                    ToastUtils.showShortToast(mWeakReference.get().getString(R.string.timeout_error_toast));
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_set_repeat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repeatType = getIntent().getIntExtra("repeatType", 0);
        rl_titlebar.setTitleText("重复");
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mList = new ArrayList<>();
        initData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
                .ubt_wifi_list_divider)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.ubt_1px));
        recycler.addItemDecoration(divider);
        recycler.setAdapter(adapter = new BaseQuickAdapter<RepeatModel, BaseViewHolder>(R.layout
                .adapter_set_repeat, mList) {
            @Override
            protected void convert(BaseViewHolder helper, RepeatModel item) {
                helper.setText(R.id.tv_name, item.name);
                helper.setVisible(R.id.iv, item.select);
                helper.setVisible(R.id.iv, item.repeatType == repeatType);
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Event<Integer> event = new Event<>(EventBusUtil.SET_REPEAT_SUCCESS);
                event.setData(mList.get(position).repeatType);
                EventBusUtil.sendEvent(event);
                finish();
            }
        });
    }

    private void initData() {
        RepeatModel m8 = new RepeatModel();
        m8.repeatType = 0;
        m8.name = "单次";
        mList.add(m8);
        RepeatModel m7 = new RepeatModel();
        m7.repeatType = 8;
        m7.name = "每天";
        mList.add(m7);
        RepeatModel m9 = new RepeatModel();
        m9.repeatType = 9;
        m9.name = "每工作日";
        mList.add(m9);
        RepeatModel m0 = new RepeatModel();
        m0.repeatType = 1;
        m0.name = "每周日";
        mList.add(m0);
        RepeatModel m1 = new RepeatModel();
        m1.repeatType = 2;
        m1.name = "每周一";
        mList.add(m1);
        RepeatModel m2 = new RepeatModel();
        m2.repeatType = 3;
        m2.name = "每周二";
        mList.add(m2);
        RepeatModel m3 = new RepeatModel();
        m3.repeatType = 4;
        m3.name = "每周三";
        mList.add(m3);
        RepeatModel m4 = new RepeatModel();
        m4.repeatType = 5;
        m4.name = "每周四";
        mList.add(m4);
        RepeatModel m5 = new RepeatModel();
        m5.repeatType = 6;
        m5.name = "每周五";
        mList.add(m5);
        RepeatModel m6 = new RepeatModel();
        m6.repeatType = 7;
        m6.name = "每周六";
        mList.add(m6);
    }
}

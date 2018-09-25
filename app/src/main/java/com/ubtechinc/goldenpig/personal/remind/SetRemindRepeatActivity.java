package com.ubtechinc.goldenpig.personal.remind;

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
import com.ubtechinc.goldenpig.view.Divider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

public class SetRemindRepeatActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private BaseQuickAdapter<RepeatModel, BaseViewHolder> adapter;
    private List<RepeatModel> mList;
    private int selestPosition = 0;

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
    protected int getContentViewId() {
        return R.layout.activity_set_repeat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_titlebar.setTitleText("重复");
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_titlebar.setTvRightName("确定");
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Event<String> event = new Event<>(EventBusUtil.ADD_REMIND_REPEAT_SUCCESS);
                    event.setData(mList.get(selestPosition).name);
                    EventBusUtil.sendEvent(event);
                    finish();
                } catch (Exception e) {
                    ToastUtils.showShortToast("请先选择重复日期");
                }

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
                helper.setVisible(R.id.iv, selestPosition == helper.getAdapterPosition());
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selestPosition = position;
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void update(Observable o, Object arg) {

    }


    private void initData() {
        RepeatModel m0 = new RepeatModel();
        m0.name = "永不";
        mList.add(m0);
        RepeatModel m1 = new RepeatModel();
        m1.name = "每天";
        mList.add(m1);
        RepeatModel m2 = new RepeatModel();
        m2.name = "每周";
        mList.add(m2);
        RepeatModel m3 = new RepeatModel();
        m3.name = "每月";
        mList.add(m3);
        RepeatModel m4 = new RepeatModel();
        m4.name = "每年";
        mList.add(m4);
    }
}

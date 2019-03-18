package com.ubtechinc.goldenpig.pigmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserRecords;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;
import com.ubtechinc.goldenpig.utils.CommendUtil;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

import static com.ubtech.utilcode.utils.TimeUtils.ONE_DAY;
import static com.ubtech.utilcode.utils.TimeUtils.ONE_HOUR;
import static com.ubtech.utilcode.utils.TimeUtils.ONE_MINUTE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DELETE_RECORD_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.EDIT_RECORD_CALLBACK;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.INVISE_RECORD_POINT;
import static com.ubtechinc.goldenpig.utils.CommendUtil.TIMEOUT;

public class RecordActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    RecordAdapter adapter;
    private ArrayList<RecordModel> mList;
    public int deletePosition = -1;
    /**
     *
     */
    private Boolean hasLoadMsg = false;

    private MyHandler mHandler;

    private ArrayList<RecordModel> allList = new ArrayList<>();

    private class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                try {
                    if (mWeakReference.get() != null) {
                        UbtToastUtils.showCustomToast(UBTPGApplication.getContext(), mWeakReference.get().getString(R
                                .string
                                .timeout_error_toast));
                        LoadingDialog.getInstance(mWeakReference.get()).dismiss();
                        if (mList.size() == 0) {
                            mStateView.showRetry();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_record;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
        initStateView(true);
        mStateView.setEmptyResource(R.layout.adapter_record_empty);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mHandler.hasMessages(1)) {
                    mHandler.removeMessages(1);
                }
                mHandler.sendEmptyMessageDelayed(1, TIMEOUT * 1000);// 20s 秒后检查加载框是否还在
                UbtTIMManager.getInstance().queryRecord();
            }
        });
        rl_titlebar.setTitleText(getString(R.string.ubt_recent_calls));
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_titlebar.setTvRightName("编辑");
        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                .ubt_tab_btn_txt_checked_color));
        rl_titlebar.getTvRight().setVisibility(View.GONE);
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() > 0) {
                    Intent it = new Intent(RecordActivity.this, EditRecordActivity.class);
                    it.putParcelableArrayListExtra("list", mList);
                    it.putParcelableArrayListExtra("allList", allList);
                    startActivity(it);
                } else {
                    ToastUtils.showShortToast(getString(R.string.record_empty_prompt));
                }
            }
        });
        mList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
                .ubt_wifi_list_divider)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.ubt_1px));
        recycler.addItemDecoration(divider);
        recycler.setSwipeMenuCreator(swipeMenuCreator);
        recycler.setSwipeMenuItemClickListener(mMenuItemClickListener);
        adapter = new RecordAdapter(this, mList);
        recycler.setAdapter(adapter);
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                try {
                    LoadingDialog.getInstance(RecordActivity.this).dismiss();
                    if (NetworkHelper.sharedHelper() == null) {
                        ToastUtils.showShortToast(getString(R.string.network_error));
                    } else if (NetworkHelper.sharedHelper().isNetworkAvailable()) {
                        ToastUtils.showShortToast(getString(R.string.msg_error_toast));
                    } else {
                        ToastUtils.showShortToast(getString(R.string.network_error));
                    }
                    if (mHandler.hasMessages(1)) {
                        mHandler.removeMessages(1);
                    }
                    if (mList.size() == 0) {
                        mStateView.showRetry();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver", "sss");
            }
        });
        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
        rl_titlebar.getTvRight().setEnabled(false);
        onRefresh();
    }

    public void onRefresh() {
        mStateView.showLoading();
        //LoadingDialog.getInstance(this).setTimeout(TIMEOUT).setShowToast(true).show();
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessageDelayed(1, TIMEOUT * 1000);// 20s 秒后检查加载框是否还在
        UbtTIMManager.getInstance().queryRecord();
    }

    //    public void onError(String str) {
//        hasLoadMsg = false;
//        ToastUtils.showShortToast(str);
//        if (mList.size() == 0) {
//            mStateView.showRetry();
//        } else {
//            mStateView.showContent();
//        }
//    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (viewType == 1) {
                return;
            }
            int width = getResources().getDimensionPixelSize(R.dimen.dp_65);
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(RecordActivity.this)
                        .setBackgroundColor(getResources().getColor(R.color
                                .ubt_dialog_btn_txt_color))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setTextSize(16)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {
                    List<UserRecords.Record> list = new ArrayList();
                    int count = mList.get(adapterPosition).ids.size();
                    for (int i = 0; i < count; i++) {
                        for (int j = 0; j < allList.size(); j++) {
                            if (mList.get(adapterPosition).ids.get(i).equals(allList.get(j).id)) {
                                UserRecords.Record.Builder recordBuild = UserRecords.Record.newBuilder();
                                recordBuild.setName(allList.get(j).name);
                                recordBuild.setNumber(allList.get(j).number);
                                recordBuild.setDateLong(allList.get(j).dateLong);
                                recordBuild.setDuration(allList.get(j).duration);
                                recordBuild.setType(allList.get(j).type);
                                recordBuild.setId(allList.get(j).id);
                                list.add(recordBuild.build());
                                break;
                            }
                        }
                    }
                    UbtLogger.d("RecordActivity", "delete the list");
                    UbtTIMManager.getInstance().deleteRecord(list);
                    deletePosition = adapterPosition;
                    LoadingDialog.getInstance(RecordActivity.this).setTimeout(TIMEOUT)
                            .setShowToast(true).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            TIMMessage msg = (TIMMessage) arg;
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
                try {
                    dealMsg(elem.getData());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    if (mHandler.hasMessages(1)) {
                        mHandler.removeMessages(1);
                    }
                    ToastUtils.showShortToast(getString(R.string.msg_error_toast));
                    LoadingDialog.getInstance(RecordActivity.this).dismiss();
                }
            }
        } catch (Exception e) {
        }
    }

    /* <call path="/im/mail/add"/>
    <call path="/im/mail/query"/>
    <call path="/im/mail/delete"/>
    <call path="/im/mail/update"/>*/
    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        switch (action) {
            case "/im/record/query":
                List<UserRecords.Record> list = msg.getPayload().unpack(UserRecords.UserRecord
                        .class).getRecordList();
                List<RecordModel> ss = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    RecordModel mo = new RecordModel();
                    mo.name = list.get(j).getName();
                    mo.number = list.get(j).getNumber();
                    mo.id = list.get(j).getId();
                    if (list.get(j).getType() == 4) {
                        mo.type = list.get(j).getType();
                    } else if (list.get(j).getType() == 5) {
                        mo.type = 2;
                    } else {
                        mo.type = list.get(j).getType();
                    }
                    mo.dateLong = list.get(j).getDateLong() * 1000;
                    mo.duration = list.get(j).getDuration();
                    ss.add(mo);
                }
                allList.clear();
                allList.addAll(ss);
                onRefreshSuccess(CommendUtil.checkRecord(ss));
                break;
            case "/im/record/delete":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                LoadingDialog.getInstance(RecordActivity.this).dismiss();
                if (flag) {
                    try {
                        mList.remove(deletePosition);
//                        if (mList.get(mList.size() - 1).type == 1) {
//                            mList.remove(mList.size() - 1);
//                        }
                    } catch (Exception e) {
                    }
                    if (mList.size() == 0) {
                        mStateView.showEmpty();
                        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                        rl_titlebar.getTvRight().setEnabled(false);
                        rl_titlebar.getTvRight().setVisibility(View.GONE);
                    } else {
                        mStateView.showContent();
                        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                                .ubt_tab_btn_txt_checked_color));
                        rl_titlebar.getTvRight().setEnabled(true);
                        rl_titlebar.getTvRight().setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShortToast("删除失败，请重试");
                }
                break;
        }
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == DELETE_RECORD_SUCCESS) {
            onRefresh();
        } else if (event.getCode() == EDIT_RECORD_CALLBACK) {
            List<RecordModel> li = (List<RecordModel>) event.getData();
            if (li != null) {
                mList.clear();
                mList.addAll(li);
                adapter.notifyDataSetChanged();
                if (mList.size() == 0) {
                    mStateView.showEmpty();
                    rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                    rl_titlebar.getTvRight().setEnabled(false);
                    rl_titlebar.getTvRight().setVisibility(View.GONE);
                } else {
                    mStateView.showContent();
                    rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                            .ubt_tab_btn_txt_checked_color));
                    rl_titlebar.getTvRight().setEnabled(true);
                    rl_titlebar.getTvRight().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void onRefreshSuccess(List<RecordModel> list) {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        Event<String> event = new Event<>(INVISE_RECORD_POINT);
        EventBusUtil.sendEvent(event);
        LoadingDialog.getInstance(this).dismiss();
        hasLoadMsg = true;
        mList.clear();
        mList.addAll(list);
        if (mList.size() == 0) {
            mStateView.showEmpty();
            rl_titlebar.getTvRight().setVisibility(View.GONE);
            rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
            rl_titlebar.getTvRight().setEnabled(false);
            rl_titlebar.getTvRight().setVisibility(View.GONE);
        } else {
            mStateView.showContent();
            rl_titlebar.getTvRight().setVisibility(View.VISIBLE);
            rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
            rl_titlebar.getTvRight().setEnabled(true);
            rl_titlebar.getTvRight().setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    public void aa() {
        List<RecordModel> ss = new ArrayList<>();
        List<Long> s = new ArrayList<>();
        long t = System.currentTimeMillis();
        s.add(t - ONE_MINUTE * 12);
        s.add(t - ONE_HOUR * 2);
        s.add(t - ONE_DAY * 12);
        s.add(t - ONE_DAY * 1 - ONE_HOUR * 12);
        s.add(t - ONE_DAY * 1 - ONE_HOUR * 2);

        s.add(t - ONE_DAY * 2);
        s.add(t - ONE_DAY * 3);
        s.add(t - ONE_DAY * 4);
        s.add(t - ONE_DAY * 5);
        s.add(t - ONE_DAY * 6);

        s.add(t - ONE_DAY * 7);
        s.add(t - ONE_DAY * 8);
        s.add(t - ONE_HOUR * 12);
        s.add(t - ONE_MINUTE * 12);
        s.add(t - ONE_HOUR * 2);

        s.add(t - ONE_DAY * 12);
        for (int j = 0; j < 16; j++) {
            RecordModel mo = new RecordModel();
            mo.name = "油瓶" + j;
            mo.number = "1598765432" + j;
            mo.id = 23;
            mo.type = 2;
            mo.dateLong = s.get(j);
            mo.duration = 2000;
            ss.add(mo);
        }
        allList.clear();
        allList.addAll(ss);
        onRefreshSuccess(CommendUtil.checkRecord(ss));
    }
}

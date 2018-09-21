package com.ubtechinc.goldenpig.pigmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.InvalidProtocolBufferException;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserContacts;
import com.ubt.improtolib.UserRecords;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;
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

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DELETE_RECORD_SUCCESS;

public class RecordActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    RecordAdapter adapter;
    private ArrayList<RecordModel> mList;
    public int deletePosition = 0;
    /**
     *
     */
    private Boolean hasLoadMsg = false;

    private MyHandler mHandler;

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
                    LoadingDialog.getInstance(mWeakReference.get()).dismiss();
                    if (mList.size() == 0) {
                        mStateView.showRetry();
                    }
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
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {

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
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() > 0) {
                    Intent it = new Intent(RecordActivity.this, EditRecordActivity.class);
                    it.putParcelableArrayListExtra("list", mList);
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
        } else {
            UbtTIMManager.getInstance().setPigAccount("2cb9b9a3");
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                LoadingDialog.getInstance(RecordActivity.this).dismiss();
                ToastUtils.showShortToast(s);
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver", "sss");
            }
        });
        onRefresh();
    }

    public void onRefresh() {
        LoadingDialog.getInstance(this).setTimeout(20).setShowToast(true).show();
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);// 20s 秒后检查加载框是否还在
        UbtTIMManager.getInstance().queryRecord();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                List<RecordModel> ss = new ArrayList<>();
//                for (int j = 0; j < 10; j++) {
//                    RecordModel mo = new RecordModel();
//                    mo.name = "测试" + j;
//                    mo.number = "15911234567";
//                    mo.id = j;
//                    mo.type = j % 4;
//                    mo.dateLong = System.currentTimeMillis() - 1000 * j * 60;
//                    mo.duration = j % 5;
//                    ss.add(mo);
//                }
//                onRefreshSuccess(ss);
//            }
//        }, 2000);
    }

    public void onError(String str) {
        hasLoadMsg = false;
        ToastUtils.showShortToast(str);
        if (mList.size() == 0) {
            mStateView.showRetry();
        } else {
            mStateView.showContent();
        }
    }

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
                    UserRecords.Record.Builder recordBuild =  UserRecords.Record.newBuilder();
                    recordBuild.setName(mList.get(adapterPosition).name);
                    recordBuild.setNumber(mList.get(adapterPosition).number);
                    recordBuild.setDateLong(mList.get(adapterPosition).dateLong);
                    recordBuild.setDuration(mList.get(adapterPosition).duration);
                    recordBuild.setType(mList.get(adapterPosition).type);
                    recordBuild.setId(mList.get(adapterPosition).id);
                    List<UserRecords.Record> list = new ArrayList();
                    list.add(recordBuild.build());
                    UbtTIMManager.getInstance().deleteRecord(list);
                    deletePosition = adapterPosition;
                    LoadingDialog.getInstance(RecordActivity.this).setTimeout(20)
                            .setShowToast(true).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        TIMMessage msg = (TIMMessage) arg;
        for (int i = 0; i < msg.getElementCount(); ++i) {
            TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
            try {
                dealMsg(elem.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                ToastUtils.showShortToast("数据异常，请重试");
                LoadingDialog.getInstance(RecordActivity.this).dismiss();
            }
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
                    mo.type = list.get(j).getType();
                    mo.dateLong = list.get(j).getDateLong() * 1000;
                    mo.duration = list.get(j).getDuration();
                    ss.add(mo);
                }
                onRefreshSuccess(ss);
                break;
            case "/im/record/delete":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                LoadingDialog.getInstance(RecordActivity.this).dismiss();
                if (flag) {
                    mList.remove(deletePosition);
                    try {
                        if (mList.get(mList.size() - 1).type == 1) {
                            mList.remove(mList.size() - 1);
                        }
                    } catch (Exception e) {
                    }
                    if (mList.size() == 0) {
                        mStateView.showEmpty();
                        mStateView.setEmptyViewMSG("无最近通话");
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
        }
    }

    public void onRefreshSuccess(List<RecordModel> list) {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        LoadingDialog.getInstance(this).dismiss();
        hasLoadMsg = true;
        mList.clear();
        mList.addAll(list);
        if (mList.size() == 0) {
            mStateView.showEmpty();
            mStateView.setEmptyViewMSG("无最近通话");
        } else {
            mStateView.showContent();
        }
        adapter.notifyDataSetChanged();
    }

}

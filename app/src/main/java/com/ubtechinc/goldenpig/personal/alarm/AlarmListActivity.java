package com.ubtechinc.goldenpig.personal.alarm;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.ai.tvs.business.UniAccessInfo;
import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.DeviceManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewImg;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AlarmModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;

public class AlarmListActivity extends BaseNewActivity implements OnRefreshListener, Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewImg rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    AlarmListAdapter adapter;
    private ArrayList<AlarmModel> mList;
    public int deletePosition = 0;

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
                    ((AlarmListActivity) mWeakReference.get()).refreshLayout.finishRefresh(true);
                }
            }
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_alarm_list;
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
                refreshLayout.autoRefresh();
            }
        });
        rl_titlebar.setTitleText(getString(R.string.alarm));
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_titlebar.setIvRight(R.drawable.ic_add);
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityRoute.toAnotherActivity(AlarmListActivity.this, AddAlarmActivity
                        .class, false);
            }
        });
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(this);
        mList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setCustomBackgroundSize(getResources().getDimensionPixelSize(R.dimen.dp_115));
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
                .ubt_main_bg_color)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.dp_11));
        recycler.addItemDecoration(divider);
        recycler.setSwipeMenuCreator(swipeMenuCreator);
        recycler.setSwipeMenuItemClickListener(mMenuItemClickListener);
        adapter = new AlarmListAdapter(this, mList);
        recycler.setAdapter(adapter);
        refreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);// 20s 秒后检查加载框是否还在
        //UbtTIMManager.getInstance().queryUser();
        getAlarmMsg();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<AlarmModel> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    AlarmModel m = new AlarmModel();
                    m.time_state = "下午";
                    m.time = "19:00";
                    m.date = "单次闹钟";
                    list.add(m);
                }
                onRefreshSuccess(list);
            }
        }, 2000);
    }

    public void onRefreshSuccess(List<AlarmModel> list) {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        refreshLayout.finishRefresh(true);
        mList.clear();
        mList.addAll(list);
        if (mList.size() == 0) {
            mStateView.showEmpty();
        } else {
            mStateView.showContent();
        }
        adapter.notifyDataSetChanged();
    }


    public void onError(String str) {
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
            int width = getResources().getDimensionPixelSize(R.dimen.dp_80);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(AlarmListActivity.this)
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
                    ToastUtils.showShortToast("删除:" + adapterPosition);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    public void update(Observable o, Object arg) {
//        TIMMessage msg = (TIMMessage) arg;
//        for (int i = 0; i < msg.getElementCount(); ++i) {
//            TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
//            try {
//                dealMsg(elem.getData());
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//                ToastUtils.showShortToast("数据异常，请重试");
//                refreshLayout.finishRefresh(true);
//            }
//        }
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == SET_ALARM_SUCCESS) {
            refreshLayout.autoRefresh();
        }
    }

    public void getAlarmMsg() {
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
        deviceManager.dsn = TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig
                .APP_ID_QQ).getDsn();
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
            stCloudAlarmReq.put("eCloud_type", 0);//0,为查看; 1为添加;2为删除;3为更新
            stCloudAlarmReq.put("sPushInfo", "推什么");
            JSONArray vCloudAlarmData = new JSONArray();
            JSONObject vCloudAlarmData0 = new JSONObject();
            JSONObject stAIDeviceBaseInfo = new JSONObject();
            stAIDeviceBaseInfo.put("strGuid", "hhh");//AuthLive.getInstance().getCurrentPig()
            // .getRobotName()
            stAIDeviceBaseInfo.put("strAppKey", BuildConfig.APP_KEY);
            vCloudAlarmData0.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            vCloudAlarmData0.put("eRepeatType", 1);
            vCloudAlarmData0.put("lAlarmId", 0);
            vCloudAlarmData0.put("lStartTimeStamp", 153606960011l);
            vCloudAlarmData0.put("vRingId", new String[]{"aa.bb$111", "aa.bb$112"});
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
                        LogUtils.d("hdf", "getAlarmMsg-onSuccess");
                    }

                    @Override
                    public void onError(String code) {
                        ToastUtils.showShortToast(code);
                        LogUtils.d("code:" + code);
                    }
                });
//        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
//                .requestTskmUniAccess(acctType, BuildConfig.PRODUCT_ID, AuthLive.getInstance()
//                        .getCurrentUser().getUserId(), "hhh", BuildConfig.APP_KEY, new TVSManager
//                        .TVSAlarmListener() {//AuthLive.getInstance().getCurrentPig()
// .getRobotName()
//                    @Override
//                    public void onSuccess(CommOpInfo msg) {
//                        LogUtils.d("hdf", "getAlarmMsg-onSuccess");
//                    }
//
//                    @Override
//                    public void onError(String code) {
//                        ToastUtils.showShortToast(code);
//                        LogUtils.d("code:" + code);
//                    }
//                });
    }
}

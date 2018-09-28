package com.ubtechinc.goldenpig.personal.alarm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.ai.tvs.business.UniAccessInfo;
import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.DeviceManager;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewImg;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AlarmModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
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

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;

public class AlarmListActivity extends BaseNewActivity implements SwipeItemClickListener {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewImg rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    AlarmListAdapter adapter;
    private ArrayList<AlarmModel> mList;

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
        mStateView.setEmptyResource(R.layout.adapter_alarm_empty);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                onRefresh();
            }
        });
        mStateView.setOnEmptyClickListener(new StateView.OnEmptyClickListener() {
            @Override
            public void onEmptyClick() {
                onRefresh();
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
        recycler.setSwipeItemClickListener(this);
        adapter = new AlarmListAdapter(this, mList);
        recycler.setAdapter(adapter);
        onRefresh();
    }


    public void onRefresh() {
        LoadingDialog.getInstance(this).show();
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
//        deviceManager.dsn = AuthLive.getInstance().getCurrentPig() == null ? "hdfeng" : AuthLive
//                .getInstance()
//                .getCurrentPig().getRobotName();
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
            stAIDeviceBaseInfo.put("strGuid", AuthLive.getInstance()
                    .getCurrentPig() == null ? "hdfeng" : AuthLive.getInstance()
                    .getCurrentPig().getRobotName());
            stAIDeviceBaseInfo.put("strAppKey", BuildConfig.APP_KEY);
            vCloudAlarmData0.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            vCloudAlarmData0.put("eRepeatType", 1);//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
            vCloudAlarmData0.put("lAlarmId", 0);
            vCloudAlarmData0.put("lStartTimeStamp", 0);
            vCloudAlarmData0.put("vRingId", null);
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
                        String str = msg.errMsg;
                        List<AlarmModel> list = new ArrayList<>();
                        try {
                            JSONObject obj = new JSONObject(str);
                            JSONArray narry = obj.getJSONArray("vCloudAlarmData");
                            if (narry == null || narry.length() == 0) {
                                onRefreshSuccess(list);
                                return;
                            }
                            for (int i = 0; i < narry.length(); i++) {
                                AlarmModel model = new AlarmModel();
                                JSONObject ob = narry.getJSONObject(i);
                                model.eRepeatType = ob.getInt("eRepeatType");
                                switch (model.eRepeatType)
                                {//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
                                    case 0:
                                    case 1:
                                        model.repeatName = "单次闹钟";
                                        break;
                                    case 2:
                                        model.repeatName = "每天";
                                        break;
                                    case 3:
                                        model.repeatName = "每周";
                                        break;
                                    case 4:
                                        model.repeatName = "每月";
                                        break;
                                    case 5:
                                        model.repeatName = "工作日";
                                        break;
                                    case 6:
                                        model.repeatName = "节假日";
                                        break;
                                }
                                model.lAlarmId = ob.getString("lAlarmId");
                                model.lStartTimeStamp = ob.getLong("lStartTimeStamp");
                                try {
                                    String time = TimeUtils.getTime(model.lStartTimeStamp, TimeUtils
                                            .DATE_FORMAT_ONLY_TIME);
                                    String[] times = time.split(":");
                                    int hour = Integer.parseInt(times[0]);
                                    if (hour >= 19) {
                                        model.amOrpm = "晚上";
                                        model.time = (hour - 12) + ":" + times[1];
                                    } else if (hour >= 13) {
                                        model.amOrpm = "下午";
                                        model.time = (hour - 12) + ":" + times[1];
                                    } else {
                                        model.amOrpm = "上午";
                                        model.time = time;
                                    }
                                } catch (Exception e) {
                                }
                                list.add(model);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onRefreshSuccess(list);
                    }

                    @Override
                    public void onError(String code) {
                        ToastUtils.showShortToast(code);
                        LoadingDialog.getInstance(AlarmListActivity.this).dismiss();
                        LogUtils.d("code:" + code);
                    }
                });
    }

    public void onRefreshSuccess(List<AlarmModel> list) {
        LoadingDialog.getInstance(this).dismiss();
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
                    deleteAlarm(adapterPosition);
                }
            }
        }
    };

    @Override
    public void onItemClick(View itemView, int position) {
        Intent it = new Intent(AlarmListActivity.this, AddAlarmActivity.class);
        it.putExtra("item", mList.get(position));
        startActivity(it);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == SET_ALARM_SUCCESS) {
            onRefresh();
        }
    }

    public void deleteAlarm(int position) {
        AlarmModel model = mList.get(position);
        LoadingDialog.getInstance(this).show();
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
//        deviceManager.dsn = AuthLive.getInstance()
//                .getCurrentPig() == null ? "hdfeng" : AuthLive.getInstance()
//                .getCurrentPig().getRobotName();
        UniAccessInfo info = new UniAccessInfo();
        info.domain = "alarm";
        info.intent = "cloud_manager";
        JSONObject obj = new JSONObject();
        try {
            obj.put("eType", 1);
            JSONObject stCloudAlarmReq = new JSONObject();
            JSONObject stAccountBaseInfo = new JSONObject();
            stAccountBaseInfo.put("eAcctType", acctType);
            stAccountBaseInfo.put("strAcctId", AuthLive.getInstance().getCurrentUser().getUserId());
            stCloudAlarmReq.put("stAccountBaseInfo", stAccountBaseInfo);
            stCloudAlarmReq.put("eCloud_type", 2);//0,为查看; 1为添加;2为删除;3为更新
            stCloudAlarmReq.put("sPushInfo", "");
            JSONArray vCloudAlarmData = new JSONArray();
            JSONObject vCloudAlarmData0 = new JSONObject();
            JSONObject stAIDeviceBaseInfo = new JSONObject();
            stAIDeviceBaseInfo.put("strGuid", AuthLive.getInstance()
                    .getCurrentPig() == null ? "hdfeng" : AuthLive.getInstance()
                    .getCurrentPig().getRobotName());
            stAIDeviceBaseInfo.put("strAppKey", BuildConfig.APP_KEY);
            vCloudAlarmData0.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            vCloudAlarmData0.put("eRepeatType", model.eRepeatType);
            //0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
            vCloudAlarmData0.put("lAlarmId", model.lAlarmId);
            vCloudAlarmData0.put("lStartTimeStamp", model.lStartTimeStamp);
            vCloudAlarmData0.put("vRingId", null);
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
                        LoadingDialog.getInstance(AlarmListActivity.this).dismiss();
                        ToastUtils.showShortToast("删除成功");
                        String str = msg.errMsg;
                        mList.remove(position);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String code) {
                        LoadingDialog.getInstance(AlarmListActivity.this).dismiss();
                        ToastUtils.showShortToast(code);
                        LogUtils.d("code:" + code);
                    }
                });
    }
}

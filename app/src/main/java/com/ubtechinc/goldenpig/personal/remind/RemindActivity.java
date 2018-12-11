package com.ubtechinc.goldenpig.personal.remind;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
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
import com.ubtechinc.goldenpig.model.RemindModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PigUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_REMIND_SUCCESS;

public class RemindActivity extends BaseNewActivity implements SwipeItemClickListener {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewImg rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    RemindAdapter adapter;
    private ArrayList<RemindModel> mList;
    Date today = new Date();
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
                if (mWeakReference.get() != null) {
                    ToastUtils.showShortToast(mWeakReference.get().getString(R.string.timeout_error_toast));
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
        return R.layout.activity_remind;
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
                onRefresh();
//                if (AuthLive.getInstance().getCurrentPig() == null) {
//                    ToastUtils.showShortToast("请先绑定八戒");
//                    finish();
//                } else if (!TextUtils.isEmpty(AuthLive.getInstance().getCurrentPig().getGuid())) {
//                    onRefresh();
//                } else {
//                    getGUID();
//                }
            }
        });
        mStateView.setEmptyResource(R.layout.adapter_remind_empty);
        mStateView.setOnEmptyClickListener(new StateView.OnEmptyClickListener() {
            @Override
            public void onEmptyClick() {
                ActivityRoute.toAnotherActivity(RemindActivity.this, AddRemindActivity
                        .class, false);
            }
        });
        rl_titlebar.setTitleText("提醒事项");
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_titlebar.setIvRight(R.drawable.ic_add);
        rl_titlebar.getIvRight().setVisibility(View.GONE);
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityRoute.toAnotherActivity(RemindActivity.this, AddRemindActivity
                        .class, false);
            }
        });
        mList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setCustomBackgroundSize(getResources().getDimensionPixelSize(R.dimen.dp_94) + 1);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
                .ubt_main_bg_color)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.dp_10));
        recycler.addItemDecoration(divider);
        recycler.setSwipeMenuCreator(swipeMenuCreator);
        recycler.setSwipeItemClickListener(this);
        recycler.setSwipeMenuItemClickListener(mMenuItemClickListener);
        adapter = new RemindAdapter(this, mList);
        recycler.setAdapter(adapter);
        LoadingDialog.getInstance(this).show();
//        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
//        if (pigInfo != null) {
//            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
//        } else {
//            UbtTIMManager.getInstance().setPigAccount("2cb9b9a3");
//        }
//        UbtTIMManager.getInstance().setMsgObserve(this);
//        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
//            @Override
//            public void onError(int i, String s) {
//                Log.e("setOnUbtTIMConver", s);
//                LoadingDialog.getInstance(RemindActivity.this).dismiss();
//                ToastUtils.showShortToast(s);
//            }
//
//            @Override
//            public void onSuccess() {
//                Log.e("setOnUbtTIMConver", "sss");
//            }
//        });
//        if (AuthLive.getInstance().getCurrentPig() == null) {
//            ToastUtils.showShortToast("请先绑定八戒");
//            finish();
//        } else if (!TextUtils.isEmpty(AuthLive.getInstance().getCurrentPig().getGuid())) {
//            onRefresh();
//        } else {
//            getGUID();
//        }
        onRefresh();
    }

    public void onRefresh() {
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
        }
        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
                .requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils.getRemindUniAccessinfo
                        ("", 0, 1, 0, 0), new TVSManager.TVSAlarmListener() {
                    @Override
                    public void onSuccess(CommOpInfo msg) {
                        String str = msg.errMsg;
                        List<RemindModel> list = new ArrayList<>();
                        try {
                            JSONObject obj = new JSONObject(str);
                            JSONArray narry = obj.getJSONArray("vCloudReminderData");
                            if (narry == null || narry.length() == 0) {
                                onRefreshSuccess(list);
                                return;
                            }
                            for (int i = 0; i < narry.length(); i++) {
                                RemindModel model = new RemindModel();
                                JSONObject ob = narry.getJSONObject(i);
                                model.eRepeatType = ob.getInt("eRepeatType");
                                switch (model.eRepeatType) {//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
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
                                model.lReminderId = ob.getLong("lReminderId");
                                model.sNote = ob.getString("sNote");
                                model.lStartTimeStamp = ob.getLong("lStartTimeStamp") * 1000;
                                try {
//                                    String time = null;
//                                    String le = System.currentTimeMillis() + "";
//                                    long mins = 0;
//                                    if (le.length() - (model.lStartTimeStamp + "").length() >= 3) {
//                                        mins = model.lStartTimeStamp * 1000;
//                                    } else {
//                                        mins = model.lStartTimeStamp;
//                                    }
                                    String time = TimeUtils.getTime(model.lStartTimeStamp, TimeUtils
                                            .DATE_FORMAT_ONLY_TIME_12);
                                    Calendar mCalendar = Calendar.getInstance();
                                    mCalendar.setTimeInMillis(model.lStartTimeStamp);
                                    if (mCalendar.get(Calendar.AM_PM) == 0) {//apm=0 表示上午，apm=1表示下午。
                                        model.amOrpm = "上午";
                                    } else {
                                        model.amOrpm = "下午";
                                    }
                                    model.time = time;
                                    model.date = changeTime(model.eRepeatType, model.lStartTimeStamp);
//                                    String time = TimeUtils.getTime(model.lStartTimeStamp, TimeUtils
//                                            .DATE_FORMAT_MON_TIME);
//                                    String[] times = time.split("-");
//                                    model.date = changeTime(model.eRepeatType, model.lStartTimeStamp);
//                                    int hour = Integer.parseInt(times[2]);
//                                    if (hour == 12) {
//                                        model.amOrpm = "下午";
//                                        model.time = "12:" + times[3];
//                                    } else if (hour == 0) {
//                                        model.amOrpm = "上午";
//                                        model.time = "12:" + times[3];
//                                    } else if (hour >= 13) {
//                                        model.amOrpm = "下午";
//                                        model.time = (hour - 12) + ":" + times[3];
//                                    } else {
//                                        model.amOrpm = "上午";
//                                        model.time = hour + ":" + times[3];
//                                    }
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
                        LoadingDialog.getInstance(RemindActivity.this).dismiss();
                        LogUtils.d("code:" + code);
                        if (code.contains("没有")) {
                            mStateView.showEmpty();
                        } else if (mList.size() == 0) {
                            mStateView.showRetry();
                            ToastUtils.showShortToast(code);
                        } else {
                            mStateView.showContent();
                            ToastUtils.showShortToast(code);
                        }
                    }
                });
    }

    public void onRefreshSuccess(List<RemindModel> list) {
        mList.clear();
        mList.addAll(list);
        if (mList.size() == 0) {
            mStateView.showEmpty();
            rl_titlebar.getIvRight().setVisibility(View.GONE);
        } else {
            rl_titlebar.getIvRight().setVisibility(View.VISIBLE);
            mStateView.showContent();
        }
        adapter.notifyDataSetChanged();
        LoadingDialog.getInstance(RemindActivity.this).dismiss();
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
                SwipeMenuItem deleteItem = new SwipeMenuItem(RemindActivity.this)
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == ADD_REMIND_SUCCESS) {
            LoadingDialog.getInstance(this).show();
            onRefresh();
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {
//        Intent it = new Intent(RemindActivity.this, AddRemindActivity.class);
//        it.putExtra("item", mList.get(position));
//        startActivity(it);
    }

    public void deleteAlarm(int position) {
        RemindModel model = mList.get(position);
        LoadingDialog.getInstance(this).show();
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
        }
        TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ)
                .requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils.getRemindUniAccessinfo
                        (model.sNote, 2, model.eRepeatType, model.lReminderId, model.lStartTimeStamp), new TVSManager
                        .TVSAlarmListener() {
                    @Override
                    public void onSuccess(CommOpInfo msg) {
                        LoadingDialog.getInstance(RemindActivity.this).dismiss();
                        ToastUtils.showShortToast("删除成功");
                        mList.remove(position);
                        adapter.notifyDataSetChanged();
                        if (mList.size() == 0) {
                            mStateView.showEmpty();
                            rl_titlebar.getIvRight().setVisibility(View.GONE);
                        } else {
                            rl_titlebar.getIvRight().setVisibility(View.VISIBLE);
                            mStateView.showContent();
                        }
                    }

                    @Override
                    public void onError(String code) {
                        LoadingDialog.getInstance(RemindActivity.this).dismiss();
                        ToastUtils.showShortToast(code);
                        LogUtils.d("code:" + code);
                    }
                });
    }

    public String changeTime(int eRepeatType, long itemTime) {
        StringBuffer sb = new StringBuffer();
        Date da = new Date();
        da.setTime(itemTime);
        switch (eRepeatType) {//0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
            case 0:
            case 1:
                if (TimeUtils.getTime(itemTime, TimeUtils.DATE_FORMAT_DATE).equals(TimeUtils.getTime(System
                        .currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE))) {
                    sb.append("今天");
                } else {
                    int month = TimeUtils.getMonthFromDate(da);
                    int day = TimeUtils.getDayFromDate(da);
                    String sb1 = month + "月" + day + "日";
                    sb.append(sb1);
                    sb.append(" " + TimeUtils.getWeekFromIn(TimeUtils.getWeekFromDate(da)));
                }
                break;
            case 2:
                sb.append("每天");
                break;
            case 3:
                int month3 = TimeUtils.getMonthFromDate(da);
                int day3 = TimeUtils.getDayFromDate(da);
                String sb3 = month3 + "月" + day3 + "日";
                sb.append(sb3);
                sb.append(TimeUtils.getWeekFromIn(TimeUtils.getWeekFromDate(da)));
                sb.append(", 每周");
                break;
            case 4:
                int month = TimeUtils.getMonthFromDate(da);
                int day = TimeUtils.getDayFromDate(da);
                String sb1 = month + "月" + day + "日";
                sb.append(sb1);
                sb.append(", 每月");
                break;
            case 5:
                sb.append("工作日");
                break;
            case 6:
                sb.append("节假日");
                break;
        }
        return sb.toString();
    }

//    @Override
//    public void update(Observable o, Object arg) {
//        TIMMessage msg = (TIMMessage) arg;
//        for (int i = 0; i < msg.getElementCount(); ++i) {
//            TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
//            try {
//                dealMsg(elem.getData());
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//                ToastUtils.showShortToast("数据异常，请重试");
//                LoadingDialog.getInstance(RemindActivity.this).dismiss();
//            }
//        }
//    }

//    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
//        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
//                .parseFrom((byte[]) arg);
//        String action = msg.getHeader().getAction();
//        switch (action) {
//            case "/im/GUID/Action":
//                String guid = msg.getPayload().unpack(GPCommons.Common.class).getDataValue();
//                AuthLive.getInstance().getCurrentPig().setGuid(guid);
//                onRefresh();
//                break;
//        }
//    }
//
//    public void getGUID() {
//        if (mHandler.hasMessages(1)) {
//            mHandler.removeMessages(1);
//        }
//        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);// 20s 秒后检查加载框是否还在
//        UbtTIMManager.getInstance().getGuid();
//    }
}

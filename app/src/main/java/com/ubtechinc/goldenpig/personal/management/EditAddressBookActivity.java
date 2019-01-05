package com.ubtechinc.goldenpig.personal.management;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserContacts;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
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

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CONTACT_CHECK_SUCCESS;

public class EditAddressBookActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    EditAddressBookAdapter adapter;
    private ArrayList<AddressBookmodel> mList;
    public int deletePosition = 0;
    /**
     * 先拉取到数据，添加联系人时要在app端作对比后再提交给音箱
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
                if (mWeakReference.get() != null) {
                    //((AddressBookActivity) mWeakReference.get()).refreshLayout.finishRefresh(true);
                    ToastUtils.showShortToast(mWeakReference.get().getString(R.string.timeout_error_toast));
                    ((EditAddressBookActivity) mWeakReference.get()).mStateView.showRetry();
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_address_book;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
        mList = getIntent().getParcelableArrayListExtra("list");
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        adapter = new EditAddressBookAdapter(this, mList);
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
                LoadingDialog.getInstance(EditAddressBookActivity.this).dismiss();
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    ToastUtils.showShortToast("八戒未登录");
                } else {
                    ToastUtils.showShortToast("未绑定八戒");
                }
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver", "sss");
            }
        });
    }

    public void onRefreshSuccess(List<AddressBookmodel> list) {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        hasLoadMsg = true;
        //refreshLayout.finishRefresh(true);
        mList.clear();
        //mList.addAll(list);
        if (list.size() == 0) {
            mStateView.showEmpty();
            tv_right.setVisibility(View.GONE);
        } else if (mList.size() >= 10) {
            AddressBookmodel ab2 = new AddressBookmodel();
            ab2.type = 2;
            mList.add(ab2);
            mList.addAll(list);
            AddressBookmodel ab = new AddressBookmodel();
            ab.type = 1;
            mList.add(ab);
            mStateView.showContent();
            tv_right.setVisibility(View.VISIBLE);
        } else {
            AddressBookmodel ab2 = new AddressBookmodel();
            ab2.type = 2;
            mList.add(ab2);
            mList.addAll(list);
            mStateView.showContent();
            tv_right.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTitlebarRightIcon(boolean highlight) {
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
//                SwipeMenuItem addItem = new SwipeMenuItem(AddressBookActivity.this)
//                        .setBackgroundColor(getResources().getColor(R.color
//                                .ubt_tab_btn_txt_checked_color))
//                        .setText("编辑")
//                        .setTextColor(Color.WHITE)
//                        .setTextSize(16)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
                SwipeMenuItem deleteItem = new SwipeMenuItem(EditAddressBookActivity.this)
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
                    UbtTIMManager.getInstance().deleteUser(mList.get(adapterPosition).name, mList
                            .get(adapterPosition).phone, mList.get(adapterPosition).id + "");
                    deletePosition = adapterPosition;
                    LoadingDialog.getInstance(EditAddressBookActivity.this).setTimeout(20)
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
        try {
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMElem tIMElem = msg.getElement(i);
                if (tIMElem != null && tIMElem instanceof TIMCustomElem) {
                    TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
                    dealMsg(elem.getData());
                }
            }
        } catch (Exception e) {
            ToastUtils.showShortToast(getString(R.string.msg_error_toast));
            mStateView.showRetry();
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
            case "/im/mail/query":
                List<UserContacts.User> list = msg.getPayload().unpack(UserContacts.UserContact
                        .class).getUserList();
                List<AddressBookmodel> ss = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    AddressBookmodel mo = new AddressBookmodel();
                    mo.name = list.get(j).getName();
                    mo.phone = list.get(j).getNumber();
                    mo.id = list.get(j).getId();
                    ss.add(mo);
                }
                onRefreshSuccess(ss);
                break;
            case "/im/mail/add":
                msg.getPayload().unpack(GPResponse.Response.class).getResult();
                break;

            case "/im/mail/delete":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                LoadingDialog.getInstance(EditAddressBookActivity.this).dismiss();
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
                        tv_right.setVisibility(View.GONE);
                    }
                    updateTitlebarRightIcon(true);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShortToast("删除失败，请重试");
                }
                break;
            case "/im/mail/update":
                msg.getPayload().unpack(GPResponse.Response.class).getResult();
                break;
            default:
        }
    }

    @Override
    protected void onReceiveStickyEvent(Event event) {
        super.onReceiveStickyEvent(event);
        if (event.getCode() == CONTACT_CHECK_SUCCESS) {
            //refreshLayout.autoRefresh();
            refresh();
        }
    }

    public void refresh() {
        mStateView.showLoading();
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);// 20s 秒后检查加载框是否还在
        UbtTIMManager.getInstance().queryUser();
    }
}

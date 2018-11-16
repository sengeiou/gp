package com.ubtechinc.goldenpig.personal.management;

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
import com.ubt.improtolib.UserContacts;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewImg;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.mvp.MVPBaseActivity;
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

public class AddressBookActivity extends MVPBaseActivity<AddressBookContract.View,
        AddressBookPrestener> implements AddressBookContract.View, Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewImg rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    //    @BindView(R.id.refreshLayout)
//    SmartRefreshLayout refreshLayout;
    AddressBookAdapter adapter;
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
                ToastUtils.showShortToast("请求超时，请重试");
                if (mWeakReference.get() != null) {
                    //((AddressBookActivity) mWeakReference.get()).refreshLayout.finishRefresh(true);
                    ((AddressBookActivity) mWeakReference.get()).mStateView.showRetry();
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_address_book;
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
                //refreshLayout.autoRefresh();
                refresh();
            }
        });
        mStateView.setEmptyViewIcon(R.drawable.img_maillist);
        rl_titlebar.setTitleText(getString(R.string.address_book));
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
//                if (!hasLoadMsg) {
//                    ToastUtils.showShortToast("请先加载联系人成功后再添加");
//                    return;
//                }
                if (mList.size() < 10) {
                    Intent it = new Intent(AddressBookActivity.this, AddAndSetContactActivity
                            .class);
                    it.putParcelableArrayListExtra("list", mList);
                    startActivity(it);
                } else {
                    ToastUtils.showShortToast(getString(R.string.contact_limit));
                }
            }
        });
//        refreshLayout.setEnableAutoLoadMore(false);
//        refreshLayout.setOnRefreshListener(this);
        mList = new ArrayList<>();
        // adapter = new AddressBookAdapter(this, mList);
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
        adapter = new AddressBookAdapter(this, mList);
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
                LoadingDialog.getInstance(AddressBookActivity.this).dismiss();
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("小猪未登录");
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定小猪");
                }
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver", "sss");
            }
        });
        //refreshLayout.autoRefresh();
        refresh();
    }

//    @Override
//    public void onRefresh(RefreshLayout refreshLayout) {
//        mStateView.showLoading();
//        if (mHandler.hasMessages(1)) {
//            mHandler.removeMessages(1);
//        }
//        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);// 20s 秒后检查加载框是否还在
//        UbtTIMManager.getInstance().queryUser();
//    }

    @Override
    public void onRefreshSuccess(List<AddressBookmodel> list) {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        hasLoadMsg = true;
        //refreshLayout.finishRefresh(true);
        mList.clear();
        mList.addAll(list);
        if (mList.size() >= 10) {
            AddressBookmodel ab = new AddressBookmodel();
            ab.type = 1;
            mList.add(ab);
            //右上角+置灰
            updateTitlebarRightIcon(false);
        } else {
            updateTitlebarRightIcon(true);
        }
        if (mList.size() == 0) {
            mStateView.showEmpty();
        } else {
            mStateView.showContent();
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTitlebarRightIcon(boolean highlight) {
        rl_titlebar.setIvRight(highlight ? R.drawable.ic_add : R.drawable.ic_add_grey);
    }

    @Override
    public void onError(String str) {
        hasLoadMsg = false;
        ToastUtils.showShortToast(str);
        if (mList.size() == 0) {
            mStateView.showRetry();
        } else {
            mStateView.showContent();
        }
    }

    @Override
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
                SwipeMenuItem addItem = new SwipeMenuItem(AddressBookActivity.this)
                        .setBackgroundColor(getResources().getColor(R.color
                                .ubt_tab_btn_txt_checked_color))
                        .setText("编辑")
                        .setTextColor(Color.WHITE)
                        .setTextSize(16)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
                SwipeMenuItem deleteItem = new SwipeMenuItem(AddressBookActivity.this)
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
                    Intent it = new Intent(AddressBookActivity.this, AddAndSetContactActivity
                            .class);
                    it.putParcelableArrayListExtra("list", mList);
                    it.putExtra("type", 1);
                    it.putExtra("position", adapterPosition);
                    startActivity(it);
                } else if (menuPosition == 1) {
                    UbtTIMManager.getInstance().deleteUser(mList.get(adapterPosition).name, mList
                            .get(adapterPosition).phone, mList.get(adapterPosition).id + "");
                    deletePosition = adapterPosition;
                    LoadingDialog.getInstance(AddressBookActivity.this).setTimeout(20)
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
                mStateView.showRetry();
                //refreshLayout.finishRefresh(true);
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
                LoadingDialog.getInstance(AddressBookActivity.this).dismiss();
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

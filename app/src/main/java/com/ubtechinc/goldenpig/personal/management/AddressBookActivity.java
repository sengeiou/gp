package com.ubtechinc.goldenpig.personal.management;

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
import android.widget.StackView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.protobuf.Extension;
import com.google.protobuf.Message;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewImg;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.mvp.MVPBaseActivity;
import com.ubtechinc.goldenpig.personal.DeviceManageActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtechinc.goldenpig.view.swipe_menu.SwipeMenuLayout;
import com.ubtechinc.goldenpig.view.swipe_menu.SwipeRecycleView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_CONTACT_SUCCESS;

import java.util.Observable;
import java.util.Observer;

public class AddressBookActivity extends MVPBaseActivity<AddressBookContract.View,
        AddressBookPrestener> implements OnRefreshListener, AddressBookContract.View, Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewImg rl_titlebar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    AddressBookAdapter adapter;
    private ArrayList<AddressBookmodel> mList;
    Handler mHandler = new Handler();
    /**
     * 先拉取到数据，添加联系人时要在app端作对比后再提交给音箱
     */
    private Boolean hasLoadMsg = false;

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
        initStateView(true);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                refreshLayout.autoRefresh();
            }
        });
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
                if (!hasLoadMsg) {
                    ToastUtils.showShortToast("请先加载联系人成功后再添加");
                    return;
                }
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
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(this);
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
        PigInfo pigInfo=AuthLive.getInstance().getCurrentPig();
        if (pigInfo!=null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }else {
            UbtTIMManager.getInstance().setPigAccount("2cb9b9a3");
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver",s);
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver","sss");
            }
        });

        UbtTIMManager.getInstance().queryUser();
        UbtTIMManager.getInstance().addUser("didid","123456789");
        refreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mPresenter.refreshData();
    }

    @Override
    public void onRefreshSuccess(List<AddressBookmodel> list) {
        hasLoadMsg = true;
        refreshLayout.finishRefresh(true);
        mList.clear();
        mList.addAll(list);
        if (mList.size() >= 10) {
            AddressBookmodel ab = new AddressBookmodel();
            ab.type = 1;
            mList.add(ab);
        }
        if (mList.size() == 0) {
            mStateView.showEmpty();
        } else {
            mStateView.showContent();
        }
        adapter.notifyDataSetChanged();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        LogUtils.d("onMessageEvent");
        if (event != null) {
            onReceiveEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void onReceiveEvent(Event event) {
        if (event.getCode() == ADD_CONTACT_SUCCESS) {
            ToastUtils.showShortToast("更新数据");
            refreshLayout.autoRefresh();
        }
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

                } else if (menuPosition == 1) {
                    mList.remove(adapterPosition);
                    try {
                        if (mList.get(mList.size() - 1).type == 1) {
                            mList.remove(mList.size() - 1);
                        }
                    } catch (Exception e) {
                    }
                    if (mList.size() == 0) {
                        mStateView.showEmpty();
                    }
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(AddressBookActivity.this, "list第" + adapterPosition + "; 右侧菜单第" +
                        menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(AddressBookActivity.this, "list第" + adapterPosition + "; 左侧菜单第" +
                        menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        LogUtils.d("");
    }

    private void addUser(String nikName, String number) {
        UbtTIMManager.getInstance().addUser(nikName, number);
    }
}

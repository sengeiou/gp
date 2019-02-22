package com.ubtechinc.goldenpig.personal.management;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.MyContact;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserContacts;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.personal.management.contact.ContactListActivity;
import com.ubtechinc.goldenpig.personal.management.contact.ContactUtil;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.RecyclerItemClickListener;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
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
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_DELETE_CONTACTS;
import static com.ubtechinc.goldenpig.utils.CommendUtil.TIMEOUT;
import static com.ubtechinc.goldenpig.utils.CommendUtil.TIMEOUT_MILLI;

public class AddressBookActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView recycler;
    AddressBookAdapter adapter;
    private ArrayList<AddressBookmodel> mList;
    public int deletePosition = -1;
    public Boolean noCard = false;
    public String pigPhoneNumber = "";
    public static final int MAXADD = 30;
    /**
     * 先拉取到数据，添加联系人时要在app端作对比后再提交给八戒
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
                    UbtToastUtils.showCustomToast(mWeakReference.get(), getString(R.string.ubt_robot_offline));
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
        noCard = getIntent().getBooleanExtra("noCard", false);
        pigPhoneNumber = getIntent().getStringExtra("pigPhoneNumber");
        initStateView(true);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                //refreshLayout.autoRefresh();
                refresh();
            }
        });
        mStateView.setEmptyResource(R.layout.adapter_mall_list_empty);
        mStateView.setOnEmptyClickListener(new StateView.OnEmptyClickListener() {
            @Override
            public void onEmptyClick(View view) {
                if (mList.size() < MAXADD) {
                    switch (view.getId()) {
                        case R.id.view_empty_click:
                            Intent it = new Intent(AddressBookActivity.this, AddAndSetContactActivity
                                    .class);
                            it.putParcelableArrayListExtra("list", mList);
                            startActivity(it);
                            break;
                        case R.id.view_empty_click2:
                            importContact();
                            break;
                    }
                } else {
                    UbtToastUtils.showCustomToast(AddressBookActivity.this, getString(R.string.contact_limit));
                }
            }
        });
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<AddressBookmodel> list = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).type == 0) {
                        list.add(mList.get(i));
                    }
                }
                Intent it = new Intent(AddressBookActivity.this, EditAddressBookActivity.class);
                it.putParcelableArrayListExtra("list", list);
                it.putExtra("noCard", noCard);
                it.putExtra("pigPhoneNumber", pigPhoneNumber);
                startActivity(it);
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
//        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
//                .ubt_wifi_list_divider)),
//                OrientationHelper.VERTICAL);
//        divider.setMargin((int) getResources().getDimension(R.dimen.dp_15), 0, (int) getResources().getDimension(R
//                .dimen.dp_15), 0);
//        divider.setHeight((int) getResources().getDimension(R.dimen.ubt_1px));
//        recycler.addItemDecoration(divider);
        recycler.setSwipeMenuCreator(swipeMenuCreator);
        recycler.setSwipeMenuItemClickListener(mMenuItemClickListener);
        adapter = new AddressBookAdapter(this, mList, new RecyclerOnItemLongListener() {
            @Override
            public void onItemLongClick(View v, int position) {

            }

            @Override
            public void onItemClick(View v, int position) {
                if (mList.size() <= MAXADD) {
                    showPopupWindow(v, mList);
                } else {
                    UbtToastUtils.showCustomToast(AddressBookActivity.this, getString(R.string.contact_limit));
                }
            }
        });
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
                    UbtToastUtils.showCustomToast(AddressBookActivity.this, "八戒未登录");
                } else {
                    UbtToastUtils.showCustomToast(AddressBookActivity.this, "未绑定八戒");
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
        } else if (mList.size() >= MAXADD) {
            AddressBookmodel ab2 = new AddressBookmodel();
            ab2.noCard = noCard;
            ab2.phone = pigPhoneNumber;
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
            ab2.noCard = noCard;
            ab2.phone = pigPhoneNumber;
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
        UbtToastUtils.showCustomToast(AddressBookActivity.this, str);
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
            if (viewType != 0) {
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
                    if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                        UbtToastUtils.showCustomToast(getApplication(), getString(R.string.network_error));
                        return;
                    }
                    UbtTIMManager.getInstance().deleteUser(mList.get(adapterPosition).name, mList
                            .get(adapterPosition).phone, mList.get(adapterPosition).id + "");
                    deletePosition = adapterPosition;
                    LoadingDialog.getInstance(AddressBookActivity.this).setTimeout(TIMEOUT)
                            .setShowToast(true).setToastTye(1).show();
                }
//                if (menuPosition == 0) {
//                    Intent it = new Intent(AddressBookActivity.this, AddAndSetContactActivity
//                            .class);
//                    it.putParcelableArrayListExtra("list", mList);
//                    it.putExtra("type", 1);
//                    it.putExtra("position", adapterPosition);
//                    startActivity(it);
//                } else if (menuPosition == 1) {
//                    UbtTIMManager.getInstance().deleteUser(mList.get(adapterPosition).name, mList
//                            .get(adapterPosition).phone, mList.get(adapterPosition).id + "");
//                    deletePosition = adapterPosition;
//                    LoadingDialog.getInstance(AddressBookActivity.this).setTimeout(20)
//                            .setShowToast(true).show();
//                }
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
            UbtToastUtils.showCustomToast(AddressBookActivity.this, getString(R.string.msg_error_toast));
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
                LoadingDialog.getInstance(AddressBookActivity.this).dismiss();
                if (flag) {
                    try {
                        mList.remove(deletePosition);
                        if (mList.get(mList.size() - 1).type == 1) {
                            mList.remove(mList.size() - 1);
                        }
                        deletePosition = -1;
                    } catch (Exception e) {
                    }
                    if (mList.size() == 0) {
                        mStateView.showEmpty();
                        tv_right.setVisibility(View.GONE);
                    }
                    updateTitlebarRightIcon(true);
                    adapter.notifyDataSetChanged();
                } else {
                    UbtToastUtils.showCustomToast(AddressBookActivity.this, "删除失败，请重试");
                }
                break;
            case "/im/mail/update":
                msg.getPayload().unpack(GPResponse.Response.class).getResult();
                break;
            default:
        }
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == CONTACT_CHECK_SUCCESS) {
            //refreshLayout.autoRefresh();
            refresh();
        } else if (event.getCode() == RECEIVE_DELETE_CONTACTS) {
            List<AddressBookmodel> list = (List<AddressBookmodel>) event.getData();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < mList.size(); j++) {
                        if (list.get(i).id == mList.get(j).id) {
                            mList.remove(j);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).type == 0) {
                    break;
                }
                if (i == mList.size() - 1) {
                    mList.clear();
                    adapter.notifyDataSetChanged();
                    mStateView.showEmpty();
                }
            }
        }
    }

    public void refresh() {
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            UbtToastUtils.showCustomToast(AddressBookActivity.this, getString(R.string.network_error));
            return;
        }
        mStateView.showLoading();
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessageDelayed(1, TIMEOUT_MILLI);// 15s 秒后检查加载框是否还在
        UbtTIMManager.getInstance().queryUser();
    }

    public void showPopupWindow(View parent, ArrayList<AddressBookmodel> mList) {
        View view = LayoutInflater.from(this).inflate(R.layout
                .view_addb_layout, null);
        RecyclerView recycler = view.findViewById(R.id.recycler);
        List<String> list = new ArrayList<>();
        list.add("添加联系人");
        list.add("导入手机通讯录");
        AddbAdapter adapter = new AddbAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //给RecyclerView设置布局管理器
        recycler.setLayoutManager(linearLayoutManager);
        Divider divider = new Divider(new ColorDrawable(0xff424242), OrientationHelper.VERTICAL);
        //单位:px
//        divider.setMargin(mContext.getResources().getDimensionPixelSize(R.dimen.dp_15), 0, mContext.getResources()
//                .getDimensionPixelSize(R.dimen.dp_15), 0);
        divider.setHeight(1);
        recycler.addItemDecoration(divider);
        recycler.setAdapter(adapter);

        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //backgroundAlpha(0.5f);//设置屏幕透明度
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
            }
        });
        recycler.addOnItemTouchListener(new RecyclerItemClickListener(this) {
            @Override
            protected void onItemClick(View view, int position) {
                popupWindow.dismiss();
                switch (position) {
                    case 0:
                        Intent it = new Intent(AddressBookActivity.this, AddAndSetContactActivity
                                .class);
                        it.putParcelableArrayListExtra("list", mList);
                        startActivity(it);
                        break;
                    case 1:
                        importContact();
                        break;
                }
            }
        });
        popupWindow.showAsDropDown(parent, -getResources().getDimensionPixelSize(R.dimen.dp_95), -10);
    }

    /**
     * 导入联系人
     */
    private void importContact() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            AndPermission.with(this)
                    .requestCode(0x1101)
                    .permission(Permission.CONTACTS)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            intentToContact();
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            showPermissionDialog(Permission.CONTACTS);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();
        } else {
            intentToContact();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (AndPermission.hasPermission(this, Manifest.permission.READ_CONTACTS)) {
//                intentToContact();
//            } else if (AndPermission.hasAlwaysDeniedPermission(this, Arrays.asList(Manifest.permission.READ_CONTACTS)
//            )) {
//                showPermissionDialog(Permission.CONTACTS);
//            } else {
//                ActivityCompat.requestPermissions(AddressBookActivity.this,
//                        new String[]{android.Manifest.permission.READ_CONTACTS},
//                        GlobalVariable.REQUEST_CONTACTS_READ_PERMISSON);
//            }
////            if (ContextCompat.checkSelfPermission(AddAndSetContactActivity.this, android.Manifest.permission
//// .READ_CONTACTS)
////                    != PackageManager.PERMISSION_GRANTED) {
////                // 若不为GRANTED(即为DENIED)则要申请权限了
////                // 申请权限 第一个为context 第二个可以指定多个请求的权限 第三个参数为请求码
////                ActivityCompat.requestPermissions(AddAndSetContactActivity.this,
////                        new String[]{android.Manifest.permission.READ_CONTACTS},
////                        GlobalVariable.REQUEST_CONTACTS_READ_PERMISSON);
////            } else {
////                //权限已经被授予，在这里直接写要执行的相应方法即可
////                intentToContact();
////            }
//        } else {
//            // 低于6.0的手机直接访问
//            intentToContact();
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
//            grantResults) {
//        if (requestCode == GlobalVariable.REQUEST_CONTACTS_READ_PERMISSON) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                intentToContact();
//            } else {
//                UbtToastUtils.showCustomToast(AddressBookActivity.this, "授权被禁止");
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions,
//                grantResults);
//    }

    private void intentToContact() {
        // 跳转到联系人界面
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.PICK");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.setType("vnd.android.cursor.dir/phone_v2");
//        startActivityForResult(intent, GlobalVariable.REQUEST_CONTACTS_READ_RESULT);
        ArrayList<MyContact> cache = new ArrayList<>();
        cache.addAll(ContactUtil.getInstance(this).getContactList());
        Intent it = new Intent(this, ContactListActivity.class);
        it.putParcelableArrayListExtra("list", mList);
        it.putParcelableArrayListExtra("cache", cache);
        startActivity(it);
    }
}

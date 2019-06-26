package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTFunctionDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.fragment.MenuPopupView;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.adpater.PigMemberAdapter;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.pigmanager.register.TransferAdminHttpProxy;
import com.ubtechinc.goldenpig.push.PushHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.FastClickUtils;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtrobot.clear.ClearContainer;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_CLEAR_PIG_INFO;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

/**
 * @author :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :成员组管理
 * @time :2018/9/19 21:11
 * @change :
 * @changetime :2018/9/19 21:11
 */
public class PigMemberActivity extends BaseToolBarActivity implements View.OnClickListener, PigMemberAdapter.OnMemberClickListener {
    private SwipeMenuRecyclerView mMemberRcy;
    private PigMemberAdapter adapter;
    private PigInfo mPig;
    private ArrayList<CheckBindRobotModule.User> mUsertList = new ArrayList<>();
    private boolean isDownloadedUserList;
    private UnbindPigProxy.UnBindPigCallback unBindPigCallback;

    private TextView tvMemberTip;

    private boolean isUnbindAll;

    private String needTransferUserId;

    @Override
    protected int getConentView() {
        return R.layout.activity_pigmember;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setTitleBack(true);
        setToolBarTitle(getString(R.string.ubt_menber_group));
        initViews();
        initData();
    }

    private void initData() {
        unBindPigCallback = new UnbindPigProxy.UnBindPigCallback() {

            @Override
            public void onError(String msg) {
                runOnUiThread(() -> ToastUtils.showShortToast(PigMemberActivity.this, msg));
            }

            @Override
            public void onSuccess() {
                imSyncRelationShip();
                runOnUiThread(() -> {
                    isDownloadedUserList = false;
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast(R.string.ubt_ubbind_success);
                    updatePigList();
                    getMember("1");
                });
            }
        };
    }

    private void imSyncRelationShip() {
        if (AuthLive.getInstance().getCurrentPig().isAdmin) {
            //TODO 给自己的猪发
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(3));
            UbtTIMManager.getInstance().sendTIM(selfMessage);

            //TODO 如果有配对关系
            PairPig pairPig = AuthLive.getInstance().getPairPig();
            if (pairPig != null) {
                //TODO 给配对的用户发
                TIMConversation pairUserConversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C, String.valueOf(pairPig.getPairUserId()));
                TIMMessage pairUserMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
                UbtTIMManager.getInstance().sendTIM(pairUserMessage, pairUserConversation);

                //TODO 给配对的猪发
                TIMConversation pairPigConversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C, pairPig.getPairSerialNumber());
                TIMMessage pairPigMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
                UbtTIMManager.getInstance().sendTIM(pairPigMessage, pairPigConversation);

            }
        }
    }

    private void initViews() {
        mMemberRcy = findViewById(R.id.ubt_rcy_member);

        mToolbarRightBtn = findViewById(R.id.ubt_imgbtn_add);
        mToolbarRightBtn.setOnClickListener(this);

        mPig = AuthLive.getInstance().getCurrentPig();
        tvMemberTip = findViewById(R.id.tv_member_tip);
        if (mPig != null && mPig.isAdmin) {
            tvMemberTip.setText("HI，你是八戒机器人的管理员，可管理成员组");
        } else {
            tvMemberTip.setText("HI，欢迎加入八戒的成员组，你可以给八戒配置网络");
        }

        adapter = new PigMemberAdapter(this, mUsertList);
        adapter.setmOnMemberClickListener(this);
        mMemberRcy.setLayoutManager(new LinearLayoutManager(this));
//        mMemberRcy.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dp_5), false));
        mMemberRcy.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.ubt_wifi_list_divider)));
        mMemberRcy.setSwipeMenuCreator(swipeMenuCreator);
        mMemberRcy.setSwipeMenuItemClickListener(mMenuItemClickListener);
        mMemberRcy.setAdapter(adapter);
        adapter.notifyDataSetChanged();

//        setAddBtnEnable(isCurrentAdmin());

    }

    private boolean isCurrentAdmin() {
        try {
            UserInfo currentUser = AuthLive.getInstance().getCurrentUser();
            if (mUsertList != null && currentUser != null) {
                for (CheckBindRobotModule.User user : mUsertList) {
                    if (user.getIsAdmin() == 1 && user.getUserId() == Integer.valueOf(currentUser.getUserId())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDownloadedUserList = false;
        getMember("1");
    }

    private synchronized void getMember(String admin) {
        if (isDownloadedUserList) {
//            setAddBtnEnable(isCurrentAdmin());
            if (adapter != null) {
                Set<CheckBindRobotModule.User> set = new TreeSet<>((o1, o2) -> String.valueOf(o1.getUserId()).compareTo(String.valueOf(o2.getUserId())));
                set.addAll(mUsertList);
                mUsertList.clear();
                mUsertList.addAll(set);
                String currUId = AuthLive.getInstance().getUserId();
                if (!mUsertList.isEmpty()) {
                    boolean contain = false;
                    for (CheckBindRobotModule.User user : mUsertList) {
                        int uid = user.getUserId();
                        if (currUId.equals(String.valueOf(uid))) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain) {
                        Collections.sort(mUsertList, (o1, o2) -> o2.getIsAdmin() - o1.getIsAdmin());
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }
            return;
        }
        if ("0".equals(admin)) {
            isDownloadedUserList = true;
        }
        if (mPig == null) {
            ToastUtils.showShortToast(this, getString(R.string.ubt_no_pigs));
            return;
        }
        if (mUsertList == null) {
            mUsertList = new ArrayList<>();
        } else if ("1".equals(admin) && mUsertList != null) {
            mUsertList.clear();
        }
        CheckUserRepository repository = new CheckUserRepository();
        repository.getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, admin, new CheckUserRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                ToastUtils.showShortToast(PigMemberActivity.this, "获取成员列表失败");
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                ToastUtils.showShortToast(PigMemberActivity.this, "获取成员列表成功");
            }

            @Override
            public void onSuccessWithJson(String jsonStr) {
                LoadingDialog.getInstance(PigMemberActivity.this).dismiss();
                final List<CheckBindRobotModule.User> bindUsers = jsonToUserList(jsonStr);
                if (mUsertList != null) {
                    mUsertList.addAll(bindUsers);
                }
                getMember("0");
            }
        });
    }

    private void setAddBtnEnable(boolean isEnable) {
        if (isEnable) {
            mToolbarRightBtn = findViewById(R.id.ubt_imgbtn_add);
            mToolbarRightBtn.setVisibility(View.VISIBLE);
            mToolbarRightBtn.setOnClickListener(this);
        } else {
            findViewById(R.id.ubt_imgbtn_add).setVisibility(View.GONE);
        }
    }

//    private void doUnbind(final String userId) {
//        if (mPig == null) {
//            return;
//        }
//        ///操作用户是唯一或只是一般成员可好直接弹框点击确认退出
//        //否则要跳转到权限转让界面操作
//        if (mUsertList.size() > 1 && isCurrentAdmin()) {
////            HashMap<String, ArrayList<CheckBindRobotModule.User>> param = new HashMap<>();
////            param.put("users", mUsertList);
////            ActivityRoute.toAnotherActivity(this, TransferAdminActivity.class, param, false);
//
//            UBTSubTitleDialog dialog = new UBTSubTitleDialog(this);
//            dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
//            dialog.setTips(getString(R.string.ubt_exit_group_tips));
//            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
//            dialog.setRightButtonTxt(getString(R.string.ubt_enter));
//            dialog.setSubTips(getString(R.string.ubt_transfer_tips));
//            dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
//                @Override
//                public void onLeftButtonClick(View view) {
//
//                }
//
//                @Override
//                public void onRightButtonClick(View view) {
//                    ActivityRoute.toAnotherActivity(PigMemberActivity.this, TransferAdminActivity.class,
//                            0x01, false);
//                }
//            });
//            dialog.show();
//
//        } else {
//            UBTFunctionDialog dialog = new UBTFunctionDialog(this);
//            dialog.setFunc1Txt(getString(R.string.exit_unbind));
//            dialog.setFunc2Txt(getString(R.string.ubt_cancel));
//            dialog.setTips(getString(R.string.ubt_drop_up_tips));
//
//            dialog.setOnUbtDialogClickLinsenter(new UBTFunctionDialog.OnUbtDialogClickLinsenter() {
//                @Override
//                public void onFunc1Click(View view) {
//                    UnbindPigProxy pigProxy = new UnbindPigProxy();
//                    final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();
//
//                    final String token = CookieInterceptor.get().getToken();
//                    pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
//
//                }
//
//                @Override
//                public void onFunc2Click(View view) {
//                    dialog.cancel();
//                }
//
//                @Override
//                public void onClose(View view) {
//
//                }
//            });
//            dialog.show();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0x01:
                if (resultCode == RESULT_OK) {
                    //TODO 先转让管理员再退出群组
                    doExitGroup();
                }
                break;
        }
    }

    private void doExitGroup() {
        UnbindPigProxy pigProxy = new UnbindPigProxy();
        final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();

        final String token = CookieInterceptor.get().getToken();
        pigProxy.unbindPig(serialNo, AuthLive.getInstance().getUserId(), token, BuildConfig.APP_ID, unBindPigCallback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_imgbtn_add:
                HashMap<String, Boolean> param = new HashMap<>();
                param.put("isPair", false);
                ActivityRoute.toAnotherActivity(this, QRCodeActivity.class, param, false);
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
//            if (viewType == 1) {
//                return;
//            }
            if (viewType != -1) {
                return;
            }
            int width = getResources().getDimensionPixelSize(R.dimen.dp_65);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem transferItem = new SwipeMenuItem(PigMemberActivity.this)
                        .setBackgroundColor(getResources().getColor(R.color
                                .ubt_tab_btn_txt_checked_color))
                        .setText(R.string.ubt_trans_admin)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.dp_88))
                        .setHeight(height);
                swipeRightMenu.addMenuItem(transferItem); // 添加菜单到右侧。
                SwipeMenuItem deleteItem = new SwipeMenuItem(PigMemberActivity.this)
                        .setBackground(getResources().getDrawable(R.drawable.shape_ubt_member_menu_bg))
                        .setText(R.string.ubt_delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

            }
        }
    };
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (mUsertList != null && adapterPosition > -1 && adapterPosition < mUsertList.size()) {
                    if (menuPosition == 1) {
                        showDeleteMember(String.valueOf(mUsertList.get(adapterPosition).getUserId()));
                    } else if (menuPosition == 0) {
                        showTransferAdminDialog(String.valueOf(mUsertList.get(adapterPosition).getUserId()));
                    }
                }
            }
        }
    };

    /**
     * 显示删除成员确定框
     *
     * @param userId 用户ID
     */
    private void showDeleteMember(final String userId) {
        //doUnbind(String.valueOf(userId);
        UBTBaseDialog dialog = new UBTBaseDialog(this);
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setTips(getString(R.string.ubt_delte_member_tips));
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                UnbindPigProxy pigProxy = new UnbindPigProxy();
                final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();

                final String token = CookieInterceptor.get().getToken();
                pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, new UnbindPigProxy.UnBindPigCallback() {

                    @Override
                    public void onError(String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(PigMemberActivity.this, msg);
                            }
                        });

                    }

                    @Override
                    public void onSuccess() {
                        doPushDelMemberMsg(userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isDownloadedUserList = false;
                                updatePigList();
                                getMember("1");
                            }
                        });
                    }
                });
            }
        });
        dialog.show();
    }

    private void doPushDelMemberMsg(String userId) {
        //TODO 给删除成员推送
        PushHttpProxy pushHttpProxy = new PushHttpProxy();
        Map map = new HashMap();
        map.put("app_category", 1);
        pushHttpProxy.pushToken("", "你已被管理员移除成员组", userId, map, 1);
    }

    /**
     * 显示转让权限确认对话框
     */
    private void showTransferAdminDialog(final String userId) {
        UBTSubTitleDialog dialog = new UBTSubTitleDialog(this);
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setTips(getString(R.string.ubt_trandfer_admin_tips));
        dialog.setRadioText(getString(R.string.unbind_confirm_tip2));
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setSubTips(getString(R.string.ubt_transfer_tips));
        dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO do管理员权限转让
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                if (dialog.isRadioSelected()) {
                    needTransferUserId = userId;
                    doClearInfoByIM();
                } else {
                    doTransferAdmin(userId);
                }

            }
        });
        dialog.show();
    }

    /**
     * 执行转让权限操作
     */
    private void doTransferAdmin(String userId) {
        LoadingDialog.getInstance(this).show();
        TransferAdminHttpProxy httpProxy = new TransferAdminHttpProxy();
        httpProxy.transferAdmin(this, CookieInterceptor.get().getToken(), AuthLive.getInstance().getCurrentPig().getRobotName(), userId, new TransferAdminHttpProxy.TransferCallback() {
            @Override
            public void onError(String error) {
                LoadingDialog.getInstance(PigMemberActivity.this).dismiss();
                com.ubtech.utilcode.utils.ToastUtils.showShortToast(error);
            }

            @Override
            public void onException(Exception e) {
                LoadingDialog.getInstance(PigMemberActivity.this).dismiss();
//                com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让失败");
            }

            @Override
            public void onSuccess(String msg) {
                com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让成功");
                imSyncRelationShip();
                doPushTransferMsg(userId);
                isDownloadedUserList = false;
                updatePigList();
                getMember("1");
            }
        });
    }

    private void doPushTransferMsg(String userId) {
        //TODO 给新管理员推送
        try {
            PushHttpProxy pushHttpProxy = new PushHttpProxy();
            Map map = new HashMap();
            map.put("app_category", 1);
            pushHttpProxy.pushToken("", "您已被指定为管理员", userId, map, 1);
            UbtTIMManager.getInstance().doTIMLogout();
        } catch (Exception e) {
            //TODO
        }

    }

    private void updatePigList() {
        if (AuthLive.getInstance().getCurrentPigList() != null) {
            AuthLive.getInstance().getCurrentPigList().clear();
        }
        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new GetPigListHttpProxy.OnGetPigListLitener() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onException(Exception e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onSuccess(String response) {
                Log.e("getPigList", response);
                PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance().getCurrentPigList());
                ArrayList<PigInfo> list = AuthLive.getInstance().getCurrentPigList();
                if (list == null || list.isEmpty()) {
                    finish();
                }
            }
        });
    }

    private List<CheckBindRobotModule.User> jsonToUserList(String jsonStr) {
        List<CheckBindRobotModule.User> result = null;
        Gson gson = new Gson();
        try {
            result = gson.fromJson(jsonStr, new TypeToken<List<CheckBindRobotModule.User>>() {
            }.getType());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) {
            return;
        }
        int code = event.getCode();
        switch (code) {
            case USER_PIG_UPDATE:
                isDownloadedUserList = false;
                updateData();
                break;
            case RECEIVE_CLEAR_PIG_INFO:
                if ((boolean) event.getData()) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除成功");
                    if (!TextUtils.isEmpty(needTransferUserId)) {
                        doTransferAdmin(needTransferUserId);
                        needTransferUserId = null;
                    } else if (isUnbindAll) {
                        doUnbindAllMember();
                    } else {
                        doUnbind();
                    }
                } else {
                    needTransferUserId = null;
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除失败，请重试");
                }
                break;
                default:
        }
    }

    private void updateData() {
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null) {
            tvMemberTip = findViewById(R.id.tv_member_tip);
            if (mPig.isAdmin) {
                tvMemberTip.setText("HI，你是八戒机器人的管理员，可管理成员组");
            } else {
                tvMemberTip.setText("HI，欢迎加入八戒的成员组，你可以给八戒配置网络");
            }
            getMember("1");
        } else {
            finish();
        }
    }

    @Override
    public void onClickExitGroup(View view, String userId) {
        if (mUsertList.size() == 1) {
            //TODO 肯定是管理员
            UBTFunctionDialog dialog = new UBTFunctionDialog(this);
            dialog.setFunc1Txt(getString(R.string.exit_unbind));
            dialog.setFunc2Txt(getString(R.string.ubt_cancel));
            dialog.setTips(getString(R.string.ubt_drop_up_tips));

            dialog.setOnUbtDialogClickLinsenter(new UBTFunctionDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onFunc1Click(View view) {
                    showUnBindConfirmDialog(userId);
                }

                @Override
                public void onFunc2Click(View view) {
                    dialog.cancel();
                }

                @Override
                public void onClose(View view) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            //TODO 判断是否为管理
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo != null && pigInfo.isAdmin) {
                UBTFunctionDialog dialog = new UBTFunctionDialog(this);
                dialog.setFunc1Txt(getString(R.string.unbind_only_self));
                dialog.setFunc2Txt(getString(R.string.unbind_all));
                dialog.showCloseIcon(true);
                dialog.setTips(getString(R.string.ubt_unbind_group_tips));

                dialog.setOnUbtDialogClickLinsenter(new UBTFunctionDialog.OnUbtDialogClickLinsenter() {
                    @Override
                    public void onFunc1Click(View view) {
                        ActivityRoute.toAnotherActivity(PigMemberActivity.this, TransferAdminActivity.class, 0x01, false);
                    }

                    @Override
                    public void onFunc2Click(View view) {
                        isUnbindAll = true;
                        showUnBindConfirmDialog(userId);
//                        doUnbindAllMember();
                    }

                    @Override
                    public void onClose(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            } else {
                UBTFunctionDialog dialog = new UBTFunctionDialog(this);
                dialog.setFunc1Txt(getString(R.string.ubt_enter));
                dialog.setFunc2Txt(getString(R.string.ubt_cancel));
                dialog.setTips(getString(R.string.ubt_exit_group_common));

                dialog.setOnUbtDialogClickLinsenter(new UBTFunctionDialog.OnUbtDialogClickLinsenter() {
                    @Override
                    public void onFunc1Click(View view) {
                        doUnbind();
                    }

                    @Override
                    public void onFunc2Click(View view) {
                        dialog.cancel();
                    }

                    @Override
                    public void onClose(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        }
    }

    /**
     * 全部成员解绑
     */
    private void doUnbindAllMember() {
        UnbindAllMemberProxy proxy = new UnbindAllMemberProxy();
        final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();
        proxy.unbind(serialNo, new UnbindAllMemberProxy.UnBindPigCallback() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> ToastUtils.showShortToast(PigMemberActivity.this, msg));
            }

            @Override
            public void onSuccess() {
                imSyncRelationShip();
                doPushUnbindMsg();
                runOnUiThread(() -> updatePigList());
            }
        });
    }

    /**
     * 群推解绑消息
     */
    private void doPushUnbindMsg() {
        UserInfo currentUser = AuthLive.getInstance().getCurrentUser();
        if (mUsertList != null && currentUser != null) {
            for (CheckBindRobotModule.User user : mUsertList) {
                String userId = String.valueOf(user.getUserId());
                if (!currentUser.getUserId().equals(userId)) {
                    PushHttpProxy pushHttpProxy = new PushHttpProxy();
                    Map map = new HashMap();
                    map.put("app_category", 1);
                    pushHttpProxy.pushToken("", "你已被管理员移除成员组", userId, map, 1);
                }
            }
        }
    }

    private void showUnBindConfirmDialog(final String userId) {
        UBTSubTitleDialog unBindConfirmDialog = new UBTSubTitleDialog(this);
        unBindConfirmDialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        unBindConfirmDialog.setSubTipColor(ContextCompat.getColor(this, R.color.ubt_tips_txt_color));
        unBindConfirmDialog.setTips(getString(R.string.unbind_confirm));
        unBindConfirmDialog.setRadioText(getString(R.string.unbind_confirm_tip2));
        unBindConfirmDialog.setRadioSelected(true);
        unBindConfirmDialog.setRightButtonTxt(getString(R.string.ubt_enter));
        unBindConfirmDialog.setSubTips(getString(R.string.unbind_confirm_tip));
        unBindConfirmDialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                if (unBindConfirmDialog.isRadioSelected()) {
                    doClearInfoByIM();
                } else {
                    if (isUnbindAll) {
                        doUnbindAllMember();
                    } else {
                        doUnbind();
                    }
                }
            }
        });
        unBindConfirmDialog.show();
    }

    private synchronized void doUnbind() {
        UnbindPigProxy pigProxy = new UnbindPigProxy();
        String userId = AuthLive.getInstance().getUserId();
        final String serialNo = AuthLive.getInstance().getCurrentPig().getRobotName();
        final String token = CookieInterceptor.get().getToken();
        pigProxy.unbindPig(serialNo, userId, token, BuildConfig.APP_ID, unBindPigCallback);
    }

    private void doClearInfoByIM() {
        if (!UBTPGApplication.isRobotOnline) {
            if (!TextUtils.isEmpty(needTransferUserId)) {
                needTransferUserId = null;
                UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline_clear_tip_for_transfer));
            } else {
                UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline_clear_tip));
            }
            return;
        }
        List<ClearContainer.Categories.Builder> categorys = new ArrayList<>();
        ClearContainer.Categories.Builder categoryBuilder1 = ClearContainer.Categories.newBuilder();
        categoryBuilder1.setName("Contact.deleteContact");
        ClearContainer.Categories.Builder categoryBuilder2 = ClearContainer.Categories.newBuilder();
        categoryBuilder2.setName("Record.deleteData");
        categorys.add(categoryBuilder1);
        categorys.add(categoryBuilder2);
        UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.clearInfo(categorys)));
    }

    @Override
    public void onClickOperMore(View view, String userId) {
        showOperatePop(view, userId);
    }

    private MenuPopupView mMenuPopupView;

    private void showOperatePop(View view, final String userId) {
        List<String> menuVal = new ArrayList<>();
        menuVal.add("转让管理员");
        menuVal.add("删除该成员");
        menuVal.add("取消");
        mMenuPopupView = new MenuPopupView(this, menuVal)
                .showAtBottom(view)
                .setCallback(new MenuPopupView.MenuCallback() {
                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onClickMenu(int position, View view, String value) {
                        switch (position) {
                            case 0:
                                showTransferAdminDialog(userId);
                                mMenuPopupView.dismiss();
                                break;
                            case 1:
                                showDeleteMember(userId);
                                mMenuPopupView.dismiss();
                                break;
                            case 2:
                                mMenuPopupView.dismiss();
                                break;
                                default:
                        }
                    }
                });
    }
}

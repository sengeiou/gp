package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.adpater.MemberPermissionAdapter;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.TransferAdminHttpProxy;
import com.ubtechinc.goldenpig.push.PushHttpProxy;
import com.ubtechinc.goldenpig.utils.FastClickUtils;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtrobot.clear.ClearContainer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.app.Constant.SP_LAST_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_CLEAR_PIG_INFO;

/**
 * @author ubt
 */
public class TransferAdminActivity extends BaseToolBarActivity implements View.OnClickListener {
    private ArrayList<CheckBindRobotModule.User> mUserList = new ArrayList<>();
    private MemberPermissionAdapter adapter;
    private RecyclerView memberRcy;
    private PigInfo mPig;
    private boolean isDownloadedUserList;

    @Override
    protected int getConentView() {
        return R.layout.activity_transfer_admin;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBusUtil.register(this);
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_trans_permission);
        setSkipEnable(true);
        initViews();
        getUserList(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getUserList(intent);
    }

    private void initViews() {
        mTvSkip = findViewById(R.id.ubt_tv_set_net_skip);
        mTvSkip.setVisibility(View.VISIBLE);
        mTvSkip.setText(R.string.ubt_complete);
        mTvSkip.setOnClickListener(this);

        memberRcy = findViewById(R.id.ubt_rcy_permission_member);
        adapter = new MemberPermissionAdapter(this, mUserList);
        memberRcy.setLayoutManager(new WrapContentLinearLayoutManager(this));
        memberRcy.setAdapter(adapter);

        mPig = AuthLive.getInstance().getCurrentPig();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    private void getUserList(Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.hasExtra("users")) {
            mUserList = (ArrayList<CheckBindRobotModule.User>) intent.getSerializableExtra("users");
            if (mUserList != null) {
                mUserList.addAll(mUserList);
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        if (mUserList == null || mUserList.size() == 0) {
            mUserList = new ArrayList<>();
            getMember("0");
        }
    }

    private void getMember(String admin) {
        if (isDownloadedUserList) {
            return;
        }
        if ("0".equals(admin)) {
            isDownloadedUserList = true;
        }
        if (mPig == null) {
            ToastUtils.showShortToast(this, getString(R.string.ubt_no_pigs));
            return;
        }
        if (mUserList == null) {
            mUserList = new ArrayList<>();
        } else if ("1".equals(admin) && mUserList != null) {
            mUserList.clear();
        }
        CheckUserRepository repository = new CheckUserRepository();
        repository.getRobotBindUsers(mPig.getRobotName(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, admin, new CheckUserRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                ToastUtils.showShortToast(TransferAdminActivity.this, "获取成员列表失败");
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {

            }

            @Override
            public void onSuccessWithJson(String jsonStr) {
                final List<CheckBindRobotModule.User> bindUsers = jsonToUserList(jsonStr);
                if (mUserList != null) {
                    mUserList.addAll(bindUsers);
                    if (adapter != null) {
                        adapter.update(mUserList);
//                        adapter.notifyDataSetChanged();
                    }
                }
                getMember("0");
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ubt_tv_set_net_skip) {
//            showTransferAdminDialog();
            showUnBindConfirmDialog();
        }
    }

    /**
     * 显示转让权限确认对话框
     */
    @Deprecated
    private void showTransferAdminDialog() {
        UBTSubTitleDialog dialog = new UBTSubTitleDialog(this);
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setTips(getString(R.string.ubt_trandfer_admin_tips));
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
                doTransferAdmin();
            }
        });
        dialog.show();
    }

    private void showUnBindConfirmDialog() {
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
                if (FastClickUtils.isFastClick()) return;
                if (unBindConfirmDialog.isRadioSelected()) {
                    doClearInfoByIM();
                } else {
                    doTransferAdmin();
                }
            }
        });
        unBindConfirmDialog.show();
    }

    private void doClearInfoByIM() {
        if (!UBTPGApplication.isRobotOnline) {
            UbtToastUtils.showCustomToast(this, getString(R.string.ubt_robot_offline_clear_tip));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case RECEIVE_CLEAR_PIG_INFO:
                if ((boolean) event.getData()) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除成功");
                    doTransferAdmin();
                } else {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("机器人数据清除失败，请重试");
                }
                break;
        }
    }


    /**
     * 执行转让权限操作
     */
    private void doTransferAdmin() {
        if (adapter != null && mUserList != null
                && adapter.getSelectedIndex() >= 0
                && mUserList.size() > adapter.getSelectedIndex()) {
            final String userId = String.valueOf(mUserList.get(adapter.getSelectedIndex()).getUserId());
            TransferAdminHttpProxy httpProxy = new TransferAdminHttpProxy();
            httpProxy.transferAdmin(this, CookieInterceptor.get().getToken(), AuthLive.getInstance().getCurrentPig().getRobotName(), userId, new TransferAdminHttpProxy.TransferCallback() {
                @Override
                public void onError(String error) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast(error);
                }

                @Override
                public void onException(Exception e) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让失败");
                }

                @Override
                public void onSuccess(String msg) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("转让成功");
                    SPUtils.get().put(SP_LAST_RECORD, "");
                    SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, 0);
                    imSyncRelationShip();
                    doPushTransferMsg(userId);
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }

    private void doPushTransferMsg(String userId) {
        //TODO 给新管理员推送
        PushHttpProxy pushHttpProxy = new PushHttpProxy();
        Map map = new HashMap();
        map.put("app_category", 1);
        pushHttpProxy.pushToken("", "您已被指定为管理员", userId, map, 1);
        UbtTIMManager.getInstance().doTIMLogout();
    }

    private void imSyncRelationShip() {
        //TODO 给自己的猪发
        TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(3));
        UbtTIMManager.getInstance().sendTIM(selfMessage);
    }
}

package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.adpater.MemberPermissionAdapter;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.CheckUserRepository;
import com.ubtechinc.goldenpig.pigmanager.register.TransferAdminHttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.ArrayList;
import java.util.List;

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
            doTransferAdmin();
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
                    imSyncRelationShip();
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }

    private void imSyncRelationShip() {
        //TODO 给自己的猪发
        TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(1));
        UbtTIMManager.getInstance().sendTIM(selfMessage);

    }
}

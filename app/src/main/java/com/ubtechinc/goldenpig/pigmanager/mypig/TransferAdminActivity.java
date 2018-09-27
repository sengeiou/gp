package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.adpater.MemberPermissionAdapter;
import com.ubtechinc.goldenpig.pigmanager.register.TransferAdminHttpProxy;

import java.util.ArrayList;

public class TransferAdminActivity extends BaseToolBarActivity implements View.OnClickListener{
    private ArrayList<CheckBindRobotModule.User> mUserList;
    private MemberPermissionAdapter adapter;
    private RecyclerView memberRcy;

    @Override
    protected int getConentView() {
        return R.layout.activity_transfer_admin;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_trans_permission);
        setSkipEnable(true);
        getUserList(getIntent());
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getUserList(intent);
    }

    private void initViews(){
        mTvSkip=findViewById(R.id.ubt_tv_set_net_skip);
        mTvSkip.setText(R.string.ubt_complete);
        mTvSkip.setOnClickListener(this);

        memberRcy=findViewById(R.id.ubt_rcy_permission_member);
        adapter=new MemberPermissionAdapter(this,mUserList);
        memberRcy.setLayoutManager(new WrapContentLinearLayoutManager(this));
        memberRcy.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
    private void getUserList(Intent intent){
        if (intent==null){
            return;
        }
        if (intent.hasExtra("users")){
            mUserList=(ArrayList<CheckBindRobotModule.User>)intent.getSerializableExtra("users");
            if (mUserList!=null){
                mUserList.addAll(mUserList);
            }
            if (adapter!=null){
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.ubt_tv_set_net_skip){
            doTransferAdmin();
        }
    }

    /**
     * 执行转让权限操作
     */
    private void doTransferAdmin(){
        if (adapter!=null&&mUserList!=null
                &&adapter.getSelectedIndex()>=0
                &&mUserList.size()>adapter.getSelectedIndex()){
            final String userId=String.valueOf(mUserList.get(adapter.getSelectedIndex()).getUserId());
            TransferAdminHttpProxy httpProxy=new TransferAdminHttpProxy();
            httpProxy.transferAdmin(CookieInterceptor.get().getToken(), AuthLive.getInstance().getCurrentPig().getRobotName(), userId, new TransferAdminHttpProxy.TransferCallback() {
                @Override
                public void onError(String error) {

                }

                @Override
                public void onException(Exception e) {

                }

                @Override
                public void onSuccess(String msg) {

                }
            });
        }
    }
}

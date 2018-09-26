package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.adpater.MemberPermissionAdapter;

import java.util.ArrayList;

public class TransferAdminActivity extends BaseToolBarActivity {
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
        getUserList(getIntent());
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getUserList(intent);
    }

    private void initViews(){
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
            if (adapter!=null){
                adapter.notifyDataSetChanged();
            }
        }
    }


}

package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.view.View;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.personal.management.AddressBookActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.MyPigActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceManageActivity extends BaseNewActivity {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_device_manage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_titlebar.setTitleText(getString(R.string.device_manage));
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_my_pig, R.id.rl_pairing, R.id.rl_member_group, R.id.rl_addressbook})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_my_pig:
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, MyPigActivity
                        .class, false);
                break;
            case R.id.rl_pairing:
                break;
            case R.id.rl_member_group:
                break;
            case R.id.rl_addressbook:
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, AddressBookActivity
                        .class, false);
                break;
        }
    }
}

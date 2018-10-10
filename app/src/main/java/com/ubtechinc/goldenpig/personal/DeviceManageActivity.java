package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.personal.management.AddressBookActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.MyPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PigMemberActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceManageActivity extends BaseNewActivity {

    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;

    @BindView(R.id.ubt_tv_member_group)
    TextView memberItemTitle;

    @BindView(R.id.ubt_tv_member_subtitle)
    TextView memberItemSubTitle;

    @BindView(R.id.rl_my_pig)
    View rlMyPig;

    @BindView(R.id.rl_pairing)
    View rlPairing;

    @BindView(R.id.rl_member_group)
    View rlMemberGroup;

    @BindView(R.id.rl_addressbook)
    View rlAddressbook;

    private PigInfo mPig;

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

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        mPig = AuthLive.getInstance().getCurrentPig();
        if (mPig != null) {
            if (mPig.isAdmin) {
                rlMyPig.setAlpha(1.0f);
                rlPairing.setAlpha(1.0f);
                rlMemberGroup.setAlpha(1.0f);
                rlAddressbook.setAlpha(1.0f);
                memberItemTitle.setText(R.string.member_group);
                memberItemSubTitle.setVisibility(View.GONE);

                rlMyPig.setEnabled(true);
                rlPairing.setEnabled(true);
                rlMemberGroup.setEnabled(true);
                rlAddressbook.setEnabled(true);
            } else {
                rlMyPig.setAlpha(1.0f);
                rlPairing.setAlpha(0.5f);
                rlMemberGroup.setAlpha(1.0f);
                rlAddressbook.setAlpha(0.5f);
                memberItemTitle.setText(R.string.member_group);
                memberItemSubTitle.setVisibility(View.GONE);

                rlMyPig.setEnabled(true);
                rlPairing.setEnabled(false);
                rlMemberGroup.setEnabled(true);
                rlAddressbook.setEnabled(false);
            }
        } else {
            rlMyPig.setAlpha(0.5f);
            rlPairing.setAlpha(0.5f);
            rlMemberGroup.setAlpha(1.0f);
            rlAddressbook.setAlpha(0.5f);
            memberItemTitle.setText(R.string.ubt_join_group);
            memberItemSubTitle.setVisibility(View.VISIBLE);
            memberItemSubTitle.setText(R.string.ubt_san_formember);

            rlMyPig.setEnabled(false);
            rlPairing.setEnabled(false);
            rlMemberGroup.setEnabled(true);
            rlAddressbook.setEnabled(false);
        }
    }

    @OnClick({R.id.rl_my_pig, R.id.rl_pairing, R.id.rl_member_group, R.id.rl_addressbook})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_my_pig:
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, MyPigActivity
                        .class, false);
                break;
            case R.id.rl_pairing:

                HashMap<String, Boolean> param = new HashMap<>();
                param.put("isPair", true);
                ActivityRoute.toAnotherActivity(this, QRCodeActivity.class, param, false);

                break;
            case R.id.rl_member_group:
                if (AuthLive.getInstance().getCurrentPig() == null) {

                    ActivityRoute.toAnotherActivity(this, MemberQRScannerActivity.class, false);
                } else {
                    ActivityRoute.toAnotherActivity(this, PigMemberActivity.class, false);
                }
                break;
            case R.id.rl_addressbook:
                ActivityRoute.toAnotherActivity(DeviceManageActivity.this, AddressBookActivity
                        .class, false);
                break;
                default:
        }
    }

}

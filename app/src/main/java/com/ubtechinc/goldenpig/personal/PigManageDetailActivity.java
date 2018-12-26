package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.personal.management.AddressBookActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.MyPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.nets.http.ThrowableWrapper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

public class PigManageDetailActivity extends BaseNewActivity implements View.OnClickListener {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_device_manage_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        rl_titlebar.setTitleText("八戒详情");
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EventBusUtil.register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.rl_wifi, R.id.rl_4g, R.id.rl_hotpoint, R.id.rl_continuity_voice, R.id.rl_member_group, R.id
            .rl_about, R.id.rl_update})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_wifi:
//                ActivityRoute.toAnotherActivity(PigManageDetailActivity.this, MyPigActivity
//                        .class, false);
                break;
            case R.id.rl_4g:
                break;
            case R.id.rl_hotpoint:
                break;
            case R.id.rl_continuity_voice:
                break;
            case R.id.rl_member_group:
                break;
            case R.id.rl_about:
                break;
            case R.id.rl_update:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case USER_PIG_UPDATE:
                break;
        }
    }


}

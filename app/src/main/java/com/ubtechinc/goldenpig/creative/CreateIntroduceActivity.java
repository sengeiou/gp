package com.ubtechinc.goldenpig.creative;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CREATEINTRODUCE;


public class CreateIntroduceActivity extends BaseNewActivity {

    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.ll_add)
    LinearLayout ll_add;
    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.tv_add)
    TextView tv_add;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UBTPGApplication.getInstance().HASCREATE=true;
        int type = getIntent().getIntExtra("type", 0);
        rl_titlebar.setTitleText(R.string.create_message);
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (isAdmin()) {
            iv_add.setImageResource(R.drawable.ic_add);
            tv_add.setTextColor(getResources().getColor(R.color.pic_remoind_main_color));
        } else {
            iv_add.setImageResource(R.drawable.ic_add2);
            tv_add.setTextColor(getResources().getColor(R.color.create_color));
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_introduce;
    }

    @OnClick(R.id.ll_add)
    void tv_add() {
        if (isAdmin()) {
            Intent it = new Intent(this, AddCreateActivity.class);
            startActivity(it);
            //finish();
        }
    }

    private boolean isAdmin(){

        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if(pigInfo != null && pigInfo.isAdmin){
            return true;
        }else{
            return false;
        }
    }


    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == CREATEINTRODUCE) {
            finish();
        }
    }
}

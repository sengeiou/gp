package com.ubtechinc.goldenpig.personal.management;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import butterknife.BindView;
import butterknife.OnClick;

public class AddAndSetContactActivity extends BaseNewActivity {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_clear2)
    ImageView ivClear2;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    /**
     * 0为添加
     * 1为编辑
     */
    public int type = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_set_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case 0:
                rl_titlebar.setTitleText("添加联系人");
                break;
            case 1:
                rl_titlebar.setTitleText("编辑联系人");
                break;
        }

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
                break;
            case R.id.rl_pairing:
                break;
            case R.id.rl_member_group:
                break;
            case R.id.rl_addressbook:
                ActivityRoute.toAnotherActivity(AddAndSetContactActivity.this, AddressBookActivity
                        .class, false);
                break;
        }
    }
}

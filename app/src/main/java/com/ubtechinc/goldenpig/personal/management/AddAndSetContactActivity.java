package com.ubtechinc.goldenpig.personal.management;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;


import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_CONTACT_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.sendEvent;

public class AddAndSetContactActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.iv_phone_clear)
    ImageView ivPhoneClear;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_name_clear)
    ImageView ivNameClear;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    /**
     * 0为添加
     * 1为编辑
     */
    private int type = 0;
    private List<String> mList;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private String strPhone;
    private String strName;
    private ArrayList<AddressBookmodel> oldList;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_set_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                ToastUtils.showShortToast(s);
                LoadingDialog.getInstance(AddAndSetContactActivity.this).dismiss();
            }

            @Override
            public void onSuccess() {

            }
        });
        type = getIntent().getIntExtra("type", 0);
        strPhone = getIntent().getStringExtra("phone");
        strName = getIntent().getStringExtra("name");
        oldList = getIntent().getParcelableArrayListExtra("list");
        if (oldList == null) {
            oldList = new ArrayList<>();
        }
        switch (type) {
            case 0:
                rl_titlebar.setTitleText(getString(R.string.add_contact));
                break;
            case 1:
                rl_titlebar.setTitleText(getString(R.string.set_contact));
                break;
        }
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strName)) {
                    ToastUtils.showShortToast("电话或昵称不能为空");
                    return;
                }
                if (!checkOldList()) {
                    return;
                }
                LoadingDialog.getInstance(AddAndSetContactActivity.this).show();
                addUser(strName, strPhone);
//                ToastUtils.showShortToast("添加或编辑完联系人");
//                Event<String> event = new Event<>(ADD_CONTACT_SUCCESS);
//                sendEvent(event);
//                finish();
            }
        });
        rl_titlebar.setTvRightName(getString(R.string.complete));
        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                .ubt_tab_btn_txt_checked_color));
        mList = new ArrayList<>();
        initData();
        GridLayoutManager gm = new GridLayoutManager(this, 5);
        gm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(gm);
        int itemSpace = (int) ((ScreenUtils.getScreenWidth() - 2 * getResources().getDimension(R
                .dimen
                .dp_25) - 5 * getResources().getDimension(R.dimen.dp_55)) / 4);
        GridSpacingItemDecoration de = new GridSpacingItemDecoration(5, itemSpace, (int)
                getResources()
                        .getDimension(R.dimen.dp_17), false);
        recycler.addItemDecoration(de);
        recycler.setAdapter(adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout
                .adapter_contact, mList) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                etName.setText(mList.get(position));
                etName.setSelection(mList.get(position).length());
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strPhone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(strPhone)) {
                    ivPhoneClear.setVisibility(View.INVISIBLE);
                } else {
                    ivPhoneClear.setVisibility(View.VISIBLE);
                }
            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strName = etName.getText().toString().trim();
                if (TextUtils.isEmpty(strName)) {
                    ivNameClear.setVisibility(View.INVISIBLE);
                } else {
                    ivNameClear.setVisibility(View.VISIBLE);
                }
            }
        });
        switch (type) {
            case 1:
                if (!TextUtils.isEmpty(strPhone))
                    etPhone.setText(strPhone);
                if (!TextUtils.isEmpty(strName))
                    etName.setText(strName);
                break;
        }
    }

    @OnClick({R.id.iv_phone_clear, R.id.iv_name_clear, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_phone_clear:
                etPhone.setText("");
                break;
            case R.id.iv_name_clear:
                break;
            case R.id.iv_add:
                if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strName)) {
                    ToastUtils.showShortToast("电话或昵称不能为空");
                    break;
                }
                if (!checkOldList()) {
                    break;
                }
                ToastUtils.showShortToast("添加或编辑完联系人");
                Event<String> event = new Event<>(ADD_CONTACT_SUCCESS);
                sendEvent(event);
                finish();
                break;
        }
    }

    public void initData() {
        mList.clear();
        mList.add("爸爸");
        mList.add("妈妈");
        mList.add("爷爷");
        mList.add("奶奶");
        mList.add("外公");
        mList.add("外婆");
        mList.add("儿子");
        mList.add("女儿");
        mList.add("弟弟");
        mList.add("哥哥");
    }

    private boolean checkOldList() {
        for (int i = 0; i < oldList.size(); i++) {
            if (oldList.get(i).phone.equals(strPhone)) {
                ToastUtils.showShortToast("手机号已存在,请重新填写");
                return false;
            }
            if (oldList.get(i).name.equals(strName)) {
                ToastUtils.showShortToast("昵称已存在,请重新填写");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        LogUtils.d("dsadsadaa");
        LoadingDialog.getInstance(AddAndSetContactActivity.this).dismiss();
    }

    private void addUser(String nikName, String number) {
        UbtTIMManager.getInstance().addUser(nikName, number);
    }
}

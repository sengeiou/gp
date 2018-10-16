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
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.view.GridSpacingItemDecoration;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CONTACT_CHECK_SUCCESS;

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
    private int updatePosition = -1;
    private int curPosition = -1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_set_contact;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        } else {
            UbtTIMManager.getInstance().setPigAccount("2cb9b9a3");
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                if (AuthLive.getInstance().getCurrentPig()!=null) {
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("小猪未登录");
                }else{
                    com.ubtech.utilcode.utils.ToastUtils.showShortToast("未绑定小猪");
                }
                LoadingDialog.getInstance(AddAndSetContactActivity.this).dismiss();
            }

            @Override
            public void onSuccess() {

            }
        });
        type = getIntent().getIntExtra("type", 0);
        updatePosition = getIntent().getIntExtra("position", -1);
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
                default:
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
                try {
                    if (type == 0) {
                        UbtTIMManager.getInstance().addUser(strName, strPhone);
                    } else {
                        UbtTIMManager.getInstance().updateUser(strName, strPhone, oldList.get
                                (updatePosition).id + "");
                    }
                    LoadingDialog.getInstance(AddAndSetContactActivity.this).setTimeout(20)
                            .setShowToast(true).show();
                } catch (Exception e) {
                    ToastUtils.showShortToast("请求异常，请重试");
                }
            }
        });
        rl_titlebar.setTvRightName(getString(R.string.complete));
        rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                .ubt_skip_txt_unenable_color));
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

                //TODO
                if (curPosition >= 0 && curPosition < mList.size()) {
                    String curText = mList.get(curPosition);
                    if (curText.equals(item)) {
                        helper.setBackgroundRes(R.id.tv_name, R.drawable.blue_round_frame);
                    } else {
                        helper.setBackgroundRes(R.id.tv_name, R.drawable.gray_round_frame);
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                etName.setText(mList.get(position));
                etName.setSelection(mList.get(position).length());

                //refresh
                curPosition = position;
                adapter.notifyDataSetChanged();
//                adapter.notifyItemChanged(position);
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
                    rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                            .ubt_skip_txt_unenable_color));
                } else {
                    ivPhoneClear.setVisibility(View.VISIBLE);
                    rl_titlebar.getTvRight().setTextColor(getResources().getColor(R.color
                            .ubt_tab_btn_txt_checked_color));
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
        if (updatePosition >= 0 && updatePosition < oldList.size()) {
            strPhone = oldList.get(updatePosition).phone;
            strName = oldList.get(updatePosition).name;
        }
        switch (type) {
            case 1:
                if (!TextUtils.isEmpty(strPhone)) {
                    etPhone.setText(strPhone);
                    try {
                        etPhone.setSelection(strPhone.length());
                    } catch (Exception e) {
                    }
                }
                if (!TextUtils.isEmpty(strName)) {
                    etName.setText(strName);
                }
                break;
                default:
        }
    }

    @OnClick({R.id.iv_phone_clear, R.id.iv_name_clear, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_phone_clear:
                etPhone.setText("");
                break;
            case R.id.iv_name_clear:
                etName.setText("");
                break;
            case R.id.iv_add:
                if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strName)) {
                    ToastUtils.showShortToast("电话或昵称不能为空");
                    break;
                }
                if (!checkOldList()) {
                    break;
                }
                try {
                    if (type == 0) {
                        UbtTIMManager.getInstance().addUser(strName, strPhone);
                    } else {
                        UbtTIMManager.getInstance().updateUser(strName, strPhone, oldList.get
                                (updatePosition).id + "");
                    }
                    LoadingDialog.getInstance(AddAndSetContactActivity.this).setTimeout(20)
                            .setShowToast(true).show();
                } catch (Exception e) {
                    ToastUtils.showShortToast("请求异常，请重试");
                }
                break;
                default:
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
            if (type == 1 && i == updatePosition) {
                continue;
            }
            if (oldList.get(i).phone.equals(strPhone)) {
                ToastUtils.showShortToast("存在重复号码");
                return false;
            }
            if (oldList.get(i).name.equals(strName)) {
                ToastUtils.showShortToast("存在重复昵称");
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
        TIMMessage msg = (TIMMessage) arg;
        for (int i = 0; i < msg.getElementCount(); ++i) {
            TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
            try {
                dealMsg(elem.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                ToastUtils.showShortToast("数据异常，请重试");
            }
        }
    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        switch (action) {
            case "/im/mail/query":
                break;
            case "/im/mail/add":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                if (flag) {
                    ToastUtils.showShortToast("添加成功");
                    EventBusUtil.sendEvent(new Event<String>(CONTACT_CHECK_SUCCESS));
                    finish();
                } else {
                    ToastUtils.showShortToast("请求异常，请重试");
                }
                break;

            case "/im/mail/delete":
                break;
            case "/im/mail/update":
                Boolean flag2 = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                if (flag2) {
                    ToastUtils.showShortToast("编辑成功");
                    EventBusUtil.sendEvent(new Event<String>(CONTACT_CHECK_SUCCESS));
                    finish();
                } else {
                    ToastUtils.showShortToast("请求异常，请重试");
                }
                break;
                default:
        }
    }
}

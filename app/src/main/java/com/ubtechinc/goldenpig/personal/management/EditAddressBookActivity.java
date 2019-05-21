package com.ubtechinc.goldenpig.personal.management;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.AddressBook;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserContacts;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.DialogUtil;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_DELETE_CONTACTS;
import static com.ubtechinc.goldenpig.personal.management.AddressBookActivity.MAXADD;
import static com.ubtechinc.goldenpig.utils.CommendUtil.TIMEOUT;

public class EditAddressBookActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    EditAddressBookAdapter adapter;
    private ArrayList<AddressBookmodel> mList;
    public int deletePosition = 0;
    /**
     * 先拉取到数据，添加联系人时要在app端作对比后再提交给音箱
     */
    private Boolean hasLoadMsg = false;

    public Boolean noCard = false;
    public Boolean hasSelect = false;
    public String pigPhoneNumber = "";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_address_book;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList list = getIntent().getParcelableArrayListExtra("list");
        noCard = getIntent().getBooleanExtra("noCard", false);
        pigPhoneNumber = getIntent().getStringExtra("pigPhoneNumber");
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (list != null) {
            AddressBookmodel ab2 = new AddressBookmodel();
            ab2.noCard = noCard;
            ab2.phone = pigPhoneNumber;
            ab2.type = 2;
            mList.add(ab2);
            mList.addAll(list);
        }
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasSelect) {
                    showDeleteDialog();
                }
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        adapter = new EditAddressBookAdapter(this, mList, new EditAddressBookAdapter.EditABListener() {
            @Override
            public void clearAll() {
                hasSelect = true;
                tv_left.setTextColor(getResources().getColor(R.color.gold_red_color));
                tv_left.setText("清空");
            }

            @Override
            public void delete() {
                hasSelect = true;
                tv_left.setTextColor(getResources().getColor(R.color.gold_red_color));
                tv_left.setText("删除");
            }

            @Override
            public void nothing() {
                hasSelect = false;
                tv_left.setTextColor(getResources().getColor(R.color.empty_color));
                tv_left.setText("删除");
            }
        });
        recycler.setAdapter(adapter);
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                try {
                    LoadingDialog.getInstance(EditAddressBookActivity.this).dismiss();
                    if (AuthLive.getInstance().getCurrentPig() != null) {
                        UbtToastUtils.showCustomToast(getApplication(), "八戒未登录");
                    } else {
                        UbtToastUtils.showCustomToast(getApplication(), "未绑定八戒");
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver", "sss");
            }
        });
    }

    public void onRefreshSuccess(List<AddressBookmodel> list) {
        hasLoadMsg = true;
        //refreshLayout.finishRefresh(true);
        mList.clear();
        //mList.addAll(list);
        if (list.size() == 0) {
            mStateView.showEmpty();
            tv_right.setVisibility(View.GONE);
        } else if (mList.size() >= MAXADD) {
            AddressBookmodel ab2 = new AddressBookmodel();
            ab2.type = 2;
            mList.add(ab2);
            mList.addAll(list);
            AddressBookmodel ab = new AddressBookmodel();
            ab.type = 1;
            mList.add(ab);
            mStateView.showContent();
            tv_right.setVisibility(View.VISIBLE);
        } else {
            AddressBookmodel ab2 = new AddressBookmodel();
            ab2.type = 2;
            mList.add(ab2);
            mList.addAll(list);
            mStateView.showContent();
            tv_right.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTitlebarRightIcon(boolean highlight) {
    }


    public void onError(String str) {
        hasLoadMsg = false;
        UbtToastUtils.showCustomToast(getApplication(), str);
        if (mList.size() == 0) {
            mStateView.showRetry();
        } else {
            mStateView.showContent();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        TIMMessage msg = (TIMMessage) arg;
        try {
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMElem tIMElem = msg.getElement(i);
                if (tIMElem != null && tIMElem instanceof TIMCustomElem) {
                    TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
                    dealMsg(elem.getData());
                }
            }
        } catch (Exception e) {
            UbtToastUtils.showCustomToast(getApplication(), getString(R.string.msg_error_toast));
            mStateView.showRetry();
        }

    }

    /* <call path="/im/mail/add"/>
    <call path="/im/mail/query"/>
    <call path="/im/mail/delete"/>
    <call path="/im/mail/update"/>*/
    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        switch (action) {
            case "/im/mail/query":
                List<UserContacts.User> list = msg.getPayload().unpack(UserContacts.UserContact
                        .class).getUserList();
                List<AddressBookmodel> ss = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    AddressBookmodel mo = new AddressBookmodel();
                    mo.name = list.get(j).getName();
                    mo.phone = list.get(j).getNumber();
                    mo.id = list.get(j).getId();
                    ss.add(mo);
                }
                onRefreshSuccess(ss);
                break;
            case "/im/mail/add":
                msg.getPayload().unpack(GPResponse.Response.class).getResult();
                break;

            case "/im/mail/delete":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                LoadingDialog.getInstance(EditAddressBookActivity.this).dismiss();
                if (flag) {
                    List<AddressBookmodel> list1 = new ArrayList<>();
                    for (int i = 1; i < mList.size(); i++) {
                        if (TextUtils.isEmpty(mList.get(i).phone)) {
                            continue;
                        }
                        if (mList.get(i).select) {
                            list1.add(mList.get(i));
                        }
                    }
                    mList.removeAll(list1);
                    mList.get(0).selectAll = false;
                    hasSelect = false;
                    tv_left.setTextColor(getResources().getColor(R.color.empty_color));
                    tv_left.setText("删除");
                    adapter.notifyDataSetChanged();
                    Event<List<AddressBookmodel>> event = new Event<>(RECEIVE_DELETE_CONTACTS);
                    event.setData(list1);
                    EventBusUtil.sendEvent(event);
                    if (mList.size() <= 1) {
                        finish();
                    }
                } else {
                    UbtToastUtils.showCustomToast(getApplication(), "删除失败，请重试");
                }
                break;
            case "/im/mail/update":
                msg.getPayload().unpack(GPResponse.Response.class).getResult();
                break;
            default:
        }
    }

    private Dialog picDialog;

    private View picView;

    public void showDeleteDialog() {
        if (picDialog == null) {
            picView = LayoutInflater.from(this).inflate(
                    R.layout.dialog_view_eab_bottom2, null);
            picDialog = DialogUtil.getMenuDialog(this, picView);
        }
        picDialog.show();
        final TextView tv_delete = (TextView) picView.findViewById(R.id.tv_delete);
        if (mList.get(0).selectAll) {
            tv_delete.setText("清空所有联系人");
        } else {
            tv_delete.setText("删除所选联系人");
        }
        tv_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//
                picDialog.dismiss();
                List<AddressBook> list = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (TextUtils.isEmpty(mList.get(i).phone) || TextUtils.isEmpty(mList.get(i).name)) {
                        continue;
                    }
                    if (mList.get(i).select) {
                        AddressBook book = new AddressBook();
                        book.nikeName = mList.get(i).name;
                        book.number = mList.get(i).phone;
                        book.userId = mList.get(i).id + "";
                        list.add(book);
                    }
                }
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    UbtToastUtils.showCustomToast(getApplication(), getString(R.string.network_error));
                    return;
                }
                UbtTIMManager.getInstance().deleteUser(list);
                LoadingDialog.getInstance(EditAddressBookActivity.this).setTimeout(TIMEOUT)
                        .setShowToast(true).setToastTye(1).show();
            }
        });
        TextView tv_cancel = (TextView) picView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                picDialog.dismiss();
            }
        });
    }
}

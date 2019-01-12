package com.ubtechinc.goldenpig.pigmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserContacts;
import com.ubt.improtolib.UserRecords;
import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;
import com.ubtechinc.goldenpig.utils.CommendUtil;
import com.ubtechinc.goldenpig.utils.DialogUtil;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.StateView;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DELETE_RECORD_SUCCESS;
import static com.ubtechinc.goldenpig.utils.CommendUtil.TIMEOUT;

public class EditRecordActivity extends BaseNewActivity implements Observer {
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.tv_left)
    TextView tvDelete;
    @BindView(R.id.iv_select_all)
    ImageView iv_select_all;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    BaseQuickAdapter<RecordModel, BaseViewHolder> adapter;
    private ArrayList<RecordModel> mList, allList;
    private Boolean isSelectAll = false;
    private MyHandler mHandler;

    private class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mWeakReference.get() != null) {
                    ToastUtils.showShortToast(mWeakReference.get().getString(R.string.timeout_error_toast));
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_record;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = getIntent().getParcelableArrayListExtra("list");
        allList = getIntent().getParcelableArrayListExtra("allList");
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (allList == null) {
            allList = new ArrayList<>();
        }
        mHandler = new MyHandler(this);
        initStateView(true);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                onRefresh();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        Divider divider = new Divider(new ColorDrawable(getResources().getColor(R.color
                .ubt_wifi_list_divider)),
                OrientationHelper.VERTICAL);
        divider.setHeight((int) getResources().getDimension(R.dimen.ubt_1px));
        recycler.addItemDecoration(divider);
        adapter = new BaseQuickAdapter<RecordModel, BaseViewHolder>(R.layout.adapter_edit_record,
                mList) {
            @Override
            protected void convert(BaseViewHolder helper, RecordModel item) {
                helper.setImageResource(R.id.iv_select, item.select ? R.drawable.ic_select : R
                        .drawable.ic_noselect);
                helper.setVisible(R.id.iv_type, item.type == 2 ? true : false);
                if (item.type == 3) {
                    helper.setTextColor(R.id.tv_content, mContext.getResources().getColor(R.color
                            .ubt_dialog_btn_txt_color));
                    helper.setTextColor(R.id.tv_count, mContext.getResources().getColor(R.color
                            .ubt_dialog_btn_txt_color));
                } else {
                    helper.setTextColor(R.id.tv_content, mContext.getResources().getColor(R.color.ubt_tips_txt_color));
                    helper.setTextColor(R.id.tv_count, mContext.getResources().getColor(R.color.ubt_tips_txt_color));
                }
                helper.setText(R.id.tv_content, !TextUtils.isEmpty(item.name) ? item.name : (!TextUtils.isEmpty(item
                        .number) ? item.number : "未知号码"));
                helper.setText(R.id.tv_count, "(" + item.count + ")");
                helper.setVisible(R.id.tv_count, item.count > 1 ? true : false);
                helper.setText(R.id.tv_date, TimeUtils.format(TimeUtils.millis2Date(item
                        .dateLong)));
            }
        };
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mList.get(position).select = !mList.get(position).select;
                adapter.notifyDataSetChanged();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).select) {
                        tvDelete.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
                        tvDelete.setEnabled(true);
                        break;
                    }
                    if (i == mList.size() - 1) {
                        tvDelete.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                        tvDelete.setEnabled(false);
                        break;
                    }
                }
                for (int i = 0; i < mList.size(); i++) {
                    if (!mList.get(i).select) {
                        isSelectAll = false;
                        iv_select_all.setImageResource(R.drawable.ic_noselect);
                        break;
                    }
                    if (i == mList.size() - 1) {
                        isSelectAll = true;
                        iv_select_all.setImageResource(R.drawable.ic_select);
                    }
                }
            }
        });
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            UbtTIMManager.getInstance().setPigAccount(pigInfo.getRobotName());
        }
        UbtTIMManager.getInstance().setMsgObserve(this);
        UbtTIMManager.getInstance().setOnUbtTIMConverListener(new OnUbtTIMConverListener() {
            @Override
            public void onError(int i, String s) {
                Log.e("setOnUbtTIMConver", s);
                LoadingDialog.getInstance(EditRecordActivity.this).dismiss();
                ToastUtils.showShortToast(s);
            }

            @Override
            public void onSuccess() {
                Log.e("setOnUbtTIMConver", "sss");
            }
        });
        tvDelete.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
        tvDelete.setEnabled(false);
    }

    @OnClick({R.id.tv_right, R.id.ll_select_all, R.id.tv_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                Boolean flag = false;
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).select) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    showDeleteDialog();
                } else {
                    ToastUtils.showShortToast("请先选择要删除的最近通话");
                }
                break;
            case R.id.tv_right:
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).select = false;
                }
                Event<List<RecordModel>> event = new Event<List<RecordModel>>(EventBusUtil.EDIT_RECORD_CALLBACK);
                event.setData(mList);
                EventBusUtil.sendEvent(event);
                finish();
                break;
            case R.id.ll_select_all:
                isSelectAll = !isSelectAll;
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).select = isSelectAll;
                }
                adapter.notifyDataSetChanged();
                if (isSelectAll) {
                    iv_select_all.setImageResource(R.drawable.ic_select);
                    tvDelete.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
                    tvDelete.setEnabled(true);
                } else {
                    iv_select_all.setImageResource(R.drawable.ic_noselect);
                    tvDelete.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                    tvDelete.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).select = false;
        }
        Event<List<RecordModel>> event = new Event<List<RecordModel>>(EventBusUtil.EDIT_RECORD_CALLBACK);
        event.setData(mList);
        EventBusUtil.sendEvent(event);
        super.onBackPressed();
    }

    public void onRefresh() {
        if (mHandler.hasMessages(1)) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessageDelayed(1, TIMEOUT * 1000);// 20s 秒后检查加载框是否还在
        UbtTIMManager.getInstance().queryRecord();
        LoadingDialog.getInstance(this).setTimeout(TIMEOUT).setShowToast(true).show();
    }

    public void onError(String str) {
        ToastUtils.showShortToast(str);
        if (mList.size() == 0) {
            mStateView.showRetry();
        } else {
            mStateView.showContent();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        UbtTIMManager.getInstance().deleteMsgObserve(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        TIMMessage msg = (TIMMessage) arg;
        for (int i = 0; i < msg.getElementCount(); ++i) {
            TIMCustomElem elem = (TIMCustomElem) msg.getElement(i);
            try {
                dealMsg(elem.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                ToastUtils.showShortToast(getString(R.string.msg_error_toast));
            }
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
            case "/im/record/query":
                List<UserRecords.Record> list = msg.getPayload().unpack(UserRecords.UserRecord
                        .class).getRecordList();
                List<RecordModel> ss = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    RecordModel mo = new RecordModel();
                    mo.name = list.get(j).getName();
                    mo.number = list.get(j).getNumber();
                    mo.id = list.get(j).getId();
                    mo.type = list.get(j).getType();
                    mo.dateLong = list.get(j).getDateLong();
                    mo.duration = list.get(j).getDuration();
                    ss.add(mo);
                }
                break;
            case "/im/record/delete":
                Boolean flag = msg.getPayload().unpack(GPResponse.Response.class).getResult();
                LoadingDialog.getInstance(EditRecordActivity.this).dismiss();
                if (flag) {
//                    Event<String> event = new Event<>(DELETE_RECORD_SUCCESS);
//                    EventBusUtil.sendEvent(event);
                    ToastUtils.showShortToast("删除最近通话成功");
                    List<RecordModel> listnew = new ArrayList<>();
                    for (int j = 0; j < mList.size(); j++) {
                        if (!mList.get(j).select) {
                            listnew.add(mList.get(j));
                        }
                    }
                    mList.clear();
                    mList.addAll(listnew);
                    adapter.notifyDataSetChanged();
                    if (mList.size() == 0) {
                        mStateView.showEmpty();
                        mStateView.setEmptyViewMSG("无最近通话");
                    } else {
                        mStateView.showContent();
                    }
                    tvDelete.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                    tvDelete.setEnabled(false);
                } else {
                    ToastUtils.showShortToast("删除失败，请重试");
                }
                break;
        }
    }

    /** */
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
        tv_delete.setText("删除所选最近通话");
        tv_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//
                picDialog.dismiss();
                List<UserRecords.Record> list = new ArrayList();
                for (int i = 0; i < mList.size(); i++) {
                    for (int j = 0; j < allList.size(); j++) {
                        if (allList.get(i).number.equals(mList.get(i).number)
                                && allList.get(i).type == mList.get(i).type
                                && CommendUtil.isSameDayOfMillis(allList.get(i).dateLong, mList.get(i).dateLong)) {
                            UserRecords.Record.Builder recordBuild = UserRecords.Record.newBuilder();
                            recordBuild.setName(allList.get(i).name);
                            recordBuild.setNumber(allList.get(i).number);
                            recordBuild.setDateLong(allList.get(i).dateLong);
                            recordBuild.setDuration(allList.get(i).duration);
                            recordBuild.setType(allList.get(i).type);
                            recordBuild.setId(allList.get(i).id);
                            list.add(recordBuild.build());
                        }
                    }
                }
                UbtLogger.d("EditRecordActivity", "delete the list");
                UbtTIMManager.getInstance().deleteRecord(list);
                LoadingDialog.getInstance(EditRecordActivity.this).setTimeout(TIMEOUT)
                        .setShowToast(true).show();
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

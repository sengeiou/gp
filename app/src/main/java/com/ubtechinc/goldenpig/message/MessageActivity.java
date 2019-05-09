package com.ubtechinc.goldenpig.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.main.CommonWebActivity;
import com.ubtechinc.goldenpig.pigmanager.popup.PopupWindowList;
import com.ubtechinc.goldenpig.view.RecyclerItemClickListener;
import com.ubtechinc.goldenpig.view.RecyclerOnItemLongListener;
import com.ubtechinc.goldenpig.view.StateView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.READ_SYSTEM_MSG;
import static com.ubtechinc.goldenpig.main.CommonWebActivity.KEY_URL;


public class MessageActivity extends BaseNewActivity {
    private static final String TAG = MessageActivity.class.getSimpleName();
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.rl_msg)
    RelativeLayout rl_msg;
    @BindView(R.id.recycler_view)
    RecyclerView recycler;
    MessageAdapter adapter;
    private ArrayList<MessageModel> mList;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStateView(rl_msg, false);
        mStateView.setEmptyResource(R.layout.adapter_system_msg_empty);
        //mStateView.setRetryResource(R.layout.view_system_msg_retry);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                refresh();
                mStateView.showLoading();
            }
        });
        rl_titlebar.setTitleText(getString(R.string.system_message));
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        adapter = new MessageAdapter(this, mList, new RecyclerOnItemLongListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                showPopWindows(v, position);
                mList.get(position).select = 1;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(View v, int position) {
                if (mList.get(position).status.equals("0")) {
                    report(position);
                }

                if(!TextUtils.isEmpty(mList.get(position).url)){
                    Intent it = new Intent(MessageActivity.this, CommonWebActivity.class);
                    it.putExtra(KEY_URL, mList.get(position).url);
                    startActivity(it);
                }else{
                    ToastUtils.showShortToast("详情页链接为空");
                }







            }
        });
        recycler.setAdapter(adapter);
        refresh();
        mStateView.showLoading();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_system_msg;
    }

    public void refresh() {

        new MessageHttpProxy().getData(this, new MessageHttpProxy.GetMessageCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" + error);
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mStateView.showRetry();
                        }
                    });
                }


            }

            @Override
            public void onSuccess(List<MessageModel> data) {

                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mList.clear();
                            if(data == null){
                                mStateView.showEmpty();
                                return;
                            }

                            mList.addAll(data);
                            adapter.notifyDataSetChanged();

                            if (mList.size() > 0) {
                                mStateView.showContent();
                            } else {
                                mStateView.showEmpty();
                            }
                        }
                    });
                }

            }
        });


    }

    private void showPopWindows(View view, int deletePosition) {
        List<String> dataList = new ArrayList<>();
        dataList.add("删除该消息");
        PopupWindowList mPopupWindowList = new PopupWindowList(view.getContext());
        mPopupWindowList.setDissListener(new PopupWindowList.DissListener() {
            @Override
            public void onDissListener() {
                mList.get(deletePosition).select = 0;
                adapter.notifyItemChanged(deletePosition);
            }
        });
        mPopupWindowList.setAnchorView(view);
        mPopupWindowList.setItemData(dataList);
        mPopupWindowList.setModal(true);
        mPopupWindowList.show();
        mPopupWindowList.setOnItemClickListener(new RecyclerItemClickListener(this) {
            @Override
            protected void onItemClick(View view, int position) {
                mPopupWindowList.hide();
                deleteMsg(deletePosition);
            }
        });
    }

    private void deleteMsg(int deletePosition) {


        LoadingDialog.getInstance(this).show();
        new MessageHttpProxy().deleteMessage(mList.get(deletePosition).id, new MessageHttpProxy.DeleteMessageCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" + error);
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(MessageActivity.this).dismiss();
                            ToastUtils.showShortToast("操作失败，请稍后重试");
                        }
                    });

                }

            }

            @Override
            public void onSuccess() {
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(MessageActivity.this).dismiss();
                            mList.remove(deletePosition);
                            adapter.notifyDataSetChanged();
                            if (mList.size() > 0) {
                                mStateView.showContent();
                            } else {
                                mStateView.showEmpty();
                            }
                            sendReadMSG();
                        }
                    });
                }

            }
        });


    }

    private void report(int reportPosition) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", mList.get(reportPosition).id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new MessageHttpProxy().reportMessage(mList.get(reportPosition).id, new MessageHttpProxy.ReportMessageCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" + error);
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(MessageActivity.this).dismiss();
                        }
                    });
                }
            }

            @Override
            public void onSuccess() {
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(MessageActivity.this).dismiss();
                            mList.get(reportPosition).status = "1";
                            adapter.notifyDataSetChanged();
                            sendReadMSG();
                        }
                    });
                }
            }
        });

        /*ViseHttpUtil.getInstance().getPost(HttpEntity.REPORT_SYSTEM_MSG, this)
                .setJson(json)
                .request(new JsonCallback<String>(String.class) {
                    @Override
                    public void onDataSuccess(String response) {
                        LoadingDialog.dismiss(MessageActivity.this);
                        UbtLog.d(TAG, "onResponse:" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {
                                mList.get(reportPosition).status = "1";
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                        }
                        sendReadMSG();
                    }

                    @Override
                    public void onFail(int i, String s) {
                        super.onFail(i,s);
                        UbtLog.d(TAG, "onError:" + s);
                        LoadingDialog.dismiss(MessageActivity.this);
                    }
                });*/
    }

    public void sendReadMSG() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).status.equals("0")) {
                break;
            }
            if (i == mList.size() - 1) {
                Event<String> event = new Event<>(READ_SYSTEM_MSG);
                EventBusUtil.sendEvent(event);
            }
        }
    }
}

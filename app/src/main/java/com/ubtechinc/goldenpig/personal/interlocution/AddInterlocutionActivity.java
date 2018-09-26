package com.ubtechinc.goldenpig.personal.interlocution;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.InterlocutionItemModel;
import com.ubtechinc.goldenpig.model.JsonCallback;
import com.ubtechinc.goldenpig.view.Divider;
import com.ubtechinc.goldenpig.view.StateView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_INTERLO_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DELETE_RECORD_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ANSWER_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_QUESTTION_SUCCESS;

public class AddInterlocutionActivity extends BaseNewActivity {
    @BindView(R.id.ll_add_question)
    LinearLayout ll_add_question;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rl_layout)
    RelativeLayout rlLayout;
    @BindView(R.id.tv_add_question)
    TextView tvAddQuestion;
    @BindView(R.id.tv_question)
    TextView tvQuestion;
    @BindView(R.id.ll_question)
    LinearLayout llQuestion;
    @BindView(R.id.tv_add_answer)
    TextView tvAddAnswer;
    @BindView(R.id.ll_add_answer)
    LinearLayout llAddAnswer;
    @BindView(R.id.tv_answer)
    TextView tvAnswer;
    @BindView(R.id.ll_answer)
    LinearLayout llAnswer;
    public String strQuest, strAnswer;
    InterlocutionModel requestModel;
    InterlocutionItemModel model;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_interlocution;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = getIntent().getParcelableExtra("item");
        requestModel = new InterlocutionModel();
        if (model != null) {
            strQuest = model.vQueries.get(0).strQuery;
            ll_add_question.setVisibility(View.GONE);
            llQuestion.setVisibility(View.VISIBLE);
            tvQuestion.setText("“" + strQuest + "”");
            strAnswer = model.vAnswers.get(0).strText;
            llAddAnswer.setVisibility(View.GONE);
            llAnswer.setVisibility(View.VISIBLE);
            tvAnswer.setText("“" + strAnswer + "”");
        }
    }

    @OnClick({R.id.tv_left, R.id.tv_right, R.id.ll_add_question, R.id.ll_question, R.id
            .ll_add_answer, R.id.ll_answer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_right:
                if (TextUtils.isEmpty(strQuest)) {
                    ToastUtils.showShortToast("请先设置问句");
                    return;
                }
                if (TextUtils.isEmpty(strAnswer)) {
                    ToastUtils.showShortToast("请先设置回答");
                    return;
                }
                LoadingDialog.getInstance(this).show();
                if (model == null) {
                    requestModel.addInterlocutionRequest(strQuest, strAnswer, new
                            JsonCallback<String>(String.class) {
                                @Override
                                public void onSuccess(String reponse) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialog.getInstance(AddInterlocutionActivity.this)
                                                    .dismiss();
                                            ToastUtils.showShortToast("添加问答成功");
                                            Event<String> event = new Event(ADD_INTERLO_SUCCESS);
                                            EventBusUtil.sendEvent(event);
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(String str) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialog.getInstance(AddInterlocutionActivity.this)
                                                    .dismiss();
                                            ToastUtils.showShortToast(str);
                                        }
                                    });
                                }
                            });
                } else {
                    requestModel.updateInterlocutionRequest(strQuest, model.vQueries.get(0)
                            .strItemId, strAnswer, model.strDocId, new
                            JsonCallback<String>(String.class) {
                                @Override
                                public void onSuccess(String reponse) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialog.getInstance(AddInterlocutionActivity.this)
                                                    .dismiss();
                                            ToastUtils.showShortToast("修改问答成功");
                                            Event<String> event = new Event(ADD_INTERLO_SUCCESS);
                                            EventBusUtil.sendEvent(event);
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(String str) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialog.getInstance(AddInterlocutionActivity.this)
                                                    .dismiss();
                                            ToastUtils.showShortToast(str);
                                        }
                                    });
                                }
                            });
                }
                break;
            case R.id.ll_add_question:
                Intent it = new Intent(AddInterlocutionActivity.this, SetQuestOrAnswerActivity
                        .class);
                it.putExtra("str", strQuest);
                it.putExtra("type", 0);
                startActivity(it);
                break;
            case R.id.ll_question:
                Intent it2 = new Intent(AddInterlocutionActivity.this, SetQuestOrAnswerActivity
                        .class);
                it2.putExtra("str", strQuest);
                it2.putExtra("type", 0);
                startActivity(it2);
                break;
            case R.id.ll_add_answer:
                Intent it3 = new Intent(AddInterlocutionActivity.this, SetQuestOrAnswerActivity
                        .class);
                it3.putExtra("str", strAnswer);
                it3.putExtra("type", 1);
                startActivity(it3);
                break;
            case R.id.ll_answer:
                Intent it4 = new Intent(AddInterlocutionActivity.this, SetQuestOrAnswerActivity
                        .class);
                it4.putExtra("str", strAnswer);
                it4.putExtra("type", 1);
                startActivity(it4);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onReceiveEvent(Event event) {
        super.onReceiveEvent(event);
        if (event.getCode() == SET_QUESTTION_SUCCESS) {
            strQuest = (String) event.getData();
            ll_add_question.setVisibility(View.GONE);
            llQuestion.setVisibility(View.VISIBLE);
            tvQuestion.setText("“" + strQuest + "”");
        } else if (event.getCode() == SET_ANSWER_SUCCESS) {
            strAnswer = (String) event.getData();
            llAddAnswer.setVisibility(View.GONE);
            llAnswer.setVisibility(View.VISIBLE);
            tvAnswer.setText("“" + strAnswer + "”");
        }
    }
}

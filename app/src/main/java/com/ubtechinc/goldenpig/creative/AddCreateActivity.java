package com.ubtechinc.goldenpig.creative;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.CreateModel;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.EditTextUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_CREATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.CREATEINTRODUCE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SHOWCREATELIST;


public class AddCreateActivity extends BaseNewActivity {

    private static final String TAG = "AddCreateActivity";

    @BindView(R.id.et_setquest)
    EditText et_setquest;
    @BindView(R.id.tv_questcount)
    TextView tv_questcount;
    @BindView(R.id.et_answerset)
    EditText et_answerset;
    @BindView(R.id.tv_answercount)
    TextView tv_answercount;
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.tv_save)
    TextView tv_save;
    String strQuest, strAnswer;
    private int maxQuestLength = 20, maxAnswerLength = 100;
    private String limitToast = "";
    private int editPosition = -1;
    /**
     * 重新编辑时去掉红字判断
     */
    private boolean hasCheckNoise = false;

    private boolean isEditFlag;

    private Handler mHandler = new Handler();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_create;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strQuest = getIntent().getStringExtra("strQuest");
        strAnswer = getIntent().getStringExtra("strAnswer");
        editPosition = getIntent().getIntExtra("position", -1);
        if (editPosition != - 1) {
            isEditFlag = true;
            rl_titlebar.setTitleText("编辑问答");
        } else {
            isEditFlag = false;
            rl_titlebar.setTitleText("添加问答");
        }
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCache();
                finish();
            }
        });

        if (!TextUtils.isEmpty(strQuest)) {
            et_setquest.setText(strQuest);
            tv_questcount.setText(strQuest.length() + "/" + maxQuestLength);
            try {
                et_setquest.setSelection(strQuest.length());
            } catch (Exception e) {
            }
        }
        if (!TextUtils.isEmpty(strAnswer)) {
            et_answerset.setText(strAnswer);
            tv_answercount.setText(strAnswer.length() + "/" + maxAnswerLength);
            try {
                et_answerset.setSelection(strAnswer.length());
            } catch (Exception e) {
            }
        }
        initLengthLimit();
        if (TextUtils.isEmpty(strQuest) || TextUtils.isEmpty(strAnswer)) {
            tv_save.setBackgroundResource(R.drawable.shape_ubt_btn_gray);
            tv_save.setEnabled(false);
        } else {
            tv_save.setBackgroundResource(R.drawable.shape_ubt_btn_cyan_gb);
            tv_save.setEnabled(true);
        }
    }

    @OnClick({R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                strQuest = et_setquest.getText().toString();
                if (TextUtils.isEmpty(strQuest)) {
                    ToastUtils.showShortToast("请先设置问句");
                    return;
                }
                strAnswer = et_answerset.getText().toString();
                if (TextUtils.isEmpty(strAnswer)) {
                    ToastUtils.showShortToast("请先设置回复");
                    return;
                }
                hasCheckNoise = false;
                if (EditTextUtil.checkNoise(EditTextUtil.CREATE,strQuest, et_setquest)) {
                    hasCheckNoise = true;
                    return;
                }

                addCreate();
                break;
        }
    }

    public void initLengthLimit() {
        et_setquest.setFilters(EditTextUtil.getChinseInpF(20));
        et_setquest.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        strQuest = et_setquest.getText().toString();
                        try {
                            if (hasCheckNoise) {
                                hasCheckNoise = false;
                                int po = et_setquest.getSelectionStart();
                                et_setquest.setText(strQuest);
                                et_setquest.setSelection(po);
                            }
                        } catch (Exception e) {
                        }
                        if (TextUtils.isEmpty(strQuest)) {
                            tv_questcount.setText("0/" + maxQuestLength);
                        } else {
                            tv_questcount.setText(strQuest.length() + "/" + maxQuestLength);
                        }
                        if (TextUtils.isEmpty(strQuest) || TextUtils.isEmpty(strAnswer)) {
                            tv_save.setBackgroundResource(R.drawable.shape_ubt_btn_gray);
                            tv_save.setEnabled(false);
                        } else {
                            tv_save.setBackgroundResource(R.drawable.shape_ubt_btn_cyan_gb);
                            tv_save.setEnabled(true);
                        }
                    }
                }
        );
        et_answerset.setFilters(EditTextUtil.getOnlyInputType(0, 100));
        et_answerset.addTextChangedListener(
                new TextWatcher() {
                    private int selectionEnd;
                    private boolean delete = false;

                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        strAnswer = et_answerset.getText().toString();
                        if (TextUtils.isEmpty(strAnswer)) {
                            tv_answercount.setText("0/" + maxAnswerLength);
                        } else {
                            tv_answercount.setText(strAnswer.length() + "/" + maxAnswerLength);
                        }
                        if (TextUtils.isEmpty(strQuest) || TextUtils.isEmpty(strAnswer)) {
                            tv_save.setBackgroundResource(R.drawable.shape_ubt_btn_gray);
                            tv_save.setEnabled(false);
                        } else {
                            tv_save.setBackgroundResource(R.drawable.shape_ubt_btn_cyan_gb);
                            tv_save.setEnabled(true);
                        }
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addCreate() {


        LoadingDialog.getInstance(this).show();
        JSONObject json = new JSONObject();
        try {
            json.put("question", strQuest);
            json.put("answer", strAnswer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new CreativeSpaceHttpProxy().addCreativeContent(json, new CreativeSpaceHttpProxy.AddCreativeCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" +error);
                if(mHandler != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.getInstance(AddCreateActivity.this).dismiss();
                            ToastUtils.showShortToast(error);
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
                            LoadingDialog.getInstance(AddCreateActivity.this).dismiss();
                            ToastUtils.showShortToast("添加成功");
                            Event<Integer> event = new Event<>(ADD_CREATE);
                            if (editPosition >= 0) {
                                event.setData(editPosition);
                            } else {
                                event.setData(-1);
                            }
                            EventBusUtil.sendEvent(event);
                            if (!UBTPGApplication.createActivity) {
                                ActivityRoute.toAnotherActivity(AddCreateActivity.this, CreateActivity.class,
                                        false);
                                Event<String> event2 = new Event<>(CREATEINTRODUCE);
                                EventBusUtil.sendEvent(event2);
                            }
                            Event<String> event3 = new Event<>(SHOWCREATELIST);
                            EventBusUtil.sendEvent(event3);
                            finish();
                        }
                    });
                }


            }
        });


        //需要同步后台数据

      /*  LoadingDialog.show(AddCreateActivity.this);
        JSONObject json = new JSONObject();
        try {
            json.put("question", strQuest);
            json.put("answer", strAnswer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SCADAHelper.recordEvent(EVENET_APP_ZCQA_SUBMIT);
        ViseHttpUtil.getInstance().getPost(HttpEntity.ADD_CREATE_MSG, this)
                .setJson(json)
                .request(new JsonCallback<String>(String.class) {
                    @Override
                    public void onDataSuccess(String response) {
                        SCADAHelper.recordEvent(EVENET_APP_ZCQA_SUBMIT_SUCCESS);
                        LogUtils.d("AddCreateActivity", "onResponse:" + response);
                        LoadingDialog.dismiss(AddCreateActivity.this);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {
                                ToastUtils.showShortToast("添加成功");
                                Event<Integer> event = new Event<>(ADD_CREATE);
                                if (editPosition >= 0) {
                                    event.setData(editPosition);
                                    //deleteCache();
                                } else {
                                    event.setData(-1);
                                }
                                EventBusUtil.sendEvent(event);
                                if (!BaseApplication.createActivity) {
                                    ActivityRoute.toAnotherActivity(AddCreateActivity.this, CreateActivity.class,
                                            false);
                                    Event<String> event2 = new Event<>(CREATEINTRODUCE);
                                    sendEvent(event2);
                                }
                                Event<String> event3 = new Event<>(SHOWCREATELIST);
                                sendEvent(event3);
                                finish();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFail(int i, String s) {
                        super.onFail(i, s);
                        SCADAHelper.recordEvent(EVENET_APP_ZCQA_SUBMIT_FAILURE);
                        LogUtils.d("AddCreateActivity", "onError:" + s);
                        LoadingDialog.dismiss();
                    }
                });*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveCache();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void saveCache() {
        strQuest = et_setquest.getText().toString();
        strAnswer = et_answerset.getText().toString();
        try {
            if (!TextUtils.isEmpty(strQuest) || !TextUtils.isEmpty(strAnswer)) {
                CreateModel model = new CreateModel();
                model.question = TextUtils.isEmpty(strQuest) ? "" : strQuest;
                model.answer = TextUtils.isEmpty(strAnswer) ? "" : strAnswer;
                model.createTime = System.currentTimeMillis();
                if (isEditFlag) {
                    //TODO update draft
                    model.sid = editPosition;
                } else {
                    //TODO new draft
                    model.sid = -1;
                }
                if (CreateUtil.saveCreateDraft(model)) {
                    Event<CreateModel> event = new Event<>(EventBusUtil.SAVE_CREATE_CACHE);
                    event.setData(model);
                    EventBusUtil.sendEvent(event);
                }
            }
        } catch (Exception e) {

        }
    }

}

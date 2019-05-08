package com.ubtechinc.goldenpig.personal.interlocution;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.InterlocutionItemModel;
import com.ubtechinc.goldenpig.model.JsonCallback;
import com.ubtechinc.goldenpig.utils.EditTextUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.ADD_INTERLO_SUCCESS;


public class AddInterloctionNewActivity extends BaseNewActivity {
    @BindView(R.id.et_setquest)
    EditText et_setquest;
    @BindView(R.id.tv_questcount)
    TextView tv_questcount;
    @BindView(R.id.et_answerset)
    EditText et_answerset;
    @BindView(R.id.tv_answercount)
    TextView tv_answercount;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    //    @BindView(R.id.tv_save)
//    TextView tv_save;
    String strQuest, strAnswer;
    private int maxQuestLength = 20, maxAnswerLength = 100;
    private String limitToast = "";
    InterlocutionItemModel model;
    InterlocutionModel requestModel;
    Handler mHander = new Handler();
    /**
     * 重新编辑时去掉红字判断
     */
    private boolean hasCheckNoise = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_interlocution_new;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = getIntent().getParcelableExtra("item");
        if (model == null) {
            tvCenter.setText("添加问答");
        } else {
            tvCenter.setText("编辑问答");
        }
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (EditTextUtil.checkNoise(EditTextUtil.INTERLOCUTION, strQuest, et_setquest)) {
                    hasCheckNoise = true;
                    return;
                }
                addInterloc();
            }
        });
        if (model != null) {
            strQuest = model.vQueries.get(0).strQuery;
            for (int i = 0; i < model.vAnswers.size(); i++) {
                if (model.vAnswers.get(i).iType == 0) {
                    strAnswer = model.vAnswers.get(i).strText;
                    break;
                }
            }
        }
        requestModel = new InterlocutionModel();
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
            tvRight.setTextColor(getResources().getColor(R.color.ubt_version_color));
            tvRight.setEnabled(false);
        } else {
            tvRight.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
            tvRight.setEnabled(true);
        }
    }

    public void initLengthLimit() {
        InputFilter[] FilterArray0 = new InputFilter[2];
        FilterArray0[0] = new InputFilter() {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!isChinese(source.charAt(i))) {
                        return "";
                    } else {
                        if ((source.charAt(i) >= 0x4e00) && (source.charAt(i) <= 0x9fbb)) {

                        } else {
                            return "";
                        }
                    }
                }
                return source;
            }
        };
        FilterArray0[1] = new InputFilter.LengthFilter(20);
        et_setquest.setFilters(FilterArray0);
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
                            tvRight.setTextColor(getResources().getColor(R.color.ubt_version_color));
                            tvRight.setEnabled(false);
                        } else {
                            tvRight.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
                            tvRight.setEnabled(true);
                        }
                    }
                }
        );
        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter() {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find() || source.toString().contains(" ") || source.toString().contentEquals("\n")) {
                    //                    Toast.makeText(MainActivity.this,"不支持输入表情", 0).show();
                    return "";
                }
                return source;
            }
        };
        FilterArray[1] = new InputFilter.LengthFilter(100);
        et_answerset.setFilters(FilterArray);
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
                            tvRight.setTextColor(getResources().getColor(R.color.ubt_version_color));
                            tvRight.setEnabled(false);
                        } else {
                            tvRight.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
                            tvRight.setEnabled(true);
                        }
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 判定输入汉字
     */
    public boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    private void addInterloc() {
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
                            mHander.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingDialog.getInstance(AddInterloctionNewActivity.this)
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
                            mHander.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingDialog.getInstance(AddInterloctionNewActivity.this)
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
                            mHander.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingDialog.getInstance(AddInterloctionNewActivity.this)
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
                            mHander.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingDialog.getInstance(AddInterloctionNewActivity.this)
                                            .dismiss();
                                    ToastUtils.showShortToast(str);
                                }
                            });
                        }
                    });
        }
    }

}

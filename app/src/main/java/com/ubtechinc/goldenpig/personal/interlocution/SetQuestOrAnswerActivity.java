package com.ubtechinc.goldenpig.personal.interlocution;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ANSWER_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_QUESTTION_SUCCESS;

public class SetQuestOrAnswerActivity extends BaseNewActivity {

    @BindView(R.id.et_set)
    EditText etSet;
    @BindView(R.id.tv_center)
    TextView tv_center;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.tv_hint1)
    TextView tv_hint1;
    @BindView(R.id.tv_hint2)
    TextView tv_hint2;
    @BindView(R.id.tv_hint3)
    TextView tv_hint3;

    String str;
    /**
     * 0为问句
     * 1为回答
     */
    int type = 0;
    int maxchineseLength = 20;
    private String limitToast = "";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_set_quest_or_answer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str = getIntent().getStringExtra("str");
        type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case 0:
                tv_center.setText("添加问句");
                maxchineseLength = 20;
                limitToast = "最大长度为20个汉字";
                etSet.setHint("输入问句");
                tv_hint1.setText("*仅支持汉字");
                tv_hint2.setText("*最多输入20个汉字");
                tv_hint3.setVisibility(View.GONE);
                tv_count.setText("0/20");
                break;
            case 1:
                maxchineseLength = 100;
                tv_center.setText("添加回复");
                limitToast = "最大长度为100个字";
                etSet.setHint("输入回复");
                tv_count.setText("0/100");
                break;
        }
        if (!TextUtils.isEmpty(str)) {
            etSet.setText(str);
            int mMsgMaxLength = 0;
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                //32-122包含了空格，大小写字母，数字和一些常用的符号，
                //如果在这个范围内则算一个字符，
                //如果不在这个范围比如是汉字的话就是两个字符
                if (charAt >= 32 && charAt <= 122) {
                    mMsgMaxLength++;
                } else {
                    mMsgMaxLength += 2;
                }
            }
            if (type == 0) {
                tv_count.setText((mMsgMaxLength / 2) + "/20");
            } else {
                tv_count.setText((mMsgMaxLength / 2) + "/100");
            }
            try {
                etSet.setSelection(str.length());
            } catch (Exception e) {
            }
        }
        initLengthLimit();
        if (TextUtils.isEmpty(etSet.getText())) {
            tv_right.setEnabled(false);
            tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
        } else {
            tv_right.setEnabled(true);
            tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
        }
    }

    @OnClick({R.id.tv_left, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_right:
                str = etSet.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    if (type == 0) {
                        ToastUtils.showShortToast("请先设置问句");
                    } else {
                        ToastUtils.showShortToast("请先设置回复");
                    }
                    return;
                }
                if (type == 0) {
                    Event<String> event = new Event<String>(SET_QUESTTION_SUCCESS);
                    event.setData(str);
                    EventBusUtil.sendEvent(event);
                } else {
                    Event<String> event = new Event<String>(SET_ANSWER_SUCCESS);
                    event.setData(str);
                    EventBusUtil.sendEvent(event);
                }
                finish();
                break;
        }
    }

    public void initLengthLimit() {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (type == 0) {
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
                } else {
                    if (source.equals(" ")) return "";
                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_\\,.，。!;?]");
                    //String speChat = "[`~@#$%^&*()+=|{}':'\\[\\]<>@#￥%……&*（）——+|{}【】‘”“]";
                    //Pattern pattern = Pattern.compile(speChat);
                    Matcher matcher = pattern.matcher(source.toString());
                    if (matcher.find()) return "";
//                    int sourceLen = CommendUtil.getMsgLength(source.toString());
//                    int destLen = CommendUtil.getMsgLength(dest.toString());
//                    LogUtils.d("sourceLen:" + sourceLen + ",destLen:" + destLen);
//                    if (sourceLen + destLen > maxchineseLength) {
//                        ToastUtils.showShortToast(limitToast);
//                        return "";
//                    }
                    return source;
                }
            }
        };
        etSet.setFilters(FilterArray);
        etSet.addTextChangedListener(
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
                        selectionEnd = etSet.getSelectionEnd();
                        String str = etSet.getText().toString();
                        //LogUtils.d("hdf", str);
                        int mMsgMaxLength = 0;
                        int countNum = 0;
                        for (int i = 0; i < str.length(); i++) {
                            char charAt = str.charAt(i);
                            //32-122包含了空格，大小写字母，数字和一些常用的符号，
                            //如果在这个范围内则算一个字符，
                            //如果不在这个范围比如是汉字的话就是两个字符
                            if (charAt >= 32 && charAt <= 122) {
                                mMsgMaxLength++;
                            } else {
                                mMsgMaxLength += 2;
                            }
                            // 当最大字符大于6000时，进行字段的截取，并进行提示字段的大小
                            if (mMsgMaxLength > maxchineseLength * 2) {
                                countNum = str.length() - i;
                                delete = true;
                                ToastUtils.showShortToast(limitToast);
                                break;
                            }
                        }
//                        try {
//                            etSet.setSelection(str.length());
//                        } catch (Exception e) {
//                        }
                        if (type == 0) {
                            tv_count.setText((mMsgMaxLength / 2) + "/20");
                        } else {
                            tv_count.setText((mMsgMaxLength / 2) + "/100");
                        }
                        if (TextUtils.isEmpty(str)) {
                            tv_right.setEnabled(false);
                            tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_color));
                        } else {
                            tv_right.setEnabled(true);
                            tv_right.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));
                        }
                        try {
                            if (delete) {
                                delete = false;
                                editable.delete(selectionEnd - countNum, selectionEnd);
                            }
                        } catch (Exception e) {
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
}

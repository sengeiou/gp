package com.ubtechinc.goldenpig.personal.interlocution;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.model.JsonCallback;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DELETE_RECORD_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ANSWER_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_QUESTTION_SUCCESS;

public class SetQuestOrAnswerActivity extends BaseNewActivity {

    @BindView(R.id.et_set)
    EditText etSet;
    @BindView(R.id.tv_center)
    TextView tv_center;

    String str;
    /**
     * 0为问句
     * 1为回答
     */
    int type = 0;

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
                break;
            case 1:
                tv_center.setText("添加回复");
                break;
        }
        if (!TextUtils.isEmpty(str)) {
            etSet.setText(str);
            try {
                etSet.setSelection(str.length() - 1);
            } catch (Exception e) {
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

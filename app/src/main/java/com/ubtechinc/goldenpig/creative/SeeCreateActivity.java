package com.ubtechinc.goldenpig.creative;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.actionbar.SecondTitleBarViewTv;
import com.ubtechinc.goldenpig.base.BaseNewActivity;

import butterknife.BindView;


public class SeeCreateActivity extends BaseNewActivity {
    @BindView(R.id.et_setquest)
    TextView et_setquest;
    @BindView(R.id.tv_questcount)
    TextView tv_questcount;
    @BindView(R.id.et_answerset)
    TextView et_answerset;
    @BindView(R.id.tv_answercount)
    TextView tv_answercount;
    @BindView(R.id.rl_titlebar)
    SecondTitleBarViewTv rl_titlebar;
    @BindView(R.id.tv_save)
    TextView tv_save;
    String strQuest, strAnswer;
    private int maxQuestLength = 20, maxAnswerLength = 100;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_see_create;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_titlebar.setTitleText("问答详情");
        rl_titlebar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        strQuest = getIntent().getStringExtra("strQuest");
        strAnswer = getIntent().getStringExtra("strAnswer");
        if (!TextUtils.isEmpty(strQuest)) {
            et_setquest.setText(strQuest);
            tv_questcount.setText(strQuest.length() + "/" + maxQuestLength);
        }
        if (!TextUtils.isEmpty(strAnswer)) {
            et_answerset.setText(strAnswer);
            tv_answercount.setText(strAnswer.length() + "/" + maxAnswerLength);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

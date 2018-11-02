package com.ubtechinc.goldenpig.main;

import android.content.Intent;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public class SkillDetailActivity extends BaseWebActivity {

    private String url;

    @Override
    protected int getConentView() {
        return R.layout.activity_web_common;
    }

    @Override
    protected String getURL() {
        return url;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_skills_detail;
    }

    @Override
    protected void onGoNextWeb() {
//        setToolBarTitle(R.string.ubt_skills_detail);
//        showActionBar();
    }

    @Override
    protected void onGoBackWeb() {
//        setToolBarTitle(R.string.ubt_skills_manual);
//        hideActionBar();
    }

    @Override
    protected void processWeb() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
    }

}

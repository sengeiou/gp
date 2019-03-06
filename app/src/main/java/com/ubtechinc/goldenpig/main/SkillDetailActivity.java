package com.ubtechinc.goldenpig.main;

import android.content.Intent;
import android.util.Log;

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
    private String SMARTHOME_URL="SmartHome";
    private String SKILL_URL="Handbook";

    @Override
    protected String getURL() {
        return url;
    }

    @Override
    protected int getToolBarTitle() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        Log.d("SkillDetailActivity",url);
        if (url.contains(SMARTHOME_URL)) {
            Log.d("SkillDetailActivity",url);
            //hiddleTitle();
            //hideActionBar();
            return R.string.ubt_tab_smarthome;
        } else if (url.contains(SKILL_URL)) {
            Log.d("SkillDetailActivity",url);
            return R.string.ubt_skills_detail;
        }
        Log.d("SkillDetailActivity",url);
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
        super.processWeb();
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
    }

}

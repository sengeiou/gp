package com.ubtechinc.goldenpig.main;

import android.content.Intent;
import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;


public class PushSwitchActivity extends BaseWebActivity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        rl_titlebar.setVisibility(View.GONE);
//        StatusBarCompat.translucentStatusBar(this, true);
    }


    @Override
    protected void processWeb() {
        Intent intent = getIntent();
        url = intent.getStringExtra("push_url");
        super.processWeb();
    }

    @Override
    protected String getURL() {
        return url;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.app_name;
    }

}

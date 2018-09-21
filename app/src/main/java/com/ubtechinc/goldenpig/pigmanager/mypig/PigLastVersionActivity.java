package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.app.Activity;
import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;

public class PigLastVersionActivity extends BaseToolBarActivity {
    @Override
    protected int getConentView() {
        return R.layout.activity_last_version;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_device_version_update);
    }
}

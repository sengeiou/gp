package com.ubtechinc.goldenpig.main;

import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;

/**
 * @author ubt
 */
public class SmartHomeWebActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getURL() {
        return UbtWebHelper.getSmartHomeUrl();
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.main_smarthome;
    }

    @Override
    protected boolean needActionBar() {
        return false;
    }
}

package com.ubtechinc.goldenpig.main;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;

/**
 * @author ubt
 */
public class BleWebActivity extends BaseWebActivity {

    @Override
    protected String getURL() {
        return BuildConfig.H5_URL + "/small/smallBlue.html";
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.main_bt_speaker;
    }

    @Override
    protected void onGoNextWeb() {
//        setToolBarTitle(R.string.ubt_feedback);
    }

    @Override
    protected void onGoBackWeb() {
//        setToolBarTitle(R.string.ubt_help);
    }
}

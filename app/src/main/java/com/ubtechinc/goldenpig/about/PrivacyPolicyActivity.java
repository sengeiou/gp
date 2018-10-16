package com.ubtechinc.goldenpig.about;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :隐私条款
 * @time :2018/9/20 14:20
 * @change :
 * @changetime :2018/9/20 14:20
 */
public class PrivacyPolicyActivity extends BaseWebActivity {

    @Override
    protected String getURL() {

        return BuildConfig.H5_URL + "/small/smallProcy.html";
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_privacy_policy;
    }

}

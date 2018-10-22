package com.ubtechinc.goldenpig.about;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;

/**
 * @author ubt
 * @email :qiangta.huang@ubtrobot.com
 * @description :服务条款
 * @time :2018/9/20 14:20
 * @change :
 * @changetime :2018/9/20 14:20
 */
public class ServicePolicyActivity extends BaseWebActivity {

    @Override
    protected String getURL() {

        return BuildConfig.H5_URL + "/small/smallService.html";
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_service_policy;
    }

}

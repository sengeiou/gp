package com.ubtechinc.goldenpig.main;

import android.os.Bundle;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.nets.utils.DeviceUtils;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public class SkillActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getURL() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallSkill.html?";

        String deviceId = DeviceUtils.getDeviceId(this);

        String url = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign(this, deviceId).replace(" ", "%20")
                + "&product=" + BuildConfig.product + "&deviceId=" + deviceId;
        return url;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_skills_manual;
    }

    @Override
    protected boolean needActionBar() {
        return false;
    }

}

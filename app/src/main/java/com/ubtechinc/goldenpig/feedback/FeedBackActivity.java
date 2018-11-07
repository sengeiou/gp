package com.ubtechinc.goldenpig.feedback;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.nets.utils.DeviceUtils;

/**
 * @author ubt
 */
public class FeedBackActivity extends BaseWebActivity {

    @Override
    protected String getURL() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallComment.html?";

        String deviceId = DeviceUtils.getDeviceId(this);

        String url = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign(this, deviceId).replace(" ", "%20")
                + "&product=" + BuildConfig.product + "&deviceId=" + deviceId;

        return url;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_help;
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

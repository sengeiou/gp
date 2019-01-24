package com.ubtechinc.goldenpig.main;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.nets.utils.DeviceUtils;

/**
 * @author ubt
 */
public class QQMusicWebActivity extends BaseWebActivity {

    @Override
    protected String getURL() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallqqMusic.html?";

        String deviceId = DeviceUtils.getDeviceId(this);

        String url = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign(this, deviceId).replace(" ", "%20")
                + "&product=" + BuildConfig.product + "&deviceId=" + deviceId + "&authorization=" + CookieInterceptor.get().getToken();

        return url;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_qq_music;
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

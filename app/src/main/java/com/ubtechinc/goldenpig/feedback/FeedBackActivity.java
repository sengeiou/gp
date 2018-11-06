package com.ubtechinc.goldenpig.feedback;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.net.URestSigner;

/**
 * @author ubt
 */
public class FeedBackActivity extends BaseWebActivity {

    @Override
    protected String getURL() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallComment.html?";

        String url = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign().replace(" ", "%20") + "&authorization=" +
                CookieInterceptor.get().getToken() + "&product=" + BuildConfig.product;

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

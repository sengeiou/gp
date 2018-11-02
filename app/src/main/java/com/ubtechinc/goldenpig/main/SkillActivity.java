package com.ubtechinc.goldenpig.main;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.net.URestSigner;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public class SkillActivity extends BaseWebActivity {

    @Override
    protected int getConentView() {
        return R.layout.activity_web_common;
    }

    @Override
    protected String getURL() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallSkill.html?";

        String url = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign().replace(" ", "%20") + "&authorization=" +
                CookieInterceptor.get().getToken() + "&product=" + BuildConfig.product;
        return url;
    }

    @Override
    protected int getToolBarTitle() {
        hideActionBar();
        return R.string.ubt_skills_manual;
    }

    @Override
    protected void onGoNextWeb() {
        setToolBarTitle(R.string.ubt_skills_detail);
        showActionBar();
    }

    @Override
    protected void onGoBackWeb() {
        setToolBarTitle(R.string.ubt_skills_manual);
        hideActionBar();
    }

    @Override
    protected void processWeb() {
        mWebView.addJavascriptInterface(new SmallPigObject(SkillActivity.this), "SmallPigObject");
    }


}

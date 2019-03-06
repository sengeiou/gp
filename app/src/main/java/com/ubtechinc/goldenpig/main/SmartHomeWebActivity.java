package com.ubtechinc.goldenpig.main;

import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.tvlloginlib.entity.LoginInfo;

/**
 * @author ubt
 */
public class SmartHomeWebActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarWrapUtil.translucentStatusBar(this, true);
    }

    @Override
    protected String getURL() {
        LoginInfo loginInfo = CookieInterceptor.get().getThridLogin();
        String accessToken = "";
        String appId = "";
        String loginType = "";
        String openeId = "";
        if (loginInfo != null) {
            accessToken = loginInfo.getAccessToken();
            appId = loginInfo.getAppId();
            loginType = loginInfo.getLoginType();
            openeId = loginInfo.getOpenId();
        }
        //String baseUrl = BuildConfig.H5_URL + "/small/smallqqMusic.html?";
        String baseUrl = "http://10.10.32.22:8080/small/smallSmartHome.html?";
        String url = baseUrl + "accessToken=" + accessToken + "&appId=" + appId
                + "&loginType=" + loginType + "&openeId=" + openeId;
        return url;
    }

    @Override
    protected int getToolBarTitle() {

        hideActionBar();
        return R.string.main_smarthome;
    }

    @Override
    protected void onGoNextWeb() {
        showActionBar();
    }

    @Override
    protected void onGoBackWeb() {
        hideActionBar();
    }
}

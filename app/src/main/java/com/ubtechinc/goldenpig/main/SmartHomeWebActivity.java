package com.ubtechinc.goldenpig.main;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.ThirdPartLoginModule;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.nets.utils.DeviceUtils;

/**
 * @author ubt
 */
public class SmartHomeWebActivity extends BaseWebActivity {

    @Override
    protected String getURL() {
        final ThirdPartLoginModule.LoginRequest loginRequest = new ThirdPartLoginModule().new LoginRequest();
        loginRequest.getAccessToken();
        loginRequest.getAppId();
        loginRequest.getLoginType();
        loginRequest.getOpenId();
        loginRequest.getUbtAppId();
        //String baseUrl = BuildConfig.H5_URL + "/small/smallqqMusic.html?";
        String baseUrl = "http://10.10.32.22:8080/small/smallSmartHome.html?";
        String url = baseUrl + "accessToken=" + loginRequest.getAccessToken() + "&appId=" + loginRequest.getAppId()
                + "&LoginType=" + loginRequest.getLoginType() + "&OpeneId=" + loginRequest.getOpenId() + "&ubtAppId=" + loginRequest.getUbtAppId();
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

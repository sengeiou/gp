package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.tencent.ai.tvs.LoginProxy;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.login.ThirdPartLoginModule;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.nets.utils.DeviceUtils;
import com.ubtechinc.tvlloginlib.TVSManager;

import java.util.HashMap;

public class SmallPigObject {

    private Context mContext;

    private WebView mWebView;

    public SmallPigObject(Context context) {
        this.mContext = context;
    }

    public SmallPigObject(Context context, WebView webView) {
        this.mContext = context;
        this.mWebView = webView;
    }

    @JavascriptInterface
    public void openNewPage(String param) {
        HashMap<String, String> map = new HashMap<>();
        map.put("url", param);
        ActivityRoute.toAnotherActivity((Activity) mContext, SkillDetailActivity.class, map, false);
    }

    @JavascriptInterface
    public void questSign() {
        String deviceId = DeviceUtils.getDeviceId(mContext);
        String sign = URestSigner.sign(mContext, deviceId).replace(" ", "%20");
        String script = "javascript:sendSign(\"" + sign + "\")";
        mWebView.post(() -> mWebView.evaluateJavascript(script, value -> LogUtils.d("goldPig", "SmallPigObject|onReceiveValue:" + value)));
    }

    @JavascriptInterface
    public void loadTencentSmartHomePages() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            LoginProxy proxy = TVSManager.getInstance(this.mContext, com.ubtechinc.goldenpig.BuildConfig.APP_ID_WX, com.ubtechinc.goldenpig.BuildConfig.APP_ID_QQ).getProxy();
            String url = "https://ddsdk.html5.qq.com/smartHome";
            proxy.tvsRequestUrl(url, null, null, null);

            final ThirdPartLoginModule.LoginRequest loginRequest = new ThirdPartLoginModule().new LoginRequest();
            loginRequest.getAccessToken();
            loginRequest.getAppId();
            loginRequest.getLoginType();
            loginRequest.getOpenId();
            loginRequest.getUbtAppId();

        } else {
            Toast.makeText(UBTPGApplication.getContext(), "仅管理员可操作", Toast.LENGTH_SHORT).show();
        }
    }

}

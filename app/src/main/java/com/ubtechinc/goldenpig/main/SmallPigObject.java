package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.nets.utils.DeviceUtils;

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
    public void loadTencentSmartHomePages(){
        
    }


}

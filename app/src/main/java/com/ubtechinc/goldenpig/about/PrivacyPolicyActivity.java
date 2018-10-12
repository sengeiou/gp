package com.ubtechinc.goldenpig.about;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :隐私条款
 *@time          :2018/9/20 14:20
 *@change        :
 *@changetime    :2018/9/20 14:20
*/
public class PrivacyPolicyActivity extends BaseToolBarActivity {
    private WebView mWebView;

    private String URL;

    @Override
    protected int getConentView() {
        return R.layout.activity_web_common;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mWebView = findViewById(R.id.web_common);

        setTitleBack(true);
        setToolBarTitle(R.string.ubt_privacy_policy);

        initWebView();
    }

    private void initWebView() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallProcy.html";

        URL = baseUrl;

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        //开发稳定后需去掉该行代码
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        UBTLog.d("goldPig", "URL:" + URL);
        mWebView.loadUrl(URL);
    }

}

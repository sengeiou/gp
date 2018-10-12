package com.ubtechinc.goldenpig.main;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.net.URestSigner;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public class SkillActivity extends BaseToolBarActivity {

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
        setToolBarTitle(R.string.ubt_skills_manual);

        initWebView();
    }

    private void initWebView() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallSkill.html?";

        URL = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign().replace(" ", "%20") + "&authorization=" +
                CookieInterceptor.get().getToken() + "&product=" + BuildConfig.product;

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

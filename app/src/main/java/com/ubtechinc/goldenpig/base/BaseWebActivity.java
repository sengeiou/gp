package com.ubtechinc.goldenpig.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.net.URestSigner;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public abstract class BaseWebActivity extends BaseToolBarActivity {

    private WebView mWebView;

    private String URL;

    private int scrollX;

    private int scrollY;

    @Override
    protected int getConentView() {
        return R.layout.activity_web_common;
    }

    protected abstract String getURL();

    protected abstract @StringRes int getToolBarTitle();

    @Override
    protected void init(Bundle savedInstanceState) {
        mWebView = findViewById(R.id.web_common);

        setTitleBack(true);
        setToolBarTitle(getToolBarTitle());

        initWebView();
    }

    private void initWebView() {
        String baseUrl = BuildConfig.H5_URL + "/small/smallSkill.html?";

        URL = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign().replace(" ", "%20") + "&authorization=" +
                CookieInterceptor.get().getToken() + "&product=" + BuildConfig.product;

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        //开发稳定后需去掉该行代码
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
//        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        mWebView.getSettings().setAllowContentAccess(true);
//        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.getSettings().setAppCacheEnabled(true);


        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                } else {
                    scrollY = view.getScrollY();
                    scrollX = view.getScrollX();
                    view.loadUrl(url);
                    onGoNextWeb();
                }
                return true;
            }
        });

        UBTLog.d("goldPig", "URL:" + URL);
        mWebView.loadUrl(getURL());
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            mWebView.scrollTo(scrollX, scrollY);
            onGoBackWeb();
        } else {
            super.onBackPressed();
        }
    }

    protected void onGoNextWeb() {
        //TODO web继续深入
    }

    protected void onGoBackWeb() {
        //TODO web继续深入
    }

    @Override
    protected boolean isInterceptBack() {
        return true;
    }
}

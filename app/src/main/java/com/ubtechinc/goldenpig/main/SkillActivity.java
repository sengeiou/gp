package com.ubtechinc.goldenpig.main;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

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

        initWebView();
    }

    private void initWebView() {
        String baseUrl = "http://10.10.32.22:8080/small/smallSkill.html?"; //local

//        String baseUrl = "http://10.10.1.14:8090/cloud-ppi/help/small/smallSkill.html?"; //test

        URL = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign().replace(" ", "%20") + "&authorization=" +
                CookieInterceptor.get().getToken() + "&product=" + BuildConfig.APP_ID;

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

//        WebViewClient webViewClient = new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return true;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                if(HttpAddress.WebServiceAdderss.contains(HttpAddress.WebAddressDevelop)){
//                    //webview 忽略证书
//                    handler.proceed();
//                }else {
//                    super.onReceivedSslError(view, handler, error);
//                }
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
//
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//
//            }
//
//        };
//        mWebView.setWebViewClient(webViewClient);

        UBTLog.d("goldPig", "URL:" + URL);
        mWebView.loadUrl(URL);
    }
}

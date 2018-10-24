package com.ubtechinc.goldenpig.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

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

    private FrameLayout root;

    private String URL;

    private int scrollX;

    private int scrollY;

    private boolean isFirst = true;

    private boolean isGoBack = false;

    private ImageView iamgeView;

    @Override
    protected int getConentView() {
        return R.layout.activity_web_common;
    }

    protected abstract String getURL();

    protected abstract @StringRes
    int getToolBarTitle();

    @Override
    protected void init(Bundle savedInstanceState) {
        mWebView = findViewById(R.id.web_common);
        root = findViewById(R.id.web_root);

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
                    isGoBack = false;
                    isFirst = false;
                    view.loadUrl(url);
                    onGoNextWeb();
                }
                return true;
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (isFirst) {
                    return; //刚进入页面不需要模拟效果，app自己有
                }
                view.setVisibility(View.GONE);//先隐藏webview
                if (newProgress == 100) {
                    //加载完毕，显示webview 隐藏imageview
                    view.setVisibility(View.VISIBLE);
                    if (iamgeView != null) {
                        iamgeView.setVisibility(View.GONE);
                    }
                    //页面进入效果的动画
                    Animation translate_in = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_right_in);
                    Animation translate_out = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_left_out);
                    //页面退出的动画
                    if (isGoBack) {
                        translate_in = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_left_in);
                        translate_out = AnimationUtils.loadAnimation(BaseWebActivity.this, R.anim.slide_right_out);
                    }
                    translate_in.setFillAfter(true);
                    translate_in.setDetachWallpaper(true);
                    translate_out.setFillAfter(true);
                    translate_out.setDetachWallpaper(true);
//                     开启动画
                    if (null != iamgeView) {
                        iamgeView.startAnimation(translate_out);
                    }
                    view.startAnimation(translate_in);
                    //动画结束后，移除imageView
                    translate_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (null != iamgeView) {
                                root.removeView(iamgeView);
                                iamgeView = null;
                                isGoBack = false;
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    //url没加载好之前，隐藏webview，在主布局中，加入imageview显示当前页面快照
                    if (null == iamgeView) {
                        iamgeView = new ImageView(BaseWebActivity.this);
                        view.setDrawingCacheEnabled(true);
                        Bitmap bitmap = view.getDrawingCache();
                        if (null != bitmap) {
                            Bitmap b = Bitmap.createBitmap(bitmap);
                            iamgeView.setImageBitmap(b);
                        }
                        root.addView(iamgeView);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                UBTLog.d("basewebview", "title:" + title);
//                setToolBarTitle(title);
            }
        });

        UBTLog.d("goldPig", "URL:" + URL);
        mWebView.loadUrl(

                getURL());
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            isGoBack = true;
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
        //TODO web回退
    }

    @Override
    protected boolean isInterceptBack() {
        return true;
    }
}

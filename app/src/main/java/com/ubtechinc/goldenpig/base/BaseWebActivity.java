package com.ubtechinc.goldenpig.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.main.SmallPigObject;

/**
 * @author：ubt
 * @date：2018/10/12 15:56
 * @modifier：ubt
 * @modify_date：2018/10/12 15:56
 * [A brief description]
 */

public abstract class BaseWebActivity extends BaseToolBarActivity {

    protected WebView mWebView;

    private FrameLayout root;

    private int scrollX;

    private int scrollY;

    private boolean isFirst = true;

    private boolean isGoBack = false;

    private ImageView imageView;

    private View mWebError;

    private boolean loadError;

    private Button btWebReload;

    private String TAG = BaseWebActivity.class.getSimpleName();

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
        mWebError = findViewById(R.id.view_web_error_info);
        btWebReload = findViewById(R.id.bt_web_reload);

        btWebReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
//                mWebError.setVisibility(View.GONE);
//                mWebView.setVisibility(View.VISIBLE);
                loadError = false;
                mWebView.reload();
            }
        });

        setTitleBack(true);
        setToolBarTitle(getToolBarTitle());

        initWebView();
        initActionBar();
    }

    /**
     * 初始化actionbar
     */
    private void initActionBar() {
        if (needActionBar()) {
            showActionBar();
        } else {
            hideActionBar();
        }
    }

    protected void processWeb() {
        mWebView.addJavascriptInterface(new SmallPigObject(this, mWebView), "SmallPigObject");
    }

    private void initWebView() {
//        String baseUrl = BuildConfig.H5_URL + "/small/smallSkill.html?";

//        URL = baseUrl + "appId=" + BuildConfig.APP_ID + "&sign=" + URestSigner.sign().replace(" ", "%20") + "&authorization=" +
//                CookieInterceptor.get().getToken() + "&product=" + BuildConfig.product;

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mWebView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setTextZoom(100);
        processWeb();

        //开发稳定后需去掉该行代码
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
//        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        mWebView.getSettings().setAllowContentAccess(true);
//        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.getSettings().setAppCacheEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);


        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading|url:" + url);
                if (url.startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    return true;
                } else {
                    scrollY = view.getScrollY();
                    scrollX = view.getScrollX();
                    isGoBack = false;
                    isFirst = false;
                    onGoNextWeb();
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d(TAG, "onReceivedError:" + error);
                loadError = true;
                mWebError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
                dismissLoadDialog();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "onProgressChanged:" + newProgress);
                if (!loadError && newProgress == 100 && mWebError != null) {
                    mWebError.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    dismissLoadDialog();
                }
                if (isFirst) {
                    return; //刚进入页面不需要模拟效果，app自己有
                }
                view.setVisibility(View.GONE);//先隐藏webview
                if (newProgress == 100) {
                    //加载完毕，显示webview 隐藏imageview
                    view.setVisibility(View.VISIBLE);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
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
                    if (null != imageView) {
                        imageView.startAnimation(translate_out);
                    }
                    view.startAnimation(translate_in);
                    //动画结束后，移除imageView
                    translate_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (null != imageView) {
                                root.removeView(imageView);
                                imageView = null;
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
                    if (null == imageView) {
                        imageView = new ImageView(BaseWebActivity.this);
                        view.setDrawingCacheEnabled(true);
                        Bitmap bitmap = view.getDrawingCache();
                        if (null != bitmap) {
                            Bitmap b = Bitmap.createBitmap(bitmap);
                            imageView.setImageBitmap(b);
                        }
                        root.addView(imageView);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.d("basewebview", "title:" + title);
                if (!TextUtils.isEmpty(title)) {
                    setToolBarTitle(title);
                }
            }
        });

        String url = getURL();
        LogUtils.d("goldPig", "URL:" + url);
        mWebView.loadUrl(url);
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

    /**
     * 是否需要原生标题栏
     *
     * @return
     */
    protected boolean needActionBar() {
        return true;
    }
}

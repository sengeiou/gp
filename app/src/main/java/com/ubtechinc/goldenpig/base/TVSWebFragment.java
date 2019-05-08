package com.ubtechinc.goldenpig.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.web.TVSWebController;
import com.tencent.ai.tvs.web.TVSWebView;
import com.ubt.robot.dmsdk.TVSWrapBridge;
import com.ubt.robot.dmsdk.TVSWrapConstant;
import com.ubt.robot.dmsdk.TVSWrapType;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.stateview.ErrorState;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :TVS web页面
 * @time :2019/4/26 11:02
 * @change :
 * @changetime :2019/4/26 11:02
 */
public class TVSWebFragment extends AbstractFragment {

    private TVSWebController mWebViewController;

    public static final int ACTIVITY_RESULT_CODE_FILECHOOSER = 1000;

    private boolean mLoadError = false;

    private long mLastLoadProgress;

    private Disposable mDisposable;

    public static TVSWebFragment newInstance() {
        return new TVSWebFragment();
    }

    public boolean webGoBack() {
        return mWebViewController.goBack();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_web_tvs;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mWebViewController = ((TVSWebView) findViewById(R.id.tvsWebView)).getController();
        mWebViewController.setDeviceInfo(TVSWrapBridge.getDevice(TVSWrapType.TVS, TVSWrapConstant.PRODUCT_ID,
                AuthLive.getInstance().getRobotUserId()));
        mWebViewController.setUIEventListener(new TVSUIEventListener());
        mWebViewController.setBusinessEventListener(new DemoBusinessEventListener());
        String targetUrl = activity.getIntent().getStringExtra(TVSWebActivity.TVS_WEB_URL);
        mWebViewController.loadURL(targetUrl);
    }

    private class TVSUIEventListener implements TVSWebController.UIEventListener {
        @Override
        public void requireUISettings(String settings) {
        }

        @Override
        public boolean shouldOverrideUrlLoading(String url) {
            return false;
        }

        @Override
        public void onReceiveTitle(String title) {
            LogUtils.d("onReceiveTitle:" + title);
//            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.setSubtitle(title);
//            }
        }

        @Override
        public void onLoadProgress(int progress) {
            LogUtils.d("onLoadProgress:" + progress);
            mLastLoadProgress = SystemClock.elapsedRealtime();
        }

        @Override
        public void onLoadStarted(String url) {
            LogUtils.d("onLoadStarted");
            mLoadError = false;
        }

        @Override
        public void onLoadFinished(String url) {
            LogUtils.d("onLoadFinished");
            if (mLoadError) {
                loadManager.showStateView(ErrorState.class);
            } else {
                mDisposable = Observable.timer(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                                    if (SystemClock.elapsedRealtime() - mLastLoadProgress >= 200) {
                                        loadManager.showSuccess();
                                    }
                                }
                        );
            }
        }

        @Override
        public void onLoadError() {
            LogUtils.d("onLoadError");
            mLoadError = true;
        }
    }

    private class DemoBusinessEventListener implements TVSWebController.BusinessEventListener {
        @Override
        public void requireCloseWebView() {
            if (activity != null) {
                activity.finish();
            }
        }

        @Override
        public void onPickFile(Intent fileChooser) {
            startActivityForResult(fileChooser, ACTIVITY_RESULT_CODE_FILECHOOSER);
        }

        @Override
        public void onReceiveProxyData(JSONObject data) {
        }

        @Override
        public void onLoginResult(ELoginPlatform platform, int errorCode) {
        }

        @Override
        public void onTokenRefreshResult(ELoginPlatform platform, int errorCode) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LoginProxy.getInstance().handleQQOpenIntent(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == ACTIVITY_RESULT_CODE_FILECHOOSER) {
            mWebViewController.onPickFileResult(resultCode, data);
        }
    }

    @Override
    public void onDestroyView() {
        mWebViewController.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        super.onDestroyView();
    }

    @Override
    protected void onStateRefresh() {
        super.onStateRefresh();
        if (mWebViewController != null) {
            mWebViewController.reload();
        }
    }

}

package com.ubtechinc.goldenpig.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;

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

import org.json.JSONObject;

/**
 * @Description: ${DESCRIPTION}
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/23 14:53
 */

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :TVS web页面
 * @time :2019/4/25 18:09
 * @change :
 * @changetime :2019/4/25 18:09
 */
public class TVSWebActivity extends BaseToolBarActivity {

    private TVSWebController mWebViewController;

    public static final int ACTIVITY_RESULT_CODE_FILECHOOSER = 1000;

    public static final String TVS_WEB_URL = "TVS_WEB_URL";

    @Override
    protected int getConentView() {
        return R.layout.activity_web_tvs;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.main_smarthome);
        mWebViewController = ((TVSWebView) findViewById(R.id.tvsWebView)).getController();
        mWebViewController.setDeviceInfo(TVSWrapBridge.getDevice(TVSWrapType.TVS, TVSWrapConstant.PRODUCT_ID,
                AuthLive.getInstance().getRobotUserId()));
        mWebViewController.setUIEventListener(new TVSUIEventListener());
        mWebViewController.setBusinessEventListener(new DemoBusinessEventListener());
        String targetUrl = getIntent().getStringExtra(TVS_WEB_URL);
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
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(title);
            }
        }

        @Override
        public void onLoadProgress(int progress) {
            LogUtils.d("onLoadProgress");
        }

        @Override
        public void onLoadStarted(String url) {
            LogUtils.d("onLoadStarted");
        }

        @Override
        public void onLoadFinished(String url) {
            LogUtils.d("onLoadFinished");
        }

        @Override
        public void onLoadError() {
            LogUtils.d("onLoadError");
        }
    }

    private class DemoBusinessEventListener implements TVSWebController.BusinessEventListener {
        @Override
        public void requireCloseWebView() {
            finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LoginProxy.getInstance().handleQQOpenIntent(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == ACTIVITY_RESULT_CODE_FILECHOOSER) {
            mWebViewController.onPickFileResult(resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mWebViewController.goBack()) {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mWebViewController.onDestroy();
        super.onDestroy();
    }
}

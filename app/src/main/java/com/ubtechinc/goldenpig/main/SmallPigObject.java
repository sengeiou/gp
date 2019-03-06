package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.tencent.ai.tvs.LoginProxy;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
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
        if (pigInfo != null) {
            if (pigInfo.isAdmin) {
                LoginProxy proxy = TVSManager.getInstance(this.mContext, com.ubtechinc.goldenpig.BuildConfig.APP_ID_WX, com.ubtechinc.goldenpig.BuildConfig.APP_ID_QQ).getProxy();
                String url = "https://ddsdk.html5.qq.com/smartHome";
                proxy.tvsRequestUrl(url, null, null, null);
            } else {
              //  Toast.makeText(UBTPGApplication.getContext(), "仅管理员可操作", Toast.LENGTH_SHORT).show();
                showBindTipDialog();
            }
        } else {
            //TODO 弹框去绑猪
            showBindTipDialog();
        }
    }

    private void showBindTipDialog() {
        UBTSubTitleDialog dialog = new UBTSubTitleDialog(mContext);
        dialog.setRightBtnColor(ContextCompat.getColor(mContext, R.color.ubt_tab_btn_txt_checked_color));
        dialog.setTips("请完成绑定与配网");
        dialog.setSubTips("完成后即可使用各项技能");
        dialog.setSubTipGravity(Gravity.CENTER);
        dialog.setLeftButtonTxt("取消");
        dialog.setRightButtonTxt("确认");
        dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO goto ble bind config
                if (mContext instanceof BaseActivity) {
                    ((BaseActivity) mContext).toBleConfigActivity(false);
                }
            }
        });
        dialog.show();
    }

}

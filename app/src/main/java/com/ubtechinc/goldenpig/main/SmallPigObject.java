package com.ubtechinc.goldenpig.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.DeviceManager;
import com.tencent.ai.tvs.ui.MotionEventListener;
import com.tencent.ai.tvs.ui.ProxyDataListener;
import com.tencent.ai.tvs.ui.UserCenterStateListener;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.URestSigner;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.nets.utils.DeviceUtils;
import com.ubtechinc.tvlloginlib.TVSManager;

import org.json.JSONObject;

import java.util.HashMap;

import static com.ubtechinc.goldenpig.main.CommonWebActivity.KEY_IMMERSE_STATUSBAR;
import static com.ubtechinc.goldenpig.main.CommonWebActivity.KEY_NEED_ACTIONBAR;
import static com.ubtechinc.goldenpig.main.CommonWebActivity.KEY_URL;

@SuppressWarnings("unused")
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
        LogUtils.d("SmallPigObject", "openNewPage:" + param);
        if (TextUtils.isEmpty(param)) return;
        HashMap<String, Object> map = new HashMap<>();
        map.put(KEY_URL, param);
        map.put(KEY_IMMERSE_STATUSBAR, true);
        map.put(KEY_NEED_ACTIONBAR, false);
        if (param.contains("smallSmartHome")) {
            map.put(KEY_IMMERSE_STATUSBAR, false);
            map.put(KEY_NEED_ACTIONBAR, true);
        }
        ActivityRoute.toAnotherActivity((Activity) mContext, CommonWebActivity.class, map, false);
    }

    @JavascriptInterface
    public void goBack() {
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
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
                DeviceManager deviceManager = new DeviceManager();
                deviceManager.productId = TVSManager.PRODUCT_ID;
                deviceManager.dsn = AuthLive.getInstance().getRobotUserId();
                LoginProxy proxy = TVSManager.getInstance(this.mContext, com.ubtechinc.goldenpig.BuildConfig.APP_ID_WX, com.ubtechinc.goldenpig.BuildConfig.APP_ID_QQ).getProxy();
                String smarthomeUrl = "https://ddsdk.html5.qq.com/smartHome";
                proxy.tvsRequestUrl(smarthomeUrl, new UserCenterStateListener() {
                    @Override
                    public void onSuccess(ELoginPlatform eLoginPlatform, int i, CommOpInfo commOpInfo) {
                    }

                    @Override
                    public void onError(int i, CommOpInfo commOpInfo) {
                    }

                    @Override
                    public void onCancel(int i, CommOpInfo commOpInfo) {
                    }
                }, new ProxyDataListener() {
                    @Override
                    public boolean onDataRecv(JSONObject jsonObject) {
                        return false;
                    }
                }, new MotionEventListener() {
                    @Override
                    public void onMotionDown() {
                    }
                }, deviceManager);
//                LoginProxy proxy = TVSManager.getInstance(this.mContext, com.ubtechinc.goldenpig.BuildConfig.APP_ID_WX, com.ubtechinc.goldenpig.BuildConfig.APP_ID_QQ).getProxy();
//                String url = "https://ddsdk.html5.qq.com/smartHome";
//                proxy.tvsRequestUrl(url, null, null, null);
            } else {
                ToastUtils.showShortToast(R.string.only_admin_operate);
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

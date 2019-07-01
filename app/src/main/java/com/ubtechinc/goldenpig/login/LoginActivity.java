package com.ubtechinc.goldenpig.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.ubt.robot.dmsdk.TVSWrapBridge;
import com.ubt.robot.dmsdk.TVSWrapPlatform;
import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.CommonWebActivity;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.main.UbtWebHelper;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.CheckUtil;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;

import java.lang.ref.WeakReference;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :登录Activity
 * @time :2018/8/17 17:59
 * @change :
 * @changTime :2018/8/17 17:59
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    /**
     * qq登录按钮和微信登录按钮
     **/
    private View mQQLoginBtn;
    private View mWechatLoginBtn;
    private View ivSelectPrivacy;

    private TextView tvAgreementPolicy;
    private LoginModel mLoginModel;

    private AuthLive.AuthState mState;

    private NetworkHelper.NetworkInductor mInductor;
    private LoginHandler handler;
    private boolean isLogined;

    /**
     * 兼容微信双开取消不回调
     */
    private boolean isWXClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        handler = new LoginHandler(this);
        initNetHelper();
        initViews();
        registerProxy();
        registerEventObserve();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_qq_login:
                doTVSLogin(TVSWrapPlatform.QQOpen);
                break;
            case R.id.ubt_btn_wechat_login:
                doTVSLogin(TVSWrapPlatform.WX);
                break;
            default:
        }
    }

    private void doTVSLogin(TVSWrapPlatform tvsWrapPlatform) {
        if (!CheckUtil.checkPhoneNetState(this)) {
            return;
        }
        if (!ivSelectPrivacy.isSelected()) {
            UbtToastUtils.showCustomToast(this, getString(R.string.ubt_login_agree_policy_tip));
            return;
        }
        if (mLoginModel == null) {
            return;
        }
        switch (tvsWrapPlatform) {
            case WX:
                if (mLoginModel.isWXInstall()) {
                    if (mLoginModel.isWXSupport()) {
                        if (mLoginModel != null) {
                            mLoginModel.loginWX(this);
                            isLogined = true;
                            isWXClick = true;
                        }
                    } else {
                        ToastUtils.showShortToast(this, getString(R.string.ubt_wx_unspported));
                    }
                } else {
                    ToastUtils.showShortToast(this, getString(R.string.ubt_wx_uninstalled));
                }
                break;
            case QQOpen:
                if (mLoginModel != null) {
                    mLoginModel.loginQQ(this);
                    isLogined = true;
                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLogined) {
            isLogined = false;
            registerProxy();
        }
        if (isWXClick) {
            handler.postDelayed(() -> {
                if (isWXClick) {
                    isWXClick = false;
                    handleLoginEnable(true);
                    dismissLoadDialog();
                    ToastUtils.showShortToast(LoginActivity.this, getString(R.string.ubt_login_cancel));
                }
            }, 1500);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferencesUtils.putBoolean(this, "isPrivacySelect", ivSelectPrivacy.isSelected());
    }

    @Override
    protected void onDestroy() {
        if (AuthLive.getInstance().getState() != AuthLive.AuthState.TVSLOGINED) {
            AuthLive.getInstance().reset();
        }
        handler.removeMessages(1);
        handler.removeCallbacks(null);
        handler = null;
        NetworkHelper.sharedHelper().removeNetworkInductor(mInductor);
        super.onDestroy();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (TVSWrapBridge.handleQQOpenIntent(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @return :
     * @throws :
     * @parmam :
     * @date :2018/8/20 10:09
     * @des : 初始化控件函数
     */
    private void initViews() {
        mQQLoginBtn = findViewById(R.id.ubt_btn_qq_login);
        mQQLoginBtn.setOnClickListener(this);
        tvAgreementPolicy = findViewById(R.id.tv_agreement_policy);
        ivSelectPrivacy = findViewById(R.id.iv_select_privacy);
        ivSelectPrivacy.setOnClickListener(v -> ivSelectPrivacy.setSelected(!ivSelectPrivacy.isSelected()));

        processPolicy();

        mWechatLoginBtn = findViewById(R.id.ubt_btn_wechat_login);
        mWechatLoginBtn.setOnClickListener(this);

    }

    private void processPolicy() {
        ivSelectPrivacy.setSelected(SharedPreferencesUtils.getBoolean(this, "isPrivacySelect", false));
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.ubt_login_agree_policy));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ActivityRoute.toAnotherActivity(LoginActivity.this, CommonWebActivity.class,
                        UbtWebHelper.getServicePolicyWebviewData(LoginActivity.this), false);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //TODO
                ds.setColor(Color.parseColor("#0099EE"));
                ds.setUnderlineText(false);
                ds.bgColor = Color.parseColor("#F5F8FB");
            }
        }, 2, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ActivityRoute.toAnotherActivity(LoginActivity.this, CommonWebActivity.class,
                        UbtWebHelper.getPrivacyPolicyWebviewData(LoginActivity.this), false);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //TODO
                ds.setColor(Color.parseColor("#0099EE"));
                ds.setUnderlineText(false);
                ds.bgColor = Color.parseColor("#F5F8FB");
            }
        }, 12, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        spannableString.setSpan(getBgSpan(), 2, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(getBgSpan(), 12, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgreementPolicy.setText(spannableString);
        tvAgreementPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private BackgroundColorSpan getBgSpan() {
        return new BackgroundColorSpan(Color.parseColor("#F5F8FB"));
    }

    private void initNetHelper() {
        mInductor = new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    ToastUtils.showShortToast(LoginActivity.this, getString(R.string.ubt_network_unconnect));
                }
            }
        };
        NetworkHelper.sharedHelper().addNetworkInductor(mInductor);
    }

    /**
     * @auther :hqt
     * @description :注册登录代理
     */
    private void registerProxy() {
        if (mLoginModel == null) {
            mLoginModel = new LoginModel();
        }
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            return;
        } else {
            handler.sendEmptyMessage(1);
        }
    }

    private void registerEventObserve() {
        AuthLive.getInstance().observe(this, authLive -> {
            isWXClick = false;
            mState = authLive.getState();
            handler.removeMessages(1);
            switch (mState) {
                case LOGINING:
                    handleLoginEnable(false);
                    showLoadingDialog();
                    break;
                case TVSLOGINED:
                    dismissLoadDialog();
                    ActivityRoute.toAnotherActivity(LoginActivity.this, MainActivity.class, true);
                    break;
                case ERROR:
                    handleLoginEnable(true);
                    dismissLoadDialog();
                    ToastUtils.showShortToast(LoginActivity.this, getString(R.string.ubt_login_failure));
                    break;
                case NORMAL:
                    break;
                case CANCEL:
                    handleLoginEnable(true);
                    dismissLoadDialog();
                    ToastUtils.showShortToast(LoginActivity.this, getString(R.string.ubt_login_cancel));
                    break;
                default:
                    dismissLoadDialog();
                    break;
            }
        });
    }

    private void handleLoginEnable(boolean isEnable) {
        if (mQQLoginBtn != null) {
            mQQLoginBtn.setEnabled(isEnable);
        }
        if (mWechatLoginBtn != null) {
            mWechatLoginBtn.setEnabled(isEnable);
        }
    }

    private static class LoginHandler extends Handler {
        private WeakReference<LoginActivity> mActivity;

        public LoginHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mActivity != null && mActivity.get() != null) {
                final LoginActivity loginActivity = mActivity.get();
                if (null == loginActivity || loginActivity.isFinishing()) {
                    return;
                }
                switch (msg.what) {
                    case 0:
                        ActivityRoute.toAnotherActivity(loginActivity, BleConfigReadyActivity.class, true);
                        break;
                    case 1:
//                        ToastUtils.showShortToast(loginActivity, loginActivity.getString(R.string.ubt_net_error_tips));
                        break;
                    default:
                }

            }

        }
    }

    ;
}

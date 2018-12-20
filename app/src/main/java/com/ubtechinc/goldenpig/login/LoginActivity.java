package com.ubtechinc.goldenpig.login;

import android.arch.lifecycle.Observer;
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

import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.about.PrivacyPolicyActivity;
import com.ubtechinc.goldenpig.about.ServicePolicyActivity;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;

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
                qqLogin();
                break;
            case R.id.ubt_btn_wechat_login:
                wxLogin();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoginModel != null) {
            mLoginModel.onResume();
        }
        if (isLogined) {
            isLogined = false;
            registerProxy();
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
        super.onDestroy();
        handler.removeMessages(1);
        handler.removeCallbacks(null);
        handler = null;
        NetworkHelper.sharedHelper().removeNetworkInductor(mInductor);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginModel.onActivityResult(requestCode, resultCode, data);
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
                ActivityRoute.toAnotherActivity(LoginActivity.this, ServicePolicyActivity.class, false);
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
                ActivityRoute.toAnotherActivity(LoginActivity.this, PrivacyPolicyActivity.class, false);
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

    private void wxLogin() {
        if (mLoginModel == null) {
            return;
        }
        if (!mLoginModel.isWXInstall()) {
            ToastUtils.showShortToast(this, getString(R.string.ubt_wx_uninstalled));
            return;
        }
        if (!mLoginModel.isWXSupport()) {
            ToastUtils.showShortToast(this, getString(R.string.ubt_wx_unspported));
            return;
        }

        if (!ivSelectPrivacy.isSelected()) {
            UbtToastUtils.showCustomToast(this, getString(R.string.ubt_login_agree_policy_tip));
            return;
        }

        mLoginModel.loginWX(LoginActivity.this);
        isLogined = true;
    }

    private void qqLogin() {
        if (!ivSelectPrivacy.isSelected()) {
            UbtToastUtils.showCustomToast(this, getString(R.string.ubt_login_agree_policy_tip));
            return;
        }
        if (mLoginModel != null) {
            mLoginModel.loginQQ(this);
            isLogined = true;
        }
    }

    private void registerEventObserve() {
        AuthLive.getInstance().observe(this, new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                mState = authLive.getState();
                handler.removeMessages(1);
                switch (mState) {
                    case LOGINING:
                        showLoadingDialog();
                        break;
                    case TVSLOGINED:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadDialog();
                                ActivityRoute.toAnotherActivity(LoginActivity.this, MainActivity.class, true);
                            }
                        }, 1000);
                        break;
                    case ERROR:
                        ToastUtils.showShortToast(LoginActivity.this, getString(R.string.ubt_login_failure));
                    case NORMAL:
                    case CANCEL:
                        ///向下传递处理
                    default:
                        dismissLoadDialog();
                        break;
                }
            }
        });
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
                }

            }

        }
    }

    ;
}

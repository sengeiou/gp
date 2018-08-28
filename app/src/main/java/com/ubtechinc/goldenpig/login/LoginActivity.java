package com.ubtechinc.goldenpig.login;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.ubtechinc.commlib.network.NetworkHelper;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;

import java.lang.ref.WeakReference;

/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   :登录Activity
 * @time       :2018/8/17 17:59
 * @change     :
 * @changTime  :2018/8/17 17:59
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener  {
    /**qq登录按钮和微信登录按钮**/
    private View mQQLoginBtn;
    private View mWecahtLoginBtn;

    private LoginModel mLoginModel;

    private AuthLive.AuthState mState;

    private NetworkHelper.NetworkInductor mInductor;
    private LoginHandler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        handler=new LoginHandler(this);
        initNetHelper();
        initViews();
        registerProxy();
        registerEventObserve();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    }

    @Override
    protected void onDestroy() {
        if (AuthLive.getInstance().getState() != AuthLive.AuthState.LOGINED) {
            AuthLive.getInstance().reset();
        }
        super.onDestroy();
        handler.removeMessages(1);
        handler.removeCallbacks(null);
        handler = null;
        NetworkHelper.sharedHelper().removeNetworkInductor(mInductor);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginModel.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *@parmam   :
     *@return    :
     *@date      :2018/8/20 10:09
     *@exception :
     *@des       : 初始化控件函数
     */
    private void initViews(){
        mQQLoginBtn=findViewById(R.id.ubt_btn_qq_login);
        mQQLoginBtn.setOnClickListener(this);

        mWecahtLoginBtn=findViewById(R.id.ubt_btn_wechat_login);
        mWecahtLoginBtn.setOnClickListener(this);

    }

    private void initNetHelper(){
        mInductor = new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                 if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    ToastUtils.showShortToast(LoginActivity.this,getString(R.string.ubt_network_unconnect));
                }
            }
        };
        NetworkHelper.sharedHelper().addNetworkInductor(mInductor);
    }
    /**
     *@auther        :hqt
     *@description   :注册登录代理
    */
    private void registerProxy() {
        mLoginModel = new LoginModel();
        if (mLoginModel.checkToken(this)) {
            handler.sendEmptyMessage(0);
        }else  if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {

            mLoginModel.logoutTVS();
            return;
        }else {
            handler.sendEmptyMessage(1);
        }
    }

    private void wxLogin(){
        if (!mLoginModel.isWXInstall()) {
            ToastUtils.showShortToast(this,getString(R.string.ubt_wx_uninstalled));
            return;
        }
        if (!mLoginModel.isWXSupport()) {
            ToastUtils.showShortToast(this,getString(R.string.ubt_wx_unspported));
            return;
        }
        mLoginModel.loginWX(LoginActivity.this);
    }
    private void qqLogin(){
        if (mLoginModel!=null){
            mLoginModel.loginQQ(this);
        }
    }
    private void registerEventObserve(){
        AuthLive.getInstance().observe(this, new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                mState = authLive.getState();
                handler.removeMessages(1);
                switch (mState){
                    case LOGINING:
                        showLoadingDialog();
                        break;
                    case LOGINED:
                        dismissLoadDialog();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        break;
                    case ERROR:
                        ToastUtils.showShortToast(LoginActivity.this,getString(R.string.ubt_login_failure));
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
        public LoginHandler(LoginActivity activity){
            mActivity=new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity!=null&&mActivity.get()!=null) {
                final  LoginActivity loginActivity=mActivity.get();
                if (null==loginActivity||loginActivity.isFinishing()){
                    return;
                }
                if (msg.what == 1) {
                    ToastUtils.showShortToast(loginActivity,loginActivity.getString(R.string.ubt_net_error_tips));
                }
                loginActivity.startActivity(new Intent(loginActivity,MainActivity.class));
                loginActivity.finish();
            }

        }
    };
}

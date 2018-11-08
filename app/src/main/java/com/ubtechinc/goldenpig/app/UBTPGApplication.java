package com.ubtechinc.goldenpig.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.ai.tvs.LoginApplication;
import com.tencent.ai.tvs.info.ProductManager;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.ActivityTool;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.login.repository.UBTAuthRepository;
import com.ubtechinc.goldenpig.net.ResponseInterceptor;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.nets.HttpManager;
import com.ubtechinc.protocollibrary.communit.ProtoBufferDisposer;
import com.ubtechinc.tvlloginlib.TVSManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SERVER_RESPONSE_UNAUTHORIZED;

/**
 * @author hqt
 * @des Ubt 金猪applicaption
 * @time 2018/08/17
 */
public class UBTPGApplication extends LoginApplication {
    private static UBTPGApplication instance;
    static Context mContext;
    public static boolean voiceMail_debug = false;

    public static String mPairSerialNumber;

    private UBTBaseDialog mForceOfflineDialog;

    private UbtTIMManager mUbtTIMManager;

    public static Activity mTopActivity;

    private UBTAuthRepository ubtAuthRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        com.ubtech.utilcode.utils.Utils.init(this);
        instance = this;
        Foreground.init(this);
        mContext = this.getApplicationContext();
        Stetho.initializeWithDefaults(this);
        UbtLogger.init(getApplicationContext());
        UbtLogger.i("", ProtoBufferDisposer.TAG);
        EventBusUtil.register(this);
        initActivityLife();
        initTIMListener();
        HttpManager.interceptors.add(new ResponseInterceptor());
    }

    /**
     * 初始化简单Activity的生命周期
     */
    private void initActivityLife() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                ActivityTool.addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mTopActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityTool.finishActivity(activity);
            }
        });
    }

    private void initTIMListener() {
        ubtAuthRepository = new UBTAuthRepository();
        mUbtTIMManager = UbtTIMManager.getInstance();
        mUbtTIMManager.setUbtIMCallBack(new UbtTIMManager.UbtIMCallBack() {
            @Override
            public void onLoginError(int i, String s) {
                AuthLive.getInstance().timLoginError();
            }

            @Override
            public void onLoginSuccess() {
                AuthLive.getInstance().timLogined();
                sendClientIdToPig();
            }

            @Override
            public void onForceOffline() {
                showForceOfflineDialog();
            }
        });
    }

    private void sendClientIdToPig() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            ProductManager.getInstance().productId = BuildConfig.PRODUCT_ID;
            ProductManager.getInstance().dsn = pigInfo.getRobotName();
            String clientId = TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ).getClientId();
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getClientId(clientId));
            TIMConversation pigConversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, pigInfo.getRobotName());
            mUbtTIMManager.sendTIM(selfMessage, pigConversation);
        }
    }

    private void showForceOfflineDialog() {
        if (mTopActivity == null) return;
        mForceOfflineDialog = new UBTBaseDialog(mTopActivity);
        mForceOfflineDialog.setCancelable(false);
        mForceOfflineDialog.setCanceledOnTouchOutside(false);
//            if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
//                if (!Settings.canDrawOverlays(this)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                    startActivity(intent);
//                    return;
//                }
//                mForceOfflineDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
////                mForceOfflineDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
//            } else {
//                mForceOfflineDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            }
        mForceOfflineDialog.setTips("您的账号于其它设备上登录");
        mForceOfflineDialog.setLeftBtnShow(false);
        mForceOfflineDialog.setRightButtonTxt("知道了");
        mForceOfflineDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                ubtAuthRepository.logout(null);
                doLogout();
            }

        });
        if (!mForceOfflineDialog.isShowing()) {
            mForceOfflineDialog.show();
        }
    }

    private void doLogout() {
        new LoginModel().logoutTVS();
        AuthLive.getInstance().logout();
        ActivityManager.getInstance().popAllActivity();
        ActivityRoute.toAnotherActivity(mTopActivity, LoginActivity.class, true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event != null && event.getCode() == SERVER_RESPONSE_UNAUTHORIZED) {
            showForceOfflineDialog();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBusUtil.unregister(this);
    }

    public static UBTPGApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }
}

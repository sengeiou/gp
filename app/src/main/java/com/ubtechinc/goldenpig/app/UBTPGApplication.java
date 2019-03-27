package com.ubtechinc.goldenpig.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.ai.tvs.LoginApplication;
import com.tencent.ai.tvs.info.ProductManager;
import com.tencent.ai.tvs.info.UserInfoManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.improtolib.GPResponse;
import com.ubt.improtolib.UserRecords;
import com.ubtech.utilcode.utils.ActivityTool;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.ResponseInterceptor;
import com.ubtechinc.goldenpig.pigmanager.SetPigNetWorkActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.mypig.CheckRobotOnlineStateProxy;
import com.ubtechinc.goldenpig.pigmanager.mypig.PairPigActivity;
import com.ubtechinc.goldenpig.pigmanager.mypig.QRCodeActivity;
import com.ubtechinc.goldenpig.pigmanager.register.GetPairPigQRHttpProxy;
import com.ubtechinc.goldenpig.pigmanager.register.GetPigListHttpProxy;
import com.ubtechinc.goldenpig.push.PushAppInfo;
import com.ubtechinc.goldenpig.push.PushHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.AppUtil;
import com.ubtechinc.goldenpig.utils.OSUtils;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.goldenpig.utils.SCADAHelper;
import com.ubtechinc.nets.HttpManager;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.utils.DeviceUtils;
import com.ubtechinc.protocollibrary.communit.ProtoBufferDisposer;
import com.ubtechinc.push.UbtPushModel;
import com.ubtechinc.tvlloginlib.TVSManager;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;
import com.ubtrobot.analytics.mobile.AnalyticsKit;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.channelservice.proto.GPRelationshipContainer;
import com.ubtrobot.gold.GPSwitchContainer;
import com.ubtrobot.info.DeviceInfoContainer;
import com.ubtrobot.info.NativeInfoContainer;
import com.ubtrobot.upgrade.VersionInformation;
import com.ubtrobot.wifi.WifiMessageContainer;
import com.vise.utils.handler.CrashHandlerUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;

import static com.ubtechinc.goldenpig.app.Constant.SP_HAS_LOOK_LAST_RECORD;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.DO_UPDATE_PAIR_PIG;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.PUSH_MESSAGE_RECEIVED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.PUSH_NOTIFICATION_RECEIVED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SERVER_RESPONSE_UNAUTHORIZED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.TVS_LOGIN_SUCCESS;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.TVS_LOGOUT_SUCCESS;

/**
 * @author hqt
 * @des Ubt 金猪applicaption
 * @time 2018/08/17
 */
public class UBTPGApplication extends LoginApplication implements Observer {
    private static UBTPGApplication instance;
    static Context mContext;
    public static boolean voiceMail_debug = false;

    private UBTBaseDialog mForceOfflineDialog;

    private UbtTIMManager mUbtTIMManager;

    public static Activity mTopActivity;

    public static final String TAG = "goldenPig";

    private boolean isShowForceOfflineDialog;

    NetworkHelper.NetworkInductor netWorkInductor = null;

    public static boolean isNetAvailable = true;

    public static boolean hasShowedMobileFlowTip;

    public static boolean isRobotOnline = false;

    @Override
    public void onCreate() {
        super.onCreate();
        checkStartProcess();
        LogUtils.d(TAG, "UBTPGApplication|onCreate");
    }

    private void checkStartProcess() {
        String processName = OSUtils.getProcessName(this, android.os.Process.myPid());
        if (!TextUtils.isEmpty(processName)) {
            boolean defaultProcess = processName.equals(getPackageName());
            if (defaultProcess) {
                initAppForMainProcess();
            } else if (processName.contains(":QALSERVICE")) {
                //TODO 处理其他进程初始化
            }
        }
    }

    private void initAppForMainProcess() {

//        new ScreenAdaptation(this, 720, 1280).register();

        //add crash
        CrashHandlerUtil.getInstance().init(this, null, "BaJie_crash/");

        //SCADA
        initSCADA();

        //bugly
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), "a6f55be79e", false);
            CrashReport.setAppChannel(getApplicationContext(), AppUtil.getMetaDataFromApp(this, AppUtil
                    .KEY_CHANNEL_META));
        }

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
        initService();
        initNetListener();
    }

    private void initNetListener() {
        NetworkHelper.sharedHelper().addNetworkInductor(netWorkInductor = networkStatus -> {
            LogUtils.d(TAG, "onNetworkChanged----net available :" + NetworkHelper.sharedHelper().isNetworkAvailable());
            if (NetworkHelper.sharedHelper().isNetworkAvailable()) {
                //TODO 网络可用
                isNetAvailable = true;
            } else {
                //TODO 网络不可用
                isNetAvailable = false;
            }
            EventBusUtil.sendEvent(new Event(EventBusUtil.NETWORK_STATE_CHANGED));
        });
        NetworkHelper.sharedHelper().registerNetworkSensor(getApplicationContext());
    }

    private void initSCADA() {
        AnalyticsKit.initialize(this, BuildConfig.APP_ID, BuildConfig.APP_KEY,
                DeviceUtils.getDeviceId(this), Executors.newSingleThreadExecutor());
    }

    private void initService() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, StartUpService.class));
//        } else {
        startService(new Intent(this, StartUpService.class));
//        }
    }

    /**
     * 初始化简单Activity的生命周期
     */
    private void initActivityLife() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                ActivityTool.addActivity(activity);
                SCADAHelper.handleSCADAForPage(activity, activity.getClass().getSimpleName());
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
        mUbtTIMManager = UbtTIMManager.getInstance();
        mUbtTIMManager.setMsgObserve(this);
        mUbtTIMManager.setUbtIMCallBack(new UbtTIMManager.UbtIMCallBack() {
            @Override
            public void onLoginError(int i, String s) {
                AuthLive.getInstance().timLoginError();
            }

            @Override
            public void onLoginSuccess() {
                AuthLive.getInstance().timLogined();
                sendClientIdToPig();
                checkRobotStateCategory();

                EventBusUtil.sendEvent(new Event(EventBusUtil.DO_GET_NATIVE_INFO));
            }

            @Override
            public void onForceOffline() {
                showForceOfflineDialog("你的账号于其它设备上登录");
            }
        });
    }

    private void checkRobotStateCategory() {
        new Thread(() -> {
            try {
                while (true) {
                    doGetRobotOnlineState();
                    Thread.sleep(10 * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取小猪在线状态
     */
    private void doGetRobotOnlineState() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            CheckRobotOnlineStateProxy proxy = new CheckRobotOnlineStateProxy();
            proxy.check(pigInfo.getRobotName(), new CheckRobotOnlineStateProxy.RobotStateCallback() {
                @Override
                public void onError(String msg) {
                    isRobotOnline = false;
                }

                @Override
                public void onSuccess(boolean isOnline) {
                    isRobotOnline = isOnline;
                    EventBusUtil.sendEvent(new Event(EventBusUtil.RECEIVE_ROBOT_ONLINE_STATE));
                }
            });
        }
    }

    private void sendClientIdToPig() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            ProductManager.getInstance().productId = BuildConfig.PRODUCT_ID;
            ProductManager.getInstance().dsn = pigInfo.getRobotName();

            TVSManager tvsManager = TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ);
            String clientId = tvsManager.getClientId();
            Log.i(TAG, "sendClientIdToPig|clientId : " + clientId);
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getClientId(clientId));
            TIMConversation pigConversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, pigInfo.getRobotName());
            mUbtTIMManager.sendTIM(selfMessage, pigConversation);

            //TODO dsn注册音乐会员
            tvsManager.bindRobot(pigInfo.getRobotName());
        }
    }

    private void showForceOfflineDialog(String tip) {
        if (mTopActivity == null) return;
        mForceOfflineDialog = new UBTBaseDialog(mTopActivity);
        mForceOfflineDialog.setCancelable(false);
        mForceOfflineDialog.setCanceledOnTouchOutside(false);
        mForceOfflineDialog.setTips(tip);
        mForceOfflineDialog.setLeftBtnShow(false);
        mForceOfflineDialog.setRightButtonTxt("我知道了");
        mForceOfflineDialog.setRightBtnColor(ContextCompat.getColor(this, R.color.ubt_tab_btn_txt_checked_color));
        mForceOfflineDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                isShowForceOfflineDialog = false;
                doLogout();
            }

        });
        mForceOfflineDialog.setOnDismissListener(dialog -> isShowForceOfflineDialog = false);
        if (!isShowForceOfflineDialog || !mForceOfflineDialog.isShowing()) {

            if (mTopActivity != null) {
                if (mTopActivity instanceof LoginActivity) {

                } else {
                    if (!mTopActivity.isDestroyed() && !mTopActivity.isFinishing()) {
                        mForceOfflineDialog.show();
                        isShowForceOfflineDialog = true;
                    }
                }
            }
        }
    }

    private void doLogout() {
        new LoginModel().logoutTVS();
        AuthLive.getInstance().logout();
        ActivityManager.getInstance().popAllActivityExcept(LoginActivity.class.getName());
        ActivityRoute.toAnotherActivity(mTopActivity, LoginActivity.class, true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case SERVER_RESPONSE_UNAUTHORIZED:
                showForceOfflineDialog("账号授权已过期，请重新登录");
                break;
            case TVS_LOGIN_SUCCESS:
                String userId = AuthLive.getInstance().getUserId();
                AnalyticsKit.setUserId(userId);
                AnalyticsKit.setDeviceInfo(userId, AppUtil.getMetaDataFromApp(this, AppUtil.KEY_CHANNEL_META));
                PushAppInfo pushAppInfo = AuthLive.getInstance().getPushAppInfo();
                String pushToken = pushAppInfo.getPushToken();
                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(pushToken)) {
                    int appId = pushAppInfo.getAppId();
                    String authorization = pushAppInfo.getToken();
                    String appVersion = ContextUtils.getVerName(this);
                    PushHttpProxy pushHttpProxy = new PushHttpProxy();
                    pushHttpProxy.bindToken(appId, pushToken, userId, appVersion, BuildConfig.product, authorization,
                            null);
                }
                updatePigPair(true);
                break;
            case TVS_LOGOUT_SUCCESS:
                UbtTIMManager.getInstance().doTIMLogout();
                break;
            case PUSH_NOTIFICATION_RECEIVED:
                updatePigList();
                showIKnowDialog(((UbtPushModel) event.getData()).getContent());
                break;
            case PUSH_MESSAGE_RECEIVED:

                break;
            case DO_UPDATE_PAIR_PIG:
                updatePigPair(true);
                break;
        }
    }


    private void showIKnowDialog(String content) {
        if (mTopActivity == null || TextUtils.isEmpty(content)) return;
        UBTBaseDialog iknowDialog = new UBTBaseDialog(mTopActivity);
        iknowDialog.setCancelable(false);
        iknowDialog.setCanceledOnTouchOutside(false);
        iknowDialog.setTips(content);
        iknowDialog.setLeftBtnShow(false);
        iknowDialog.setRightButtonTxt("我知道了");
        iknowDialog.setRightBtnColor(ContextCompat.getColor(this, R.color.ubt_tab_btn_txt_checked_color));
        iknowDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }

        });
        if (!mTopActivity.isDestroyed() && !mTopActivity.isFinishing()) {
            iknowDialog.show();
        }
    }

    private void showUnpairPigDialog() {
        updatePigList();
        if (mTopActivity == null) return;
        UBTBaseDialog unpairPigDialog = new UBTBaseDialog(mTopActivity);
        unpairPigDialog.setCancelable(false);
        unpairPigDialog.setCanceledOnTouchOutside(false);
        unpairPigDialog.setTips("配对八戒已被对方解除");
        unpairPigDialog.setLeftBtnShow(false);
        unpairPigDialog.setRightButtonTxt("我知道了");
        unpairPigDialog.setRightBtnColor(ContextCompat.getColor(this, R.color.ubt_tab_btn_txt_checked_color));
        unpairPigDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                if (mTopActivity instanceof PairPigActivity) {
                    mTopActivity.finish();
                }
            }

        });
        if (!mTopActivity.isDestroyed() && !mTopActivity.isFinishing()) {
            if (mTopActivity instanceof SetPigNetWorkActivity) {

            } else {
                unpairPigDialog.show();
            }
        }
    }

    private void updatePigList() {
        new GetPigListHttpProxy().getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new
                GetPigListHttpProxy.OnGetPigListLitener() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onException(Exception e) {
                Log.e("getPigList", e.getMessage());
            }

            @Override
            public void onSuccess(String response) {
                Log.e("getPigList", response);
                PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance()
                        .getCurrentPigList());
                PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();

                if (pigInfo != null && pigInfo.isAdmin) {
                    UbtTIMManager.avatarURL = UserInfoManager.getInstance().headImgUrl;
                    UbtTIMManager.getInstance().loginTIM(AuthLive.getInstance().getUserId(), pigInfo.getRobotName(),
                            com.ubt.imlibv2.BuildConfig.IM_Channel);
                }
            }
        });
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBusUtil.unregister(this);
        NetworkHelper.sharedHelper().unregisterNetworkSensor(getApplicationContext());
    }

    public static UBTPGApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void update(Observable o, Object arg) {
        TIMMessage msg = (TIMMessage) arg;
        try {
            for (int i = 0; i < msg.getElementCount(); ++i) {
                TIMElem tIMElem = msg.getElement(i);
                if (tIMElem != null && tIMElem instanceof TIMCustomElem) {
                    TIMCustomElem elem = (TIMCustomElem) tIMElem;
                    dealMsg(elem.getData());
                } else if (tIMElem != null && tIMElem instanceof TIMSoundElem) {
                    Event<Integer> event = new Event<>(EventBusUtil.NEW_MESSAGE_NOTIFICATION);
                    EventBusUtil.sendEvent(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealMsg(Object arg) throws InvalidProtocolBufferException {
        ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
                .parseFrom((byte[]) arg);
        String action = msg.getHeader().getAction();
        if (action.equals(ContactsProtoBuilder.IM_RELATIONSHIP_CHANGED)) {
            GPRelationshipContainer.RelationShip info = msg.getPayload().unpack(GPRelationshipContainer.RelationShip
                    .class);
            if (info != null) {
                int event = info.getEvent();
                handleRelationShip(event);
            }
        } else if (action.equals(ContactsProtoBuilder.IM_RECORD_LATEST)) {
            List<UserRecords.Record> list = msg.getPayload().unpack(UserRecords.UserRecord
                    .class).getRecordList();
            boolean hasRecord = false;
            if (list != null && !list.isEmpty()) {
                if (list.get(0).getType() == 3) {
                    hasRecord = true;
                }
                SPUtils.get().put(SP_HAS_LOOK_LAST_RECORD, list.get(0).getType());
            }
            if (hasRecord) {
                Event<Boolean> event = new Event<>(EventBusUtil.NEW_CALL_RECORD);
                event.setData(hasRecord);
                EventBusUtil.sendEvent(event);
            }
        } else if (action.equals(ContactsProtoBuilder.GET_NATIVE_INFO)) {
            NativeInfoContainer.NativeInfo nativeInfo = msg.getPayload().unpack(NativeInfoContainer.NativeInfo.class);
            Log.d(TAG, "nativeInfo = " + nativeInfo);
            if (nativeInfo != null) {
                Event<NativeInfoContainer.NativeInfo> event = new Event<>(EventBusUtil.RECEIVE_NATIVE_INFO);
                event.setData(nativeInfo);
                EventBusUtil.sendEvent(event);
            }
        } else if (action.equals(ContactsProtoBuilder.GET_VERSION_ACTION)) {
            VersionInformation.UpgradeInfo info = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class);
            if (info != null) {
                String version = info.getCurrentVersion();
                Event<String> event = new Event<>(EventBusUtil.RECEIVE_PIG_VERSION);
                event.setData(version);
                EventBusUtil.sendEvent(event);
            }
        } else if (action.equals(ContactsProtoBuilder.IM_DIALOG_REQUEST)) {
            GPSwitchContainer.Switch switchInfo = msg.getPayload().unpack(GPSwitchContainer.Switch.class);
            boolean state = switchInfo.getState();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_CONTINUOUS_VOICE_STATE);
            event.setData(state);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_DIALOG_SWITCH)) {
            final boolean result = msg.getPayload().unpack(GPResponse.Response.class).getResult();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_CONTINUOUS_VOICE_RESPONSE);
            event.setData(result);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_DEVICE_INFO)) {
            DeviceInfoContainer.GPDeviceInfo deviceInfo = msg.getPayload().unpack(DeviceInfoContainer.GPDeviceInfo
                    .class);
            Event<DeviceInfoContainer.GPDeviceInfo> event = new Event<>(EventBusUtil.RECEIVE_PIG_DEVICE_INFO);
            event.setData(deviceInfo);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_REQUEST_WIFI_LIST)) {
            //TODO wifi列表
            WifiMessageContainer.WifiList wifiList = msg.getPayload().unpack(WifiMessageContainer.WifiList.class);
            Event<WifiMessageContainer.WifiList> event = new Event<>(EventBusUtil.RECEIVE_PIG_WIFI_LIST);
            event.setData(wifiList);
            EventBusUtil.sendEvent(event);

        } else if (action.equals(ContactsProtoBuilder.IM_CONNECT_WIFI)) {
            //TODO wifi切换
            WifiMessageContainer.ConnectStatus connectStatus = msg.getPayload().unpack(WifiMessageContainer
                    .ConnectStatus.class);
            Event<WifiMessageContainer.ConnectStatus> event = new Event<>(EventBusUtil.RECEIVE_PIG_WIFI_CONNECT);
            event.setData(connectStatus);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_CLEAR_REQUEST)) {
            //TODO 本体信息清除
            final boolean result = msg.getPayload().unpack(GPResponse.Response.class).getResult();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_CLEAR_PIG_INFO);
            event.setData(result);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.UPATE_VERSION_ACTION) || action.equals(ContactsProtoBuilder
                .UPATE_VERSION_RESULT_ACTION)) {
            final int result = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class).getStatus();
            handleOTADialog(result, action);
        } else if (action.equals(ContactsProtoBuilder.GET_VERSION_STATE_ACTION)) {
            VersionInformation.UpgradeInfo info = msg.getPayload().unpack(VersionInformation.UpgradeInfo.class);
            Event<VersionInformation.UpgradeInfo> event = new Event<>(EventBusUtil.RECEIVE_ROBOT_VERSION_STATE);
            event.setData(info);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_GET_SHUTDOWN_ALARM_REQUEST)) {
            GPSwitchContainer.Switch switchInfo = msg.getPayload().unpack(GPSwitchContainer.Switch.class);
            boolean state = switchInfo.getState();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_SHUTDOWN_STATE);
            event.setData(state);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_SET_SHUTDOWN_ALARM_REQUEST)) {
            final boolean result = msg.getPayload().unpack(GPResponse.Response.class).getResult();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_SHUTDOWN_SWITCH_STATE);
            event.setData(result);
            EventBusUtil.sendEvent(event);
        }else if (action.equals(ContactsProtoBuilder.IM_GET_NO_DELAY_WAKEUP_REQUEST)) {
            GPSwitchContainer.Switch switchInfo = msg.getPayload().unpack(GPSwitchContainer.Switch.class);
            boolean state = switchInfo.getState();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_NO_DELAY_WAKEUP_STATE);
            event.setData(state);
            EventBusUtil.sendEvent(event);
        } else if (action.equals(ContactsProtoBuilder.IM_SET_NO_DELAY_WAKEUP_REQUEST)) {
            final boolean result = msg.getPayload().unpack(GPResponse.Response.class).getResult();
            Event<Boolean> event = new Event<>(EventBusUtil.RECEIVE_NO_DELAY_WAKEUP_SWITCH_STATE);
            event.setData(result);
            EventBusUtil.sendEvent(event);
        }
    }

    private void handleOTADialog(int result, String action) {
        Log.d(TAG, "OTA|action=" + action + " result=" + result);
        String tip = "";
        switch (result) {
            case 3:
                tip = "系统正处于升级状态，升级包会在连接无线网络时自动下载安装";
                break;
            case 4:
                tip = "收到升级请求，八戒会在连接\n" +
                        "Wi-Fi且电量充足时自动升级";
                break;
            case 5:
                tip = "升级包异常，系统升级失败";
                if (action.equals(ContactsProtoBuilder.UPATE_VERSION_RESULT_ACTION)) {
                    if (SharedPreferencesUtils.getBoolean(this, "hasTipOTAResult", false)) {
                        tip = "";
                    } else {
                        SharedPreferencesUtils.putBoolean(this, "hasTipOTAResult", true);
                    }
                }
//                if (mTopActivity instanceof DeviceUpdateActivity) {
//                    mTopActivity.finish();
//                }
                break;
            case 6:
                tip = "系统升级成功";
                if (action.equals(ContactsProtoBuilder.UPATE_VERSION_RESULT_ACTION)) {
                    if (SharedPreferencesUtils.getBoolean(this, "hasTipOTAResult", false)) {
                        tip = "";
                    } else {
                        SharedPreferencesUtils.putBoolean(this, "hasTipOTAResult", true);
                    }
                }
//                if (mTopActivity instanceof DeviceUpdateActivity) {
//                    mTopActivity.finish();
//                }
                break;
            case 7:
                tip = "服务器异常，无法升级\n" +
                        "请稍后重试";
//                if (mTopActivity instanceof DeviceUpdateActivity) {
//                    mTopActivity.finish();
//                }
                break;
            case 8:
                tip = "服务器异常，无法升级\n" +
                        "请稍后重试";
//                if (mTopActivity instanceof DeviceUpdateActivity) {
//                    mTopActivity.finish();
//                }
                break;
            case 9:
                tip = "八戒系统空间不足，无法升级";
//                if (mTopActivity instanceof DeviceUpdateActivity) {
//                    mTopActivity.finish();
//                }
                break;
            case 10:
                tip = "八戒文件系统异常，无法升级";
//                if (mTopActivity instanceof DeviceUpdateActivity) {
//                    mTopActivity.finish();
//                }
                break;
//            default:
//                tip = "OTA升级完成";
//                break;
        }
        showIKnowDialog(tip);
    }

    private void handleRelationShip(int event) {
        switch (event) {
            case 1:
                //绑定关系变化
                break;
            case 2:
                //配对关系变化
                updatePigPair(false);
                break;
        }
    }

    private void updatePigPair(boolean initiative) {
        new GetPairPigQRHttpProxy().getPairPigQR(this, CookieInterceptor.get().getToken(), BuildConfig
                .APP_ID, new GetPairPigQRHttpProxy.GetPairPigQRCallBack() {
            @Override
            public void onError(String error) {
                //TODO 配对关系不存在
                AuthLive.getInstance().setPairPig(null);
                Event<Integer> event = new Event<>(EventBusUtil.PAIR_PIG_UPDATE);
                EventBusUtil.sendEvent(event);
                if (!initiative) {
                    showUnpairPigDialog();
                }
            }

            @Override
            public void onSuccess(String response) {
                //TODO 刷新配对关系
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject != null) {
                            JSONObject pairData = new JSONObject(jsonObject.optString("pairData"));
                            if (pairData != null) {
                                int pairUserId = pairData.optInt("pairUserId");
                                String serialNumber = pairData.optString("serialNumber");
                                String pairSerialNumber = pairData.optString("pairSerialNumber");
                                int userId = pairData.optInt("userId");
                                PairPig pairPig = new PairPig();
                                pairPig.setPairUserId(pairUserId);
                                pairPig.setSerialNumber(serialNumber);
                                pairPig.setPairSerialNumber(pairSerialNumber);
                                pairPig.setUserId(userId);
                                AuthLive.getInstance().setPairPig(pairPig);
                                Event<Integer> event = new Event<>(EventBusUtil.PAIR_PIG_UPDATE);
                                EventBusUtil.sendEvent(event);
                                if (mTopActivity != null && mTopActivity instanceof QRCodeActivity) {
                                    mTopActivity.finish();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        UBTLog.e("pig", e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            //非默认值
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        Configuration configuration = res.getConfiguration();
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
        return res;
    }

}

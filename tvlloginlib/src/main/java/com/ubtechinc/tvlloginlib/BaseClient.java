package com.ubtechinc.tvlloginlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.ai.tvs.AuthorizeListener;
import com.tencent.ai.tvs.ConstantValues;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.env.EUserAttrType;
import com.tencent.ai.tvs.info.DeviceManager;
import com.tencent.ai.tvs.info.LoginInfoManager;
import com.tencent.ai.tvs.info.PushInfoManager;
import com.tencent.ai.tvs.ui.UserCenterStateListener;
import com.ubtechinc.tvlloginlib.entity.LoginInfo;

import SmartService.EAIPushIdType;
import qrom.component.push.base.utils.LogUtil;

import static com.ubtechinc.tvlloginlib.TVSManager.DEVICE_OEM;
import static com.ubtechinc.tvlloginlib.TVSManager.DEVICE_TYPE;
import static com.ubtechinc.tvlloginlib.TVSManager.PRODUCT_ID;


public abstract class BaseClient implements AuthorizeListener {

    public static final String TAG = "BaseClient";

    private ELoginPlatform platform;

    protected LoginProxy proxy;

    protected ClientResultListener listener;

    protected String userId;

    protected String dsn;

    protected String productId;

    protected LoginInfoManager loginInfoManager;

    protected IRobotTvsManager robotTvsManager;


    public BaseClient(LoginProxy proxy, ELoginPlatform platform) {
        this(proxy, platform, null);
    }

    public BaseClient(LoginProxy proxy, ELoginPlatform platform, ClientResultListener listener) {
        this.proxy = proxy;
        this.platform = platform;
        this.listener = listener;
        loginInfoManager = proxy.getInfoManager(platform);
    }

    public boolean isTokenExist(Context context) {
        return proxy.isTokenExist(platform, context);
    }

    public void login(Activity activity, String productId, String dsn) {
        Log.i(TAG, "tvs login start platform = " + platform.name());
        proxy.requestLogin(platform, "", "", activity);
    }

    public void tvsAuth(IRobotTvsManager robotTvsManager) {
        this.robotTvsManager = robotTvsManager;
        if (robotTvsManager == null) {
            throw new RuntimeException("IRobotTvsManager is null");
        }
        Log.d(TAG, "tvs auth start platform = " + platform.name());
        getProductIdFromDevice();
    }

    public void tvsAuth(String productId, String dsn) {
        robotTvsManager = null;
        if (!TextUtils.isEmpty(productId) && !TextUtils.isEmpty(dsn)) {
            BaseClient.this.dsn = dsn;
            BaseClient.this.productId = productId;
            proxy.requestTokenVerify(platform, productId, dsn);
        } else {
            authError(404);
        }
    }

    public void refreshLoginToken() {
        Log.d(TAG, "tvs refresh start platform = " + platform.name());
        proxy.requestTokenVerify(platform, "", "");
    }

    public void toUserCenter(String dsn, EUserAttrType eUserAttrType) {
        DeviceManager deviceManager = new DeviceManager();
        deviceManager.deviceOEM = DEVICE_OEM;
        deviceManager.deviceType = DEVICE_TYPE;
        deviceManager.productId = PRODUCT_ID;
        deviceManager.dsn = dsn;
        Log.d(TAG, " toUserCenter -- dsn : " + dsn);
        proxy.toUserCenter(eUserAttrType, deviceManager, new UserCenterStateListener() {
            @Override
            public void onSuccess(ELoginPlatform eLoginPlatform, int i, CommOpInfo var2) {
                Log.i(TAG, " onSuccess i : " + i);
            }

            @Override
            public void onError(int i, CommOpInfo var2) {
                Log.i(TAG, " onError i : " + i);
            }

            @Override
            public void onCancel(int i, CommOpInfo var2) {
                Log.i(TAG, " onCancel i : " + i);
            }
        });
    }

    private void getProductIdFromDevice() {
        Log.d(TAG, "getProductIdFromDevice start ");
        robotTvsManager.getProductId(new IRobotTvsManager.GetRobotTvsProductIdListener() {
            @Override
            public void onSuccess(String productId, String dsn) {
                BaseClient.this.dsn = dsn;
                BaseClient.this.productId = productId;
                proxy.requestTokenVerify(platform, productId, dsn);
            }

            @Override
            public void onError() {
                authError(500);
            }
        });
    }

    private void sendAccessTokenToDevice() {
//        DBRobotTVSInfo info = loadAccessTokenFromDB(userId, dsn);
        if (robotTvsManager == null) {
            throw new RuntimeException("robotTvsManager is null");
        }
        Log.d(TAG, "sendAccessTokenToDevice start ");
        final String accessToken = loginInfoManager.accessToken;
        final String refreshToken = loginInfoManager.refreshToken;
        final Long expireTime = loginInfoManager.expireTime;
        final String clientId = proxy.getClientId(platform);
//        if (info != null && accessToken.equals(info.getAccessToken())) {
//            authSuccess();
//            return;
//        } else {
        robotTvsManager.sendAccessToken(accessToken, refreshToken, expireTime, clientId, new IRobotTvsManager
                .SendTvsAccessTokenListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "send tvs Access Token Data success");
                authSuccess(clientId);
            }

            @Override
            public void onError() {
                Log.d(TAG, "send tvs Access Token Data error");
                authError(500);
            }
        });

//        }
    }

    public void logout(Context context) {
        userId = null;
        dsn = null;
        productId = null;
        if (platform.equals(ELoginPlatform.QQOpen)) {//qq登录不管是否有token都要退出登录，单独处理
            proxy.clearToken(platform, context);
            return;
        }
        if (proxy.isTokenExist(platform, context)) {
            Log.d(TAG, platform.name() + " : logout");
            proxy.clearToken(platform, context);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //QQ 互联需要处理
    }

    public void onResume() {

    }

    protected LoginInfo getLoginInfo(String userId) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAppId(loginInfoManager.appId);
        loginInfo.setAccessToken(loginInfoManager.accessToken);
        loginInfo.setMiniTvsId(userId);
        String type = LoginInfo.LoginType.get(platform.ordinal());
        loginInfo.setLoginType(type);
        loginInfo.setOpenId(loginInfoManager.openID);
        loginInfo.setTvsId(loginInfoManager.tvsID);
        Log.d("hdf", "loginInfoManager.tvsID:" + loginInfoManager.tvsID);
        return loginInfo;
    }

    /*public DBRobotTVSInfo loadProductIdFromDB(String userId, String dsn) {
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(dsn)) {
            return RobotTVSInfoProvider.get().findRobotByUserIdAndDsn(userId, dsn);
        }
        return null;
    }

    public DBRobotTVSInfo loadAccessTokenFromDB(String userId, String dsn) {
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(dsn)) {
            return RobotTVSInfoProvider.get().findRobotByUserIdAndDsnAndPlatform(userId, dsn, platform.name());
        }
        return null;
    }

    public void saveAccessTokenToDB(LoginInfoManager infoManager) {
        try {
            DBRobotTVSInfo info = new DBRobotTVSInfo();
            info.setProductId(BaseClient.this.productId);
            info.setDsn(BaseClient.this.dsn);
            info.setAccessToken(infoManager.accessToken);
            info.setRefreshToken(infoManager.refreshToken);
            info.setExpire(infoManager.expireTime);
            info.setExpireAt(infoManager.expireTime * 1000 + TimeUtils.getCurrentTimeInLong());
            info.setUserId(userId);
            info.setPlatform(platform.name());
            RobotTVSInfoProvider.get().saveOrUpdateByUserIdAndDsnAndPlatform(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void loginSuccess(LoginInfo info) {
        if (listener != null) {
            listener.onLoginSuccess(info);
        }
    }

    public void loginError() {
        if (listener != null) {
            listener.onLoginError();
        }
    }

    public void authSuccess(String clientId) {
        //TODO
        if (listener != null) {
            listener.onAuthSuccess(clientId);
        }
    }

    public void authError(int code) {
        //TODO
        if (listener != null) {
            listener.onAuthError(code);
        }
    }

    @Override//requestTokenVerify方法的返回值
    public void onSuccess(int i, CommOpInfo var2) {
        doOnSuccess();
    }

    @Override
    public void onError(int i, CommOpInfo var2) {
        doOnError();
    }

    private void doOnSuccess() {
        final String clientId = proxy.getClientId(platform);
        if (ConstantValues.INVALID_CLIENTID.equals(clientId)) {
            String userId = proxy.getUserId();
            LoginInfo loginInfo = getLoginInfo(userId);
            loginSuccess(loginInfo);
        } else {
            if (robotTvsManager != null) {
                sendAccessTokenToDevice();
            } else {
                authSuccess(clientId);
            }
        }
    }

    private void doOnError() {
        final String clientId = proxy.getClientId(platform);
        if (ConstantValues.INVALID_CLIENTID.equals(clientId)) {
            loginError();
        } else {
            authError(403);
        }
    }


    public interface ClientResultListener {

        public void onLoginSuccess(LoginInfo info);

        public void onLoginError();

        public void onAuthSuccess(String clientId);

        public void onAuthError(int code);

        public void onClientCancel();

    }

    @Override
    public void onCancel(int i) {

    }

    public boolean isAppInstall() {
        return true;
    }

    public boolean isSupportLogin() {
        return true;
    }

    public void bindRobot(String dsn) {
        Log.d("hdf", "bindRobot");
        PushInfoManager pushManager = PushInfoManager.getInstance();
        DeviceManager deviceManager = new DeviceManager();
        deviceManager.deviceOEM = DEVICE_OEM;
        deviceManager.deviceType = DEVICE_TYPE;
        deviceManager.productId = PRODUCT_ID;
        deviceManager.dsn = dsn;
        pushManager.idType = EAIPushIdType._ETVSSpeakerIdentifier;
        pushManager.idExtra = ConstantValues.PUSHMGR_IDEXTRA;
        proxy.requestSetPushMapInfoEx(platform, pushManager, deviceManager);
        proxy.requestGetPushDeviceInfo(platform);
    }

    public void unbindRobot(String dsn) {
        PushInfoManager pushManager = PushInfoManager.getInstance();
        DeviceManager deviceManager = new DeviceManager();
        deviceManager.deviceOEM = DEVICE_OEM;
        deviceManager.deviceType = DEVICE_TYPE;
        deviceManager.productId = PRODUCT_ID;
        deviceManager.dsn = dsn;
        pushManager.idType = EAIPushIdType._ETVSSpeakerIdentifier;
        pushManager.idExtra = ConstantValues.PUSHMGR_IDEXTRA;
        proxy.requestDelPushMapInfo(platform, pushManager, deviceManager);
    }
}

package com.ubtechinc.goldenpig.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.push.PushAppInfo;
import com.ubtechinc.goldenpig.push.PushHttpProxy;
import com.ubtechinc.goldenpig.push.PushListener;
import com.ubtechinc.push.PushBrandType;
import com.ubtechinc.push.UbtPushManager;

public class StartUpService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(UBTPGApplication.TAG, "StartUpService|onCreate");
//        getPushToken();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getPushToken();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getPushToken() {
        String name = "GoldenPig_A";
        String password = "123456";
        PushHttpProxy pushHttpProxy = new PushHttpProxy();
        pushHttpProxy.getToken(name, password, new PushHttpProxy.GetTokenCallback() {
            @Override
            public void onError(String error) {

            }

            @Override
            public void onSuccess(String token) {
                PushAppInfo pushAppInfo = new PushAppInfo();
                pushAppInfo.setToken(token);
                AuthLive.getInstance().setPushAppInfo(pushAppInfo);
                getPushAppInfo(token);
            }
        });
    }

    private void getPushAppInfo(String token) {
        String name = "GoldenPig";
        PushHttpProxy pushHttpProxy = new PushHttpProxy();
        pushHttpProxy.getAppInfo(name, token, new PushHttpProxy.GetAppInfoCallback() {
            @Override
            public void onError(String error) {

            }

            @Override
            public void onSuccess(PushAppInfo pushAppInfo) {
                AuthLive.getInstance().setPushAppInfo(pushAppInfo);
                long accessID = pushAppInfo.getAccessID();
                String accessKey = pushAppInfo.getAccessKey();
                UbtPushManager.getInstance().init(getApplicationContext(), accessID, accessKey, PushBrandType.XG, new PushListener());
            }
        });
    }
}

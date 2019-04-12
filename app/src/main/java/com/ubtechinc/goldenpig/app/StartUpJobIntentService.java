package com.ubtechinc.goldenpig.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.push.PushAppInfo;
import com.ubtechinc.goldenpig.push.PushHttpProxy;
import com.ubtechinc.goldenpig.push.PushListener;
import com.ubtechinc.push.PushBrandType;
import com.ubtechinc.push.UbtPushManager;

/**
 * @Description: ${DESCRIPTION}
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/11 15:13
 */
public class StartUpJobIntentService extends JobIntentService {

    static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, StartUpJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        getPushToken();
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

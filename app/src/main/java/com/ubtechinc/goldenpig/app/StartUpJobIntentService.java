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
        getPushAppInfo();
    }

    private void getPushAppInfo() {
        String name = "GoldenPig";
        PushHttpProxy pushHttpProxy = new PushHttpProxy();
        pushHttpProxy.getAppInfo(name, new PushHttpProxy.GetAppInfoCallback() {
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

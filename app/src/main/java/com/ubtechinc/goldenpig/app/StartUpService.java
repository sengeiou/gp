package com.ubtechinc.goldenpig.app;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.net.PushHttpProxy;

public class StartUpService extends IntentService {

    public StartUpService() {
        super("PushService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
//    public StartUpService(String name) {
//        super(name);
//    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

//        processPush();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("PushHttpProxy", "StartUpService|onCreate");
        processPush();
    }

    private void processPush() {
        String name = "GoldenPig_A";
        String password = "123456";
        PushHttpProxy pushHttpProxy = new PushHttpProxy();
        pushHttpProxy.getToken(name, password, new PushHttpProxy.GetTokenCallback() {
            @Override
            public void onError(String error) {

            }

            @Override
            public void onSuccess(String result) {

            }
        });
    }
}

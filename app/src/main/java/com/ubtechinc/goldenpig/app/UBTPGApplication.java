package com.ubtechinc.goldenpig.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.tencent.ai.tvs.LoginApplication;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.protocollibrary.communit.ProtoBufferDisposer;

/**
 * @des Ubt 金猪applicaption
 * @author  hqt
 * @time    2018/08/17
 */
public class UBTPGApplication extends LoginApplication {
    private static UBTPGApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        Stetho.initializeWithDefaults(this);
        UbtLogger.init(getApplicationContext());
        UbtLogger.i("", ProtoBufferDisposer.TAG);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static UBTPGApplication getInstance() {
        return instance;
    }
}

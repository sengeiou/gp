package com.ubtechinc.goldenpig.app;

import android.app.Application;

import com.tencent.ai.tvs.LoginApplication;
import com.ubtechinc.commlib.log.UbtLogger;

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
        UbtLogger.init(getApplicationContext());
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

package com.ubtechinc.goldenpig.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.tencent.ai.tvs.LoginApplication;
import com.tencent.ai.tvs.env.ELoginEnv;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.protocollibrary.communit.ProtoBufferDisposer;
import com.ubtechinc.tvlloginlib.TVSManager;

/**
 * @author hqt
 * @des Ubt 金猪applicaption
 * @time 2018/08/17
 */
public class UBTPGApplication extends LoginApplication {
    private static UBTPGApplication instance;
    static Context mContext;
    public static boolean voiceMail_debug = false;

    public static boolean pig_net_status = true;

    public static String mPairSerialNumber;

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
        if (BuildConfig.TVS_MODE) {
            TVSManager.eLoginEnv = ELoginEnv.FORMAL;
        } else {
            TVSManager.eLoginEnv = ELoginEnv.TEST;
        }
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

    public static Context getContext() {
        return mContext;
    }
}

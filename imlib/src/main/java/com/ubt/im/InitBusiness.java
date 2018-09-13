package com.ubt.im;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusiness {

    private static final String TAG = InitBusiness.class.getSimpleName();

    private InitBusiness(){}



    public static void start(Context context, int logLevel,int appid){
        initImsdk(context, logLevel,appid);
    }


    /**
     * 初始化imsdk
     */
    private static void initImsdk(Context context, int logLevel,int appId){
        TIMSdkConfig config = new TIMSdkConfig(appId)
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/");
        //初始化imsdk
         TIMManager.getInstance().init(context, config);
        //禁止服务器自动代替上报已读
        Log.d(TAG, "initIMsdk");

    }





}

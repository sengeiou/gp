package com.ubtechinc.goldenpig.net;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BaseHttpProxy {
    protected OkHttpClient getHttpClient(){
        OkHttpClient okHttpClient   = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        return  okHttpClient;
    }


}

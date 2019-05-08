package com.ubtechinc.goldenpig.main;


import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CheckMessageHttpProxy extends BaseHttpProxy {

    private static String URL = "http://10.10.1.14:8090/cloud-ppi/pig/sysInfo/getSysInfoStatus";

    public void getData(final CheckMessageCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        RequestBody body = RequestBody.create(JSON, "");
        String url = BuildConfig.HOST + "/cloud-ppi/pig/sysInfo/getSysInfoStatus";
        final Request okrequest = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UbtLogger.d("CheckMessageHttpProxy", "CheckMessageHttpProxy：" +e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("CheckMessageHttpProxy", result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {
                            String models = jsonObject.optString("models");
                            if (callback != null) {
                                if(models.equalsIgnoreCase("0")){
                                    callback.onSuccess(true);
                                }else{
                                    callback.onSuccess(false);
                                }

                            }
                        }
                    } else {
                        LogUtils.d("CheckMessageHttpProxy", "getData|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("CheckMessageHttpProxy", e.getMessage());
                }
            }
        });
    }

    public interface CheckMessageCallback {

        void onError(String error);

        void onSuccess(boolean show);
    }

}

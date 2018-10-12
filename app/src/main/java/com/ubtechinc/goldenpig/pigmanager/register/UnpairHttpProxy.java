package com.ubtechinc.goldenpig.pigmanager.register;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UnpairHttpProxy extends BaseHttpProxy {
    public void doUnpair(String token, String appId, String userId, final UnpairCallBack callBack){
        OkHttpClient okHttpClient =getHttpClient();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST+"/user-service-rest/v2/goldenPig/unpairRobot")
                .get()
                .addHeader("authorization",token)
                .addHeader("X-UBT-AppId",appId)
                .addHeader("product",BuildConfig.product)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack!=null){
                    callBack.onError();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack!=null) {
                    if (response.isSuccessful()){
                        try {
                            String result = response.body().source().readUtf8();
                            callBack.onSuccess();
                        } catch (RuntimeException e) {
                            callBack.onError();
                        }
                    }else {
                        callBack.onError();
                    }

                }

            }
        });
    }

    public interface  UnpairCallBack{
        void onError();
        void onSuccess();
    }
}

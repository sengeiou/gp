package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.util.Log;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UnbindPigProxy extends BaseHttpProxy{
    public void unbindPig(String serialNumber,String userId,String token,String appid,final UnBindPigCallback callback){

        OkHttpClient okHttpClient  = getHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("serialNumber", serialNumber)
                .add("unbindingUserId", userId)
                .build();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST+"/user-service-rest/v2/robot/common/unbinding")
                .post(formBody)
                .addHeader("authorization",token)
                .addHeader("X-UBT-AppId",appid)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback!=null) {
                    callback.onError(e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // if (loginListener!=null) {
                if (response != null) {

                    if (callback!=null) {
                        String result = response.body().source().readUtf8();
                        callback.onSuccess(result);
                    }


                }
                //}

            }
        });

    }
    public interface UnBindPigCallback{
        void onError(IOException e);
        void onSuccess(String reponse);
    }
}

package com.ubtechinc.goldenpig.personal.interlocution;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InterlocutionModel extends BaseHttpProxy {
    public void unbindPig(String serialNumber, String userId, String token, String appid, final
    UnBindPigCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("serialNumber", serialNumber)
                .add("unbindingUserId", userId)
                .build();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST + "/user-service-rest/v2/robot/common/unbinding")
                .post(formBody)
                .addHeader("authorization", token)
                .addHeader("X-UBT-AppId", appid)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // if (loginListener!=null) {
                if (response != null) {

                    if (callback != null) {
                        String result = response.body().source().readUtf8();
                        callback.onSuccess(result);
                    }


                }
                //}

            }
        });

    }

    public interface UnBindPigCallback {
        void onError(IOException e);

        void onSuccess(String reponse);
    }

    public interface InterlocutionCallback {
        void onError(IOException e);

        void onSuccess(String reponse);
    }

    public void getInterlocutionRequest(InterlocutionCallback callback) {
        StringBuilder sb = new StringBuilder();
        if (CookieInterceptor.get().getThridLogin().getLoginType().equals("wx")) {
            sb.append(2);
            sb.append("|");
        } else {
            sb.append(1);
            sb.append("|");
        }
        sb.append(CookieInterceptor.get().getThridLogin().getAppId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getOpenId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getAccessToken() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getMiniTvsId());

        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url("https://ddsdk.html5.qq.com/api/ugc")
                .get()
                .addHeader("dd-auth", sb.toString())
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    if (callback != null) {
                        String result = response.body().string();
                        LogUtils.d("InterlocutionModel", result);
                        callback.onSuccess(result);
                    }


                }
                //}

            }
        });
    }

}

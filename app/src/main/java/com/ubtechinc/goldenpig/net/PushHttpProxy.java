package com.ubtechinc.goldenpig.net;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PushHttpProxy extends BaseHttpProxy {

    public void getToken(String name, String password, final GetTokenCallback callBack) {

        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url(BuildConfig.PUSH_HOST + "/xinge-push-rest/openapi/token?" + "name=" + name + "&password=" + password)
                .get()
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack != null) {
                    try {
                        String result = response.body().source().readUtf8();
                        LogUtils.d("PushHttpProxy", result);
                        if (response.isSuccessful()) {
                            callBack.onSuccess(new JSONObject(result).optString("token"));
                        } else {
                            JSONObject jsonObject = new JSONObject(result);
                            String message = jsonObject.optString("message");
                            callBack.onError(message);
                        }
                    } catch (Exception e) {
                        callBack.onError(e.getMessage());
                    }
                }

            }
        });
    }

    public interface GetTokenCallback {
        void onError(String error);

        void onSuccess(String token);
    }
}

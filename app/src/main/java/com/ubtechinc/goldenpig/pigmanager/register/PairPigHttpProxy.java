package com.ubtechinc.goldenpig.pigmanager.register;

import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PairPigHttpProxy extends BaseHttpProxy {
    public void doPair(String token, String appId, String sign, final PairPigCallback callBack) {
        OkHttpClient okHttpClient = getHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("sign", sign)
                .build();
        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST + "/user-service-rest/v2/goldenPig/pairRobot ")
                .post(formBody)
                .addHeader("authorization", token)
                .addHeader("X-UBT-AppId", appId)
                .addHeader("product", BuildConfig.product)
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
                        if (response.isSuccessful()) {
                            callBack.onSuccess();
                        } else {
                            UBTLog.d("PairPigHttpProxy", result);
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

    public interface PairPigCallback {
        void onError(String error);

        void onSuccess();
    }
}

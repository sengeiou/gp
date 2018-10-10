package com.ubtechinc.goldenpig.pigmanager.register;

import android.content.Context;
import android.os.Handler;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :
 * @time :2018/9/28 0:26
 * @change :
 * @changetime :2018/9/28 0:26
 */
public class TransferAdminHttpProxy extends BaseHttpProxy {
    public void transferAdmin(Context context, String token, String pigId, String transUserId, final TransferCallback callback) {
        OkHttpClient okHttpClient = getHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("serialNumber", pigId)
                .add("transUserId", transUserId)
                .build();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST + "/user-service-rest/v2/robot/common/transferAdmin")
                .post(formBody)
                .addHeader("authorization", token)
                .addHeader("product", BuildConfig.product)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {

            Handler mainHandler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onException(e));
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (callback != null) {
                    mainHandler.post(() -> {
                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().source().readUtf8();
                                callback.onSuccess(result);
                            } catch (IOException e) {
                                callback.onException(e);
                            }

                        } else {
                            callback.onError(String.valueOf(response.code()));
                        }
                    });
                }

            }
        });
    }

    public interface TransferCallback {
        void onError(String error);

        void onException(Exception e);

        void onSuccess(String msg);
    }
}

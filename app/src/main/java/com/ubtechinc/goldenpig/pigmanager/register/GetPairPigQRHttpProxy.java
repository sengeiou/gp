package com.ubtechinc.goldenpig.pigmanager.register;

import android.content.Context;
import android.os.Handler;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :获取生成添加成员的二维码
 * @time :2018/9/22 14:17
 * @change :
 * @changetime :2018/9/22 14:17
 */
public class GetPairPigQRHttpProxy extends BaseHttpProxy {
    public void getPairPigQR(Context context, String token, String appId, final GetPairPigQRCallBack callBack) {
        OkHttpClient okHttpClient = getHttpClient();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOME_HOST + "goldenPig/getPairInfo")
                .get()
                .addHeader("authorization", token)
                .addHeader("X-UBT-AppId", appId)
                .addHeader("product", BuildConfig.product)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {

            Handler mHandler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    mHandler.post(() -> callBack.onError(e.getMessage()));

                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().source().readUtf8();
                            mHandler.post(() -> callBack.onSuccess(result));
                        } catch (RuntimeException e) {
                            mHandler.post(() -> callBack.onError(e.getMessage()));
                        }
                    } else {
                        mHandler.post(() -> callBack.onError(response.message()));
                    }
                }

            }
        });
    }

    public interface GetPairPigQRCallBack {
        void onError(String error);

        void onSuccess(String response);
    }
}

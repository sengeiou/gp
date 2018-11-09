package com.ubtechinc.goldenpig.net;

import android.text.TextUtils;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {

    private static final String TAG = "RequestInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = CookieInterceptor.get().getToken();
        Request request = chain.request()
                .newBuilder()
                .header("X-UBT-AppId", BuildConfig.APP_ID)
                .header("X-UBT-Sign", URestSigner.sign())
                .header("authorization", TextUtils.isEmpty(token) ? "" : token)
                .header("product", BuildConfig.product)
                .build();
        return chain.proceed(request);
    }
}

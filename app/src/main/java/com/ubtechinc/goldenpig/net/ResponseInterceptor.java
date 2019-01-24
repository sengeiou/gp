package com.ubtechinc.goldenpig.net;

import android.util.Log;

import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResponseInterceptor implements Interceptor {

    private static final String TAG = "ResponseInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            Event<Integer> event = new Event<>(EventBusUtil.SERVER_RESPONSE_UNAUTHORIZED);
            EventBusUtil.sendEvent(event);
        }

        Log.d(TAG, "url      =  : " + request.url());
        Log.d(TAG, "code     =  : " + response.code());
        Log.d(TAG, "message  =  : " + response.message());
        Log.d(TAG, "protocol =  : " + response.protocol());

        if (response.body() != null && response.body().contentType() != null) {
            MediaType mediaType = response.body().contentType();
            String string = response.body().string();
            Log.d(TAG, "mediaType =  :  " + mediaType.toString());
            Log.d(TAG, "string    =  : " + string);
            ResponseBody responseBody = ResponseBody.create(mediaType, string);
            return response.newBuilder().body(responseBody).build();
        } else {
            return response;
        }
    }
}

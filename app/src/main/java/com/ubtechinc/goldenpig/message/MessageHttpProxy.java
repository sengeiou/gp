package com.ubtechinc.goldenpig.message;

import android.content.Context;

import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageHttpProxy extends BaseHttpProxy {

    public void getData(final Context context,  final GetMessageCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        RequestBody body = RequestBody.create(JSON, "");

        String  url = BuildConfig.HOST + "pig/sysInfo/getSysInfo";

        final Request okrequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d("MessageHttpProxy ", "onFailure:" + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("MessageHttpProxy", result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {
                            String modelJson = jsonObject.optString("models");
                            List<MessageModel> data   = JsonUtils.stringToObjectList(modelJson, MessageModel.class);
                            LogUtils.d("MessageHttpProxy", "getData|success:" + data);

                            if (callback != null) {
                                callback.onSuccess(data);
                            }
                        }else{
                            if (callback != null) {
                                callback.onError("response failed");
                            }
                        }
                    } else {
                        LogUtils.d("MessageHttpProxy", "getData|fail" + result);
                        if (callback != null) {
                            callback.onError("response failed");
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("MessageHttpProxy", e.getMessage());
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                }
            }
        });
    }

    public interface GetMessageCallback {

        void onError(String error);

        void onSuccess(List<MessageModel> data );
    }


    public interface DeleteMessageCallback{
        void onError(String error);

        void onSuccess();
    }

    public interface ReportMessageCallback{
        void onError(String error);

        void onSuccess();
    }



    public void deleteMessage(String id, DeleteMessageCallback callback){

        OkHttpClient okHttpClient = getHttpClient();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        String content = JsonUtils.map2Json(map);
        RequestBody body = RequestBody.create(JSON, content);
        String url = BuildConfig.HOST + "pig/sysInfo/delSysInfo";

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("deleteMessage", result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {

                            if (callback != null) {
                                callback.onSuccess();
                            }
                        }
                    } else {
                        LogUtils.d("deleteMessage", "deleteMessage|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("deleteMessage", e.getMessage());
                }
            }
        });

    }


    public void reportMessage(String id, ReportMessageCallback callback){

        OkHttpClient okHttpClient = getHttpClient();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        String content = JsonUtils.map2Json(map);
        RequestBody body = RequestBody.create(JSON, content);
        String url = BuildConfig.HOST + "pig/sysInfo/reportStatus";

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("deleteMessage", result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {

                            if (callback != null) {
                                callback.onSuccess();
                            }
                        }
                    } else {
                        LogUtils.d("deleteMessage", "deleteMessage|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("deleteMessage", e.getMessage());
                }
            }
        });

    }

}

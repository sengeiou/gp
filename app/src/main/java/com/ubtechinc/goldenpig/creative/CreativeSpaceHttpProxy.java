package com.ubtechinc.goldenpig.creative;

import android.content.Context;

import com.google.gson.JsonObject;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.message.MessageHttpProxy;
import com.ubtechinc.goldenpig.message.MessageModel;
import com.ubtechinc.goldenpig.model.CreateModel;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreativeSpaceHttpProxy extends BaseHttpProxy {

    private static final String TAG = "CreativeSpaceHttpProxy";


    public void getData(int page, final GetCreativeCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();


        String  url = BuildConfig.HOST + "/v1/cloud-ppi/pig/statement/list?page="+page;
        if(!BuildConfig.SERVER_ENV.equals("FORMAL")){
            url = BuildConfig.HOST + "/cloud-ppi/pig/statement/list?page"+page;
        }

        final Request okrequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "onFailure:" + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d(TAG, result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {
                            JSONObject modelJson = jsonObject.getJSONObject("models");
                            List<CreateModel> data   = JsonUtils.stringToObjectList(modelJson.optString("records"), CreateModel.class);
                            LogUtils.d("CreativeSpaceHttpProxy", "getData|success:" + data);

                            if (callback != null) {
                                callback.onSuccess(data);
                            }
                        }else{
                            if (callback != null) {
                                callback.onError("response failed");
                            }
                        }
                    } else {
                        LogUtils.d(TAG, "getData|fail" + result);
                        if (callback != null) {
                            callback.onError("response failed");
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                }
            }
        });
    }

    public interface GetCreativeCallback {

        void onError(String error);

        void onSuccess(List<CreateModel> data );
    }


    public void addCreativeContent(JSONObject parmJson, final AddCreativeCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        RequestBody body = RequestBody.create(JSON, parmJson.toString());

        String  url = BuildConfig.HOST + "/v1/cloud-ppi/pig/statement/add";
        if(!BuildConfig.SERVER_ENV.equals("FORMAL")){
            url = BuildConfig.HOST + "/cloud-ppi/pig/statement/add";
        }

        final Request okrequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "addCreativeContent onFailure:" + e.getMessage());
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d(TAG, "addCreativeContent:" + result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {


                            if (callback != null) {
                                callback.onSuccess();
                            }
                        }else{
                            if (callback != null) {
                                callback.onError("response failed");
                            }
                        }
                    } else {
                        LogUtils.d(TAG, "getData|fail" + result);
                        if (callback != null) {
                            callback.onError("response failed");
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                }
            }
        });
    }


    public interface AddCreativeCallback {

        void onError(String error);

        void onSuccess( );
    }




}

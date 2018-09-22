package com.ubtechinc.goldenpig.personal.interlocution;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.model.InterlocutionItemModel;
import com.ubtechinc.goldenpig.model.JsonCallback;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InterlocutionModel extends BaseHttpProxy {
    Handler mHander = new Handler(Looper.getMainLooper());

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
                    mHander.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
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
            }
        });

    }

    public interface UnBindPigCallback {
        void onError(IOException e);

        void onSuccess(String reponse);
    }


    public interface InterlocutionCallback {
        void onError(String e);

        void onSuccess(List<InterlocutionItemModel> reponse);
    }

    public void getInterlocutionRequest(JsonCallback callback) {
        StringBuilder sb = new StringBuilder();
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            sb.append(0);
            sb.append("|");
        } else {
            sb.append(1);
            sb.append("|");
        }
        sb.append(CookieInterceptor.get().getThridLogin().getAppId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getOpenId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getAccessToken() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getTvsId());

        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url("https://ddsdk.html5.qq.com/api/ugc")
                .get()
                .addHeader("dd-auth", sb.toString())
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(callback);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (callback != null) {
//                    callback.onError(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response != null) {
//                    if (callback != null) {
//                        String result = response.body().string();
//                        LogUtils.d("InterlocutionModel", result);
//                        JSONObject jsonObject = null;
//                        try {
//                            jsonObject = new JSONObject(result);
//                            int code = jsonObject.getInt("code");
//                            if (code != 0) {
//                                callback.onError("数据异常，请重试");
//                                return;
//                            }
//                            Gson gson = new Gson();
//                            List<InterlocutionItemModel> list = gson.fromJson(jsonObject
//                                    .getJSONArray("skills").toString(), new
//                                    TypeToken<List<InterlocutionItemModel>>() {
//                                    }.getType());
//                            mHander.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    callback.onSuccess(list);
//                                }
//                            });
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            mHander.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    callback.onError("数据异常，请重试");
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//        });
    }

    public void addInterlocutionRequest(String strQuest, String strAnswer, JsonCallback callback) {
        StringBuilder sb = new StringBuilder();
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            sb.append(0);
            sb.append("|");
        } else {
            sb.append(1);
            sb.append("|");
        }
        sb.append(CookieInterceptor.get().getThridLogin().getAppId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getOpenId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getAccessToken() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getTvsId());

        OkHttpClient okHttpClient = getHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase()
                    .equals("wx") ? "0" : "1");
            obj.put("openId", CookieInterceptor.get().getThridLogin().getOpenId());
            obj.put("token", CookieInterceptor.get().getThridLogin().getAccessToken());
            obj.put("appId", CookieInterceptor.get().getThridLogin().getAppId());
            JSONArray questions = new JSONArray();
            JSONObject objQu = new JSONObject();
            objQu.put("strQuery", strQuest);
            questions.put(0, objQu);
            obj.put("questions", questions);
            JSONArray answers = new JSONArray();
            JSONObject objAn = new JSONObject();
            objAn.put("strText", strAnswer);
            answers.put(0, objAn);
            obj.put("answers", answers);
            obj.put("imgurl", "");
            obj.put("audiourl", "");
            LogUtils.d("hdf", obj.toString());
            RequestBody requestBody = RequestBody.create(JSON, obj.toString());
            final Request okrequest = new Request.Builder()
                    .url("https://ddsdk.html5.qq.com/api/ugc")
                    .post(requestBody)
                    .addHeader("dd-auth", sb.toString())
                    .addHeader("Content-Type", "application/json")
                    .build();
            Call call = okHttpClient.newCall(okrequest);
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteInterlocRequest(String id, JsonCallback callback) {
        StringBuilder sb = new StringBuilder();
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            sb.append(0);
            sb.append("|");
        } else {
            sb.append(1);
            sb.append("|");
        }
        sb.append(CookieInterceptor.get().getThridLogin().getAppId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getOpenId() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getAccessToken() + "|");
        sb.append(CookieInterceptor.get().getThridLogin().getTvsId());

        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url("https://ddsdk.html5.qq.com/api/ugc/" + id)
                .delete()
                .addHeader("dd-auth", sb.toString())
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(callback);
    }

    public void release() {
        mHander.removeCallbacksAndMessages(null);
        getHttpClient().dispatcher().cancelAll();
    }

}

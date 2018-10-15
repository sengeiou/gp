package com.ubtechinc.goldenpig.personal.interlocution;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.model.InterlocutionItemModel;
import com.ubtechinc.goldenpig.model.JsonCallback;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.tvlloginlib.TVSManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class InterlocutionModel extends BaseHttpProxy {
    Handler mHander = new Handler(Looper.getMainLooper());

//    public void unbindPig(String serialNumber, String userId, String token, String appid,String pId, final
//    UnBindPigCallback callback) {
//
//        OkHttpClient okHttpClient = getHttpClient();
//
//        FormBody formBody = new FormBody.Builder()
//                .add("serialNumber", serialNumber)
//                .add("unbindingUserId", userId)
//                .build();
//
//        final Request okrequest = new Request.Builder()
//                .url(BuildConfig.HOST + "/user-service-rest/v2/robot/common/unbinding")
//                .post(formBody)
//                .addHeader("authorization", token)
//                .addHeader("product",BuildConfig.product)
//                .addHeader("X-UBT-AppId", appid)
//                .build();
//        Call call = okHttpClient.newCall(okrequest);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (callback != null) {
//                    mHander.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            callback.onError(e);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // if (loginListener!=null) {
//                if (response != null) {
//                    if (callback != null) {
//                        String result = response.body().source().readUtf8();
//                        callback.onSuccess(result);
//                    }
//                }
//            }
//        });
//
//    }

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
        if (TextUtils.isEmpty(CookieInterceptor.get().getThridLogin().getTvsId())) {
            if (TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                    .APP_ID_QQ).info != null) {
                sb.append(TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                        .APP_ID_QQ).info.getTvsId());
            }
        } else {
            sb.append(CookieInterceptor.get().getThridLogin().getTvsId());
        }
        LogUtils.d("sb.toString():" + sb.toString());
        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url("https://ddsdk.html5.qq.com/api/ugc")
                .get()
                .addHeader("dd-auth", sb.toString())
                .addHeader("product", BuildConfig.product)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(callback);
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
        if (TextUtils.isEmpty(CookieInterceptor.get().getThridLogin().getTvsId())) {
            if (TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                    .APP_ID_QQ).info != null) {
                sb.append(TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                        .APP_ID_QQ).info.getTvsId());
            }
        } else {
            sb.append(CookieInterceptor.get().getThridLogin().getTvsId());
        }

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
                    .addHeader("product", BuildConfig.product)
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
        if (TextUtils.isEmpty(CookieInterceptor.get().getThridLogin().getTvsId())) {
            if (TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                    .APP_ID_QQ).info != null) {
                sb.append(TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                        .APP_ID_QQ).info.getTvsId());
            }
        } else {
            sb.append(CookieInterceptor.get().getThridLogin().getTvsId());
        }

        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url("https://ddsdk.html5.qq.com/api/ugc/" + id)
                .delete()
                .addHeader("dd-auth", sb.toString())
                .addHeader("product", BuildConfig.product)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(callback);
    }

    public void updateInterlocutionRequest(String strQuest, String strItemId, String strAnswer,
                                           String id, JsonCallback callback) {
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
        if (TextUtils.isEmpty(CookieInterceptor.get().getThridLogin().getTvsId())) {
            if (TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                    .APP_ID_QQ).info != null) {
                sb.append(TVSManager.getInstance(UBTPGApplication.getInstance(), BuildConfig.APP_ID_WX, BuildConfig
                        .APP_ID_QQ).info.getTvsId());
            }
        } else {
            sb.append(CookieInterceptor.get().getThridLogin().getTvsId());
        }

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
            objQu.put("strItemId", strItemId);
            questions.put(objQu);
            obj.put("questions", questions);
            JSONArray answers = new JSONArray();
            JSONObject objAn = new JSONObject();
            objAn.put("strText", strAnswer);
            answers.put(objAn);
            obj.put("answers", answers);
            obj.put("imgurl", "");
            obj.put("audiourl", "");
            LogUtils.d("hdf", obj.toString());
            RequestBody requestBody = RequestBody.create(JSON, obj.toString().getBytes());
            final Request okrequest = new Request.Builder()
                    .url("https://ddsdk.html5.qq.com/api/ugc/" + id)
                    .put(requestBody)
                    .addHeader("dd-auth", sb.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("product", BuildConfig.product)
                    .build();
            Call call = okHttpClient.newCall(okrequest);
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void release() {
        mHander.removeCallbacksAndMessages(null);
        getHttpClient().dispatcher().cancelAll();
    }

}

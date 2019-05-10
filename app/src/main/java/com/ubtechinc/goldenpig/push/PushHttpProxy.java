package com.ubtechinc.goldenpig.push;

import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
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

public class PushHttpProxy extends BaseHttpProxy {

    public void getAppInfo(String appName, final GetAppInfoCallback callBack) {

        OkHttpClient okHttpClient = getHttpClient();
        final Request okrequest = new Request.Builder()
                .url(BuildConfig.PUSH_HOST + "/appInfo?" + "appName=" + appName)
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
                            List<PushAppInfo> list = JsonUtils.stringToObjectList(result, PushAppInfo.class);
                            if (list != null && !list.isEmpty()) {
                                for (PushAppInfo pushAppInfo : list) {
                                    if ("a".equalsIgnoreCase(pushAppInfo.getDevice())) {
                                        callBack.onSuccess(pushAppInfo);
                                        break;
                                    }
                                }
                            }
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

    public void bindToken(int appId, String pushToken, String userId, String appVersion, String productNo,
                          final GetAppInfoCallback callBack) {

        OkHttpClient okHttpClient = getHttpClient();

        Map<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        map.put("token", pushToken);
        map.put("userId", Integer.parseInt(userId));
        map.put("appVersion", appVersion);
        map.put("productNo", productNo);
        String content = JsonUtils.map2Json(map);
        RequestBody body = RequestBody.create(JSON, content);
        final Request okrequest = new Request.Builder()
                .url(BuildConfig.PUSH_HOST + "userToken")
                .post(body)
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
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("PushHttpProxy", result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean bindStatus = jsonObject.optBoolean("bindStatus");
                        PushAppInfo pushAppInfo = AuthLive.getInstance().getPushAppInfo();
                        pushAppInfo.setBindStatus(bindStatus);
                        LogUtils.e("PushHttpProxy", "bindToken|bindStatus:" + bindStatus);
                    } else {
                        LogUtils.d("PushHttpProxy", "bindToken|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("PushHttpProxy", e.getMessage());
                }
            }
        });
    }

    public void pushToken(String title, String content, String userId, Map customMap,
                          int messageType) {

        OkHttpClient okHttpClient = getHttpClient();

        PushAppInfo pushAppInfo = AuthLive.getInstance().getPushAppInfo();
        Map<String, Object> map = new HashMap<>();
        map.put("appName", pushAppInfo.getAppName());
        map.put("appType", "a");
        map.put("title", title);
        map.put("content", content);
        map.put("userId", userId);
        map.put("customMap", customMap);
        map.put("messageType", messageType);
        map.put("openURL", "");
        RequestBody body = RequestBody.create(JSON, JsonUtils.map2Json(map));
        final Request okrequest = new Request.Builder()
                .url(BuildConfig.PUSH_HOST + "token")
                .post(body)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("PushHttpProxy", "pushToken|result:" + result);
                    if (response.isSuccessful()) {
                        LogUtils.e("PushHttpProxy", "pushToken|success:" + result);
                    } else {
                        LogUtils.d("PushHttpProxy", "pushToken|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("PushHttpProxy", e.getMessage());
                }
            }
        });
    }

    public interface GetTokenCallback {
        void onError(String error);

        void onSuccess(String token);
    }

    public interface GetAppInfoCallback {
        void onError(String error);

        void onSuccess(PushAppInfo appInfo);
    }

}

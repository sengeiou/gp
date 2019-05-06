package com.ubtechinc.goldenpig.main;

import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckUpdateHttpProxy extends BaseHttpProxy {

    private static final String URL = "https://prerelease.ubtrobot.com/cloud-ppi/pig/sys/update";

    public void checkUpdate( final GetFunctionCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        Map<String, Object> map = new HashMap<>();
        map.put("version", "V" + com.ubtechinc.goldenpig.BuildConfig.VERSION_NAME);
        map.put("clientType", "1");
        String content = JsonUtils.map2Json(map);
        RequestBody body = RequestBody.create(JSON, content);
        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOME_HOST + "/v1/cloud-ppi/pig/sys/update")
                .post(body)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().source().readUtf8();
                    LogUtils.d("CheckUpdateHttpProxy result:", result);
                   if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {
                            JSONObject modelJson = jsonObject.optJSONObject("models");
                            if(modelJson != null){
                                Type type = new TypeToken<UpdateInfoModel>() {
                                }.getType();
                                UpdateInfoModel model = JsonUtils.getObject(modelJson.toString(), type);

                                if (callback != null) {
                                    callback.onSuccess(model);
                                }
                            }else{
                                if (callback != null) {
                                    callback.noUpdate();
                                }
                            }

                        }
                    } else {
                        LogUtils.d("CheckUpdateHttpProxy", "getData|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("CheckUpdateHttpProxy", e.getMessage());
                }
            }
        });
    }

    public interface GetFunctionCallback {

        void onError(String error);

        void onSuccess(UpdateInfoModel updateInfoModel);
        void noUpdate();
    }

}

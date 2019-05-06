package com.ubtechinc.goldenpig.main;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;
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

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :首页功能卡片数据
 * @time :2018/12/24 17:20
 * @change :
 * @changetime :2018/12/24 17:20
 */
public class HomeDataHttpProxy extends BaseHttpProxy {

    private static String URL = "http://10.10.1.14:8090/cloud-ppi/pig/index";

    public void getData(final Context context, String category, String statement, final GetFunctionCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        Map<String, Object> map = new HashMap<>();
        map.put("md5_category", category);
        map.put("md5_statement", statement);
        map.put("version", "V" + com.ubtechinc.goldenpig.BuildConfig.VERSION_NAME);
        map.put("clientType", 1);
        String content = JsonUtils.map2Json(map);
        RequestBody body = RequestBody.create(JSON, content);
        String url = BuildConfig.HOST + "/v1/cloud-ppi/pig/index";
        final Request okrequest = new Request.Builder()
                .url(url)
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
                    LogUtils.d("HomeDataHttpProxy", result);
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean status = jsonObject.optBoolean("status");
                        if (status) {
                            JSONObject modelJson = jsonObject.optJSONObject("models");
                            Type type = new TypeToken<FunctionModel>() {
                            }.getType();
                            FunctionModel model = JsonUtils.getObject(modelJson.toString(), type);
                            LogUtils.d("HomeDataHttpProxy", "getData|success:" + model);
                            String md5_category = model.catetory.md5;
                            String md5_statement = model.statement.md5;
                            SharedPreferencesUtils.putString(context, "md5_category", md5_category);
                            SharedPreferencesUtils.putString(context, "md5_statement", md5_statement);
                            if (callback != null) {
                                callback.onSuccess(model);
                            }
                        }
                    } else {
                        LogUtils.d("HomeDataHttpProxy", "getData|fail" + result);
                    }
                } catch (Exception e) {
                    LogUtils.e("HomeDataHttpProxy", e.getMessage());
                }
            }
        });
    }

    public interface GetFunctionCallback {

        void onError(String error);

        void onSuccess(FunctionModel functionModel);
    }

}

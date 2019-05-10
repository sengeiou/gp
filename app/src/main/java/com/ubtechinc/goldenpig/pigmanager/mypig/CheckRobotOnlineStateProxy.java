package com.ubtechinc.goldenpig.pigmanager.mypig;

import com.ubt.imlibv2.bean.Utils;
import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :获取机器人在线状态
 * @time :2019/1/10 20:06
 * @change :
 * @changetime :2019/1/10 20:06
 */
public class CheckRobotOnlineStateProxy extends BaseHttpProxy {

    public void check(String serialNumber, final RobotStateCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        long time = System.currentTimeMillis();
        String signature = Utils.getSingal(time);

        HashMap<String, String> params = new HashMap<>();
        params.put("signature", signature);
        params.put("time", String.valueOf(time));
        params.put("accounts", serialNumber);
        params.put("channel", com.ubt.imlibv2.BuildConfig.IM_Channel);

        final Request okrequest = new Request.Builder()
                .url(getUrl(params))
                .get()
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
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    try {
                        String result = response.body().source().readUtf8();
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject returnMap = jsonObject.optJSONObject("returnMap");
                            String state =returnMap.optString(serialNumber);
                            if ("Online".equalsIgnoreCase(state)) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } else {
                            JSONObject jsonObject = new JSONObject(result);
                            String message = jsonObject.optString("returnMsg");
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                }
            }
        });

    }

    private String getUrl(HashMap<String, String> params) {
        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuilder stringBuilder = new StringBuilder(BuildConfig.IM_HOST + "isOnline");
        stringBuilder.append("?");

        for (int i = 0; i < params.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuilder.append(keys.next() + "=" + value);
            if (i != params.size() - 1) {
                stringBuilder.append("&");
            }

        }
        return stringBuilder.toString();
    }

    public interface RobotStateCallback {

        void onError(String msg);

        void onSuccess(boolean isOnline);
    }
}

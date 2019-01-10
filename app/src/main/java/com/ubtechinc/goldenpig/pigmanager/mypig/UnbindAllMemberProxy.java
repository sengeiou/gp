package com.ubtechinc.goldenpig.pigmanager.mypig;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :全部成员解绑
 *@time          :2019/1/10 16:17
 *@change        :
 *@changetime    :2019/1/10 16:17
*/
public class UnbindAllMemberProxy extends BaseHttpProxy {

    public void unbind(String serialNumber, final UnBindPigCallback callback) {

        OkHttpClient okHttpClient = getHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("serialNumber", serialNumber)
                .build();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST + "/user-service-rest/v2/robot/common/unbindingAll")
                .post(formBody)
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
                            callback.onSuccess();
                        } else {
                            JSONObject jsonObject = new JSONObject(result);
                            String message = jsonObject.optString("message");
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError(e.getMessage());
                    }
                }
            }
        });

    }

    public interface UnBindPigCallback {

        void onError(String msg);

        void onSuccess();
    }
}

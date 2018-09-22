package com.ubtechinc.goldenpig.pigmanager.register;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.nets.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :获取生成添加成员的二维码
 *@time          :2018/9/22 14:17
 *@change        :
 *@changetime    :2018/9/22 14:17
*/
public class GetAddMemberQRHttpProxy extends BaseHttpProxy {
    public void getMemberQR(String token,String appId,final GetMemberQRCallBack callBack){
        OkHttpClient okHttpClient =getHttpClient();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST+"/user-service-rest/v2/goldenPig/getCiphertext")
                .get()
                .addHeader("authorization",token)
                .addHeader("X-UBT-AppId",appId)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack!=null){
                    callBack.onError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack!=null) {
                    if (response.isSuccessful()){
                        try {
                            String result = response.body().source().readUtf8();

                        } catch (RuntimeException e) {
                            callBack.onError(e.getMessage());
                        }
                    }else {
                        callBack.onError(response.message());
                    }

                }

            }
        });
    }

    public interface GetMemberQRCallBack{
        void onError(String error);
        void onSuccess(String response);
    }
}

package com.ubtechinc.goldenpig.pigmanager.register;

import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;
import com.ubtechinc.goldenpig.net.GetRobotListModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.nets.BuildConfig;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :获取该用户所有关联的小猪
 *@time          :2018/9/12 17:39
 *@change        :
 *@changetime    :2018/9/12 17:39
*/
public class GetPigListHttpProxy extends BaseHttpProxy {
    public void getUserPigs(String token,String appid,String serialNumber,final OnGetPigListLitener listener) {
       // GetRobotListModule.Request request=new GetRobotListModule.Request();
        if (!TextUtils.isEmpty(serialNumber)) {
          //  request.setSerialNumber(serialNumber);
        }
        HashMap<String,String> hearder=new HashMap<>();
        hearder.put("authorization",token);
        hearder.put("X-UBT-AppId",appid);


        OkHttpClient okHttpClient =getHttpClient();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST+"/user-service-rest/v2/robot/common/queryRobotList")
                .get()
                .addHeader("authorization",token)
                .addHeader("X-UBT-AppId",appid)
                .addHeader("product",BuildConfig.product)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener!=null) {
                    listener.onException(e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
               // if (loginListener!=null) {
                    if (response != null) {
                        if (listener!=null) {
                            if (response.isSuccessful()){
                                try {
                                    String result = response.body().source().readUtf8();
                                    listener.onSuccess(result);
                                } catch (RuntimeException e) {
                                    listener.onError(new ThrowableWrapper(e,2222));
                                }
                            }else {
                                listener.onSuccess(response.message());
                            }

                        }
                        //loginListener.OnSuccess(result);
                    }
                //}

            }
        });
    }
    public interface OnGetPigListLitener{
        void onError(ThrowableWrapper e);
        void onException(Exception e);
        void onSuccess(String response);
    }
}

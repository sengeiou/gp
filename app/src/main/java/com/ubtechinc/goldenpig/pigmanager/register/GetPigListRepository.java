package com.ubtechinc.goldenpig.pigmanager.register;

import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.goldenpig.net.GetRobotListModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.nets.BuildConfig;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.io.IOException;
import java.util.HashMap;

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
public class GetPigListRepository {
    public void getUserPigs(String token,String appid,String serialNumber,final OnGetPigListLitener listener) {
       // GetRobotListModule.Request request=new GetRobotListModule.Request();
        if (!TextUtils.isEmpty(serialNumber)) {
          //  request.setSerialNumber(serialNumber);
        }
        HashMap<String,String> hearder=new HashMap<>();
        hearder.put("authorization",token);
        hearder.put("X-UBT-AppId",appid);
        // http://10.10.20.71:8010/user-service-rest/v2/robot/common/queryRobotLis
       /* HttpProxy.get().doGet(request, hearder,new ResponseListener<GetRobotListModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {

                listener.onError(e);
            }

            @Override
            public void onSuccess(GetRobotListModule.Response response) {
                listener.onSuccess(response);
            }
        });*/

        OkHttpClient okHttpClient = new OkHttpClient();

        final Request okrequest = new Request.Builder()
                .url(BuildConfig.HOST+"/user-service-rest/v2/robot/common/queryRobotList")
                .get()
                .addHeader("authorization",token)
                .addHeader("X-UBT-AppId",appid)
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                listener.onException(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
               // if (loginListener!=null) {
                    if (response != null) {

                        String result=response.body().source().readUtf8();
                        listener.onSuccess(result);
                        Log.e("loginListener",result);
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

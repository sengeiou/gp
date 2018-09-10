package com.ubtechinc.goldenpig.pigmanager.register;


import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.nets.CheckBindResponseListener;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;

/**
 * @Date: 2017/10/26.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :  获取与机器人关联的所有用户信息
 */

public class CheckRobotRepository {

    public void getRobotBindUsers(String searialNumber,final ICheckBindStateCallBack callback){
        CheckBindRobotModule.Request request = new CheckBindRobotModule.Request();
        request.setSerialNumber(searialNumber);
        HashMap<String,String> headers=new HashMap<>();


        HttpProxy.get().doGet(request, new ResponseListener<CheckBindRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError(e);
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                callback.onSuccess(response);
            }
        });
    }
    public void getRobotBindUsers(String searialNumber,String token,String appId,final ICheckBindStateCallBack callback){
        CheckBindRobotModule.Request request = new CheckBindRobotModule.Request();
        request.setSerialNumber(searialNumber);
        HashMap<String,String> headers=new HashMap<>();
        headers.put("authorization",token);

        
        HttpProxy.get().doGet(request, headers,new CheckBindResponseListener<CheckBindRobotModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError(e);
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                callback.onSuccess(response);
            }

            @Override
            public void onSuccessWithJson(String json) {
                callback.onSuccessWithJson(json);
            }
        });
    }

    public interface ICheckBindStateCallBack{
        public void onError(ThrowableWrapper e);

        public void onSuccess(CheckBindRobotModule.Response response);
        void onSuccessWithJson(String jsonStr);
    }
}

package com.ubtechinc.goldenpig.pigmanager.register;

import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.goldenpig.net.GetRobotListModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;

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
        GetRobotListModule.Request request=new GetRobotListModule.Request();
        if (!TextUtils.isEmpty(serialNumber)) {
            request.setSerialNumber(serialNumber);
        }
        HashMap<String,String> hearder=new HashMap<>();
        hearder.put("authorization",token);
        hearder.put("X-UBT-AppId",appid);
        // http://10.10.20.71:8010/user-service-rest/v2/robot/common/queryRobotLis
        HttpProxy.get().doGet(request, hearder,new ResponseListener<GetRobotListModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {

                listener.onError(e);
            }

            @Override
            public void onSuccess(GetRobotListModule.Response response) {
                Log.i("getUserPigs",response.getMsg());
                listener.onSuccess(response);
            }
        });
    }
    public interface OnGetPigListLitener{
        void onError(ThrowableWrapper e);
        void onSuccess(GetRobotListModule.Response response);
    }
}

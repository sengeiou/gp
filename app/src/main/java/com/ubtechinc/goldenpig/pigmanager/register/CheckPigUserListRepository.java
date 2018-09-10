package com.ubtechinc.goldenpig.pigmanager.register;

import android.util.Log;

import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;

 /**
  *@auther        :hqt
  *@email         :qiangta.huang@ubtrobot.com
  *@description   :
  *@time          :2018/9/6 16:35
  *@change        :
  *@changetime    :2018/9/6 16:35
 */

public class CheckPigUserListRepository {
    private static final String TAG = CheckPigUserListRepository.class.getSimpleName();
    public void checkPigUserList(String token,String userId, String srialNumber,String appid, final CheckUerListListener listener){

        RegisterRobotModule.Request request = new RegisterRobotModule().new Request();
        request.setUserId(userId);
        request.setSerialNumber(srialNumber);
        HashMap<String,String> hearder=new HashMap<>();
        hearder.put("authorization",token);
        hearder.put("X-UBT-AppId","100080018");
        HttpProxy.get().doPost(request, hearder,new ResponseListener<RegisterRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG, " registerRobot onError : ");

                listener.onError(e.getErrorBody());
            }

            @Override
            public void onSuccess(RegisterRobotModule.Response response) {
                Log.d(TAG, " registerRobot onSuccess : ");
                listener.onSuccess(response);
            }
        });
    }

    public interface CheckUerListListener{
         void onSuccess(RegisterRobotModule.Response response);

         void onError(String error);
    }
}

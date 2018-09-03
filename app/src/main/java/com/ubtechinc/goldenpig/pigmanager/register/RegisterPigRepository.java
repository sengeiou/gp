package com.ubtechinc.goldenpig.pigmanager.register;

import android.util.Log;



import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;

/**
 * @author：wululin
 * @date：2017/10/30 10:08
 * @modifier：ubt
 * @modify_date：2017/10/30 10:08
 * [A brief description]
 * version
 */

public class RegisterPigRepository {
    private static final String TAG = RegisterPigRepository.class.getSimpleName();
    public void registerRobot(String token, String appid, final RegisterRobotListener listener){

        RegisterRobotModule.Request request = new RegisterRobotModule().new Request();


        HashMap<String,String> hearder=new HashMap<>();
        hearder.put("authorization",token);
        hearder.put("X-UBT-AppId",appid);
        HttpProxy.get().doPost(request, hearder,new ResponseListener<RegisterRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG, " registerRobot onError : ");

                listener.onError();
            }

            @Override
            public void onSuccess(RegisterRobotModule.Response response) {
                Log.d(TAG, " registerRobot onSuccess : ");
                listener.onSuccess(response);
            }
        });
    }

    public interface RegisterRobotListener{
         void onSuccess(RegisterRobotModule.Response response);

         void onError();
    }
}

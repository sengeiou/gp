package com.ubtechinc.goldenpig.pigmanager.register;

import android.util.Log;



import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

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
    public void registerRobot(String userName, String userOnlyId, final RegisterRobotListener listener){
        Log.d(TAG, " registerRobot userName : " + userName);
        RegisterRobotModule.Request request = new RegisterRobotModule().new Request();
        request.setUserName(userName);
        request.setUserOnlyId(userOnlyId);

        HttpProxy.get().doPost(request, new ResponseListener<RegisterRobotModule.Response>() {
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
        public void onSuccess(RegisterRobotModule.Response response);

        public void onError();
    }
}

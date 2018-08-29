package com.ubtechinc.nets.im.business;
import android.content.Context;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.MD5Utils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.alpha.event.IMLoginResultEvent;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.im.constant.TimConstant;
import com.ubtechinc.nets.im.event.IMStateChange;
import com.ubtechinc.nets.im.model.UserInfo;
import com.ubtechinc.nets.im.modules.FindBindModule;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import static com.ubtechinc.alpha.im.IMCmdId.IM_CHANNEL;

//import com.ubtech.utilcode.utils.Log;


/**
 * Created by Administrator on 2016/11/29.
 */
public class LoginBussiness {

    public static final String TAG ="LoginBussiness";
    private static final String IM_GETINFO_URL="im/getInfo";
    private static LoginBussiness sInstance;
    private Context context;
    private UserInfo userInfo;
    private boolean loginSucc = false;
    NetworkHelper.NetworkInductor netWorkInductor = null;  //持有一下，防止被回收 ，NetworkHelper内部是弱引用持有
    public static LoginBussiness getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LoginBussiness.class) {
                if (sInstance == null) {
                    sInstance = new LoginBussiness(context);
                }
            }
        }
        return sInstance;
    }

    public void login(String userId) {
        loginSucc = false;
        LogUtils.d(TAG,"login --- userId = "+userId);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }

        userInfo.setId(userId);
        queryUserInfo();
    }

    public LoginBussiness(Context context) {
        this.context = context;
        init();
    }





    private void init() {


        NetworkHelper.sharedHelper().addNetworkInductor(netWorkInductor = new NetworkHelper.NetworkInductor() {

            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus networkStatus) {
                LogUtils.d(TAG,"onNetworkChanged----net available :"+NetworkHelper.sharedHelper().isNetworkAvailable());
                if (NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    if (!loginSucc) {
                        if (needQueryFromServer()) {
                            queryUserInfo(); //先从后台获取信息，再登录IM
                        } else {
                            //realLoginIM(); //IM sdk自身会在连上网络后进行重新登录
                        }
                    }
                } else {
                    loginSucc = false;
                }
            }
        });
    }


    private boolean needQueryFromServer() {
        if (userInfo == null || !userInfo.hasGetAllInfo()) {
            return true;
        }
        return false;
    }
    private void realLoginIM(){
        if (isLoginIM()) {
            return;
        }

    }

    public  boolean isLoginIM(){
//        boolean isLoginIM =false;
//
//        if(TIMManager.getInstance().getLoginUser() == null){
//            Log.i(TAG, "userInfo getLoginUser为null");
//        }
//        if(userInfo != null && userInfo.getId()!= null && TIMManager.getInstance().getLoginUser() != null){
//            isLoginIM =true;
//        }
//        Log.d(TAG,"isLoginIM : "+isLoginIM);
//        return isLoginIM;
        return loginSucc;
    }

    private int hasTryCount = 0;
    //需要监听网络变化，当连上网络时需要再次重试
    private void queryUserInfo() {

        LogUtils.d(TAG,"queryUserInfo----");

        final FindBindModule.Request bindRequest = new FindBindModule.Request();
        bindRequest.equipmentId = userInfo.getId();

        HashMap<String,String> map = new HashMap<>();
        long time = System.currentTimeMillis();
        String md5 = MD5Utils.md5("IM$SeCrET"+time,32);
        map.put("signature",md5);
        map.put("time",String.valueOf(time));
        map.put("userId",userInfo.getId());
        map.put("channel",IM_CHANNEL);
        HttpProxy.get().doGet(IM_GETINFO_URL,map,new ResponseListener<FindBindModule.Response>(){

            @Override
            public void onError(ThrowableWrapper e) {
                if (NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    hasTryCount++;
                    if (hasTryCount <= 3) {

                        LogUtils.e(TAG,"get---im/getInfo--onError--- try again--hasTryCount = " + hasTryCount);
                        try {
                            Thread.sleep(500*(hasTryCount+1));
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        queryUserInfo();
                    } else {
                        LogUtils.e(TAG,"get---im/getInfo--onError--- hasTryCount " + hasTryCount);

                    }
                } else {
                    LogUtils.e(TAG,"get---im/getInfo--onError--- no network");

                }
                e.getCause().printStackTrace();
            }

            @Override
            public void onSuccess(FindBindModule.Response response) {

                LogUtils.d(TAG,"get---im/getInfo--onSuccess---");

                hasTryCount = 0;
                   if (response != null) {
                        fillUserInfo(response.returnMap);
                        realLoginIM();
                    }
            }

        });

    }

    private void fillUserInfo(FindBindModule.RobotData userData) {

        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserSig(userData.userSig);
        userInfo.setAccountType(userData.accountType);
        userInfo.setAppidAt3rd(userData.appidAt3rd);
        Log.d(TAG,"fillUserInfo--userInfo = "+userInfo);
    }



    public void setUserId(String userId) {
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setId(userId);
    }

    public void logout() {

    }
}

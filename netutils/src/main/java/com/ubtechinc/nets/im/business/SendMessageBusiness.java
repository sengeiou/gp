package com.ubtechinc.nets.im.business;

import android.util.Log;


import com.ubtechinc.nets.im.IMErrorUtil;
import com.ubtechinc.nets.im.service.RobotPhoneCommuniteProxy;
import com.ubtechinc.nets.phonerobotcommunite.ISendMsg;

import java.io.File;

/**
 * Created by Administrator on 2016/12/17.
 */
public class SendMessageBusiness implements ISendMsg {

    public  final String TAG="SendRevMsgBusiness";

    private static SendMessageBusiness sInstance;

    public static SendMessageBusiness getInstance() {
        if (sInstance == null) {
            synchronized (SendMessageBusiness.class) {
                sInstance = new SendMessageBusiness();
            }
        }
        return sInstance;
    }

    private SendMessageBusiness() {
    }


    public void init() {

    }

    @Override
    public void sendMsg(long requestSerialId, String peer, byte[] bodyBytes, RobotPhoneCommuniteProxy.Callback callback) {
        sendTextMessageByIM(requestSerialId,peer,bodyBytes,callback);
    }

    private void sendTextMessageByIM(final long requestSerialId,String peer, byte[] bodyBytes, final RobotPhoneCommuniteProxy.Callback callback) {
        if (bodyBytes == null) {
            return;


        }

    }
}

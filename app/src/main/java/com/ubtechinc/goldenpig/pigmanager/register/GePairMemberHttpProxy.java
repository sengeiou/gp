package com.ubtechinc.goldenpig.pigmanager.register;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;

/**
 * 获取配对八戒的用户列表
 */
public class GePairMemberHttpProxy extends BaseHttpProxy {
    public void getPairMember(String token, String appId,final GetPairMemberCallback callBack){

    }
    public interface  GetPairMemberCallback{
        void onError();
        void onSuccess();
    }
}

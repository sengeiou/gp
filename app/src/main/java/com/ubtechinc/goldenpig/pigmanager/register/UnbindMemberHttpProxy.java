package com.ubtechinc.goldenpig.pigmanager.register;

import com.ubtechinc.goldenpig.net.BaseHttpProxy;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :成员组解绑
 *@time          :2018/9/20 17:19
 *@change        :
 *@changetime    :2018/9/20 17:19
*/
public class UnbindMemberHttpProxy extends BaseHttpProxy {

    public void doUnbind(String appId,String token,String pigNo,String userId,final UnbindCallBack callBack){

    }
    public interface UnbindCallBack{
        void onError(String err);
        void onSuccess();
    }
}

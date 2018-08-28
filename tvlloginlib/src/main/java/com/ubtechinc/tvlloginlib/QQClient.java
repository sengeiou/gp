package com.ubtechinc.tvlloginlib;

import android.content.Intent;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.connect.common.Constants;


public class QQClient extends BaseClient {

    public QQClient(LoginProxy proxy, ClientResultListener listener) {
        super(proxy, ELoginPlatform.QQOpen, listener);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                proxy.handleQQOpenIntent(requestCode, resultCode, data);
            }else{
                if(listener != null){
                    listener.onClientCancel();
                }
            }
        }
    }

    @Override
    public void onResume() {
        proxy.handleCheckQQOpenTokenValid();
    }
}

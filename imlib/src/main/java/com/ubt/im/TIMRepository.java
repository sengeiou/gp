package com.ubt.im;

import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;

public class TIMRepository {
    private OnTIMLoginListener loginListener;
    public void login(String singa, String time, String userId, String channel) {
        final TIMModule.LoginRequest request = new TIMModule().new LoginRequest();
        request.setChannel(channel);
        request.setSignature(singa);
        request.setTime(time);
        request.setUserId(userId);
        HashMap<String,String> parma=new HashMap<>();
        /*parma.put("signature",singa);
        parma.put("time",time);
        parma.put("userId",userId);
        parma.put("channel",channel);*/

        HttpProxy.get().doGet(request,parma, new ResponseListener<TIMModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                if (loginListener!=null){
                    loginListener.onFailure(e.getMessage());
                }
            }

            @Override
            public void onSuccess(TIMModule.Response response) {
                if (loginListener!=null){
                    loginListener.OnSuccess(response.getMessage());
                }
            }
        });


    }

    public void setLoginListener(OnTIMLoginListener loginListener) {
        this.loginListener = loginListener;
    }
}
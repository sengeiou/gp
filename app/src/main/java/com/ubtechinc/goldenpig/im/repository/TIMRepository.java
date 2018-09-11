package com.ubtechinc.goldenpig.im.repository;

import com.ubtechinc.goldenpig.im.TIMModule;
import com.ubtechinc.goldenpig.login.ThirdPartLoginModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;

public class TIMRepository {

    public void login() {
        final TIMModule.LoginRequest request = new TIMModule().new LoginRequest();
        HashMap<String, String> heraders = new HashMap<>();
        HttpProxy.get().doPut(request, heraders, new ResponseListener<TIMModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {

            }

            @Override
            public void onSuccess(TIMModule.Response response) {

            }
        });
    }
}
package com.ubt.imlibv2.bean;

import android.util.Log;


import com.ubt.imlibv2.bean.listener.OnTIMLoginListener;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TIMRepository extends Repository{
    private OnTIMLoginListener loginListener;
    public void login(String singa, String time, String userId, String channel) {
       /* final TIMModule.LoginRequest request = new TIMModule().new LoginRequest();
        request.setChannel(channel);
        request.setSignature(singa);
        request.setTime(time);
        request.setUserId(userId);*/
        HashMap<String,String> parma=new HashMap<>();
        /*parma.put("signature",singa);
        parma.put("time",time);
        parma.put("userId",userId);
        parma.put("channel",channel);*/

       /* HttpProxy.get().doGet(request,parma, new ResponseListener<TIMModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                if (loginListener!=null){
                    loginListener.onFailure(e.getMessage());
                }
            }

            @Override
            public void onSuccess(TIMModule.Response response) {
                if (loginListener!=null){
                    loginListener.OnSuccess(response.getMsg());
                }
            }
        });*/
        OkHttpClient okHttpClient = new OkHttpClient();
        parma.put("signature",singa);
        parma.put("time",time);
        parma.put("userId",userId);
        parma.put("channel",channel);
        final Request okrequest = new Request.Builder()
                .url(getIMLoginUrl(parma))
                .get()
                .build();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (loginListener!=null){
                    loginListener.onFailure(e.getMessage());
                }

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (loginListener!=null) {
                    if (response != null) {

                        String result=response.body().source().readUtf8();

                        Log.e("loginListener",result);
                        loginListener.OnSuccess(result);
                    }
                }

            }
        });



    }

    public void setLoginListener(OnTIMLoginListener loginListener) {
        this.loginListener = loginListener;
    }
}
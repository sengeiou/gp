package com.ubt.imlibv2.bean;

import android.util.Log;


import com.ubt.imlibv2.bean.listener.OnPigOnlineStateListener;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TIMPigOnLineRepository extends Repository{
    private OnPigOnlineStateListener onlineStateListener;
    public void getPligOnLineState(String singa, String time, final String accounts, String channel, final String msg){
        HashMap<String,String> parma=new HashMap<>();
        parma.put("signature",singa);
        parma.put("time",time);
        parma.put("accounts",accounts);
        parma.put("channel",channel);
        final Request okrequest = new Request.Builder()
                .url(getIMLoginUrl(parma))
                .get()
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        Call call = okHttpClient.newCall(okrequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (onlineStateListener!=null){
                    onlineStateListener.onFailure(e.getMessage());
                }

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (onlineStateListener!=null) {
                    if (response != null) {

                        String result=response.body().source().readUtf8();

                        Log.e("loginListener",result);
                        onlineStateListener.OnSuccess(accounts,result,msg);
                    }
                }

            }
        });
    }
    public void getPligOnLineState(String singa, String time,String[] accountArray,String channel,String msg){
        if (accountArray!=null){
            StringBuilder stringBuilder=new StringBuilder();
            final int length=accountArray.length;
            for(int index=0;index<length;index++){
                stringBuilder.append(accountArray[index]);
                if (index<length-1){
                    stringBuilder.append(",");
                }
            }
            getPligOnLineState(singa,time,stringBuilder.toString(),channel,msg);
        }

    }
    public void setLoginListener(OnPigOnlineStateListener loginListener) {
        this.onlineStateListener = loginListener;
    }
    public void removeLoginListener() {
        this.onlineStateListener = null;
    }
}

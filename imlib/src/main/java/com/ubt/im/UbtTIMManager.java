package com.ubt.im;


import com.ubtech.utilcode.utils.MD5Utils;
import com.ubtechinc.commlib.log.UbtLogger;


/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :处理IM功能管理类
 *@time          :2018/9/10 18:10
 *@change        :
 *@changetime    :2018/9/10 18:10
*/
public class UbtTIMManager {
    private static volatile UbtTIMManager instance;
    private boolean isLoginedTIM; ///是否登录IM
    private TIMRepository repository;
    private String channel;
    private String userId;
    private UbtTIMManager(){
        repository=new TIMRepository();
        setTIMLoginListener();
    }

    public static UbtTIMManager getInstance(){
        if (instance==null)
        synchronized (UbtTIMManager.class){
            if(instance==null){
                instance=new UbtTIMManager();
            }
        }
        return instance;
    }

    public void loginTIM(String userId,String channel){
        this.userId=userId;
        this.channel=channel;
        if (repository!=null){
            long time = System.currentTimeMillis();
            String singa = MD5Utils.md5("IM$SeCrET"+time,32);
            repository.login(singa,String.valueOf(time),userId,channel);
        }
    }

    private void setTIMLoginListener() {
        if (repository!=null){
            repository.setLoginListener(new OnTIMLoginListener() {
                @Override
                public void onFailure(String error) {
                    UbtLogger.e("setTIMLoginListener",error);
                }

                @Override
                public void OnSuccess(String msg) {

                }
            });
        }
    }

    public void send(String msg){
        if (!isLoginedTIM){
            loginTIM(userId, channel);
        }
    }

    public boolean isLoginedTIM(){
        return isLoginedTIM;
    }



}

package com.ubtechinc.goldenpig.im;



/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :处理IM功能管理类
 *@time          :2018/9/10 18:10
 *@change        :
 *@changetime    :2018/9/10 18:10
*/
public class TIMMode{
    private static volatile TIMMode instance;
    private boolean isLoginTim; ///是否登录IM
    private TIMMode(){}

    public static TIMMode getInstance(){
        if (instance==null)
        synchronized (TIMMode.class){
            if(instance==null){
                instance=new TIMMode();
            }
        }
        return instance;
    }

    public void loginTim(String userNam,String pwd){

    }
}

package com.ubtechinc.goldenpig.login;
/**
 *@auther        :hqt
 *@description   :这是ILoginView接口定义
 *@time          :2018/8/21 11:12
 *@change        :
 *@changetime    :2018/8/21 11:12
*/
public interface ILoginView {
    /**
     *@auther        :hqt
     *@description   : 登录MVP中View接口定义显示登录等待
     *@parma         :
     *@return        :
     *@exception     :  
    */
    void showLoginLoading();
    /**
     *@auther        :hqt
     *@description   :登录MVP中View接口定义取消登录等待
     *@parma         :
     *@return        :
     *@exception     :  
    */
    void hindeLoginLoading();
    
    /**
     *@auther        :hqt
     *@description   :判断权限函数
     *@parma         :
     *@return        :
     *@exception     :  
    */
    void judePermisson();
}

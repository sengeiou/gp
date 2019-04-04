package com.ubtechinc.goldenpig.app;


/**
 * 网络请求URL，当前环境有：测试、预发布、正式环境
 *
 */

public class HttpEntity {

    //业务相关url
    public static final String BASE_BUSSINESS_URL = "https://internal.ubtrobot.com/v1/cloud-ppi/"; //正式环境，后续根据配置在build文件中

    public static final String  HOME_DATA_REQUEST = BASE_BUSSINESS_URL + "index";
    public static final String  APP_UPDATE_REQUEST = BASE_BUSSINESS_URL + "pig/sys/update";

    //登录相关url
    public static final String BASE_LOGIN_URL = "https://internal.ubtrobot.com/user-service-rest/v2"; //正式环境，后续根据配置在build文件中



    //信鸽推送
    public static final String XINGE_PUSH_URL = "https://internal.ubtrobot.com/xinge-push-rest/push/";

    //IM
    public static final String IM_URL = "https://internal.ubtrobot.com/im/";

    //数据采集
    public static final String DATA_COLLECTION_URL = "https://internal.ubtrobot.com/v1/collect-rest";

    //OTA
    public static final String OTA_URL = "https://internal.ubtrobot.com/v1/upgrade-rest";

}

package com.ubtechinc.bluetooth;

import android.util.SparseArray;

public class Constants {

    public static final SparseArray analysisBtn = new SparseArray();

    public static final String ROBOT_BBMINI = "LittlePig";//机器人标志

    public static final String ROBOT_TAG = "BaJie_";//机器人显示的TAG

    public static final String DATA_COMMAND = "co";//蓝牙传输指令

    public static final String WIIF_LIST_COMMAND = "p";// 机器人响应的WiFi列表

    public static final int WIFI_LIST_TRANS = 1;// WiFi列表的请求和响应

    public static final int WIFI_LIST_RESLUT_TRANS = 101; //返回wifi列表的指令

    public static final int WIFI_INFO_TRANS = 2;// WIFI 信息发送

    public static final int WIFI_INFO_TRANS_FOR_BAINGD = 21;// 绑定联网

    public static final int ROBOT_CONNENT_WIFI_SUCCESS = 102;//机器人连接WiFi成功指令

    public static final int ROBOT_BLE_NETWORK_FAIL = -1;//机器人蓝牙配网失败

    public static final int CLIENT_ID_TRANS = 3;// 发送client id 命令

    public static final int ROBOT_BANGDING_SUCCESS = 103;//机器人绑定成功指令

    public static final int CONNECT_SUCCESS = 4;//蓝牙连接成功指令

    public static final int ROBOT_CONNECT_SUCCESS = 104; //机器人回复手机端连接成功指令

    public static final int ROBOT_WIFI_IS_OK_TRANS = 5;//发送机器人是否在线

    public static final int ROBOT_NETWORK_NOT_AVAILABLE = 7;//手机网络无效

    public static final int ROBOT_REPLY_WIFI_IS_OK_TRANS = 105;//机器人回复WiFi是否连接指令

    public static final int PIG_NET_STATE = 20;///查询小猪网络情况

    public static final int PIG_REPLY_NET_STATE = 120; ////小猪返回网络状态

    public static final int SEND_PIG_CHECK_NET_STATE = 6;  //发给小猪check网络状态

    public static final String REQUEST_SHARE_TIME = "request_share_time";
    public static String CODE = "cd";
    public static final String ERROR_CODE = "ec";

    public static final String PRODUCTID = "pid";

    public static final String SERISAL_NUMBER = "sid";

    public static final String CLIENTID = "cid";

    public static final String USERID = "uid";

    /**
     * ping 网址失败的错误码
     */
    public static final int PING_ERROR_CODE = 2;

    /**
     * 登录TVS错误码
     */
    public static final int TVS_LOGIN_ERROR_CODE = 3;

    /**
     * WiFi密码不合法错误码
     */
    public static final int PASSWORD_VALIDATA_ERROR_CODE = 1;

    /**
     * 连接WiFi超时错误码
     */
    public static final int CONNECT_TIME_OUT_ERROR_CODE = 4;

    /**
     * 已经有设备连接机器人错误码
     */
    public static final int ALEARDY_CONNECT_ERROR_CODE = 5;

    /**
     * 注册tvs失败错误码
     */
    public static final int TVS_ERROR_CODE = 6;

    /**
     * 绑定机器人失败错误码
     */
    public static final int REGISTER_ROBOT_ERROR_CODE = -1;

    /**
     * 获取clientId失败错误码
     */
    public static final int GET_CLIENT_ID_ERROR_CODE = -2;


    /**
     * 发送蓝牙信息失败
     */
    public static final int BLUETOOTH_SEND_FIAL = -3;

    /**
     * 没有序列号错误码
     */
    public static final int ON_SERAIL_ERROR_CODE = 7;

    /**
     * 蓝牙断开连接
     */
    public static final int BLE_LOST_CONNECT = 9;

    /**
     * 已经被绑定
     */
    public static final int ALREADY_BADING = 8;

    public static final int CODE_0 = 0;

    public static final int CODE_1 = 1;


    /**
     * 共享权限界面
     */
    public static int PERMISSION_REQUEST_KEY = 1024;

    public static final String CUSTOMERS_CALL = "4006666700";

    public static String getErrorMsg(int errorCode) {
        String reuslt = "";
        switch (errorCode) {
            case PING_ERROR_CODE:
                reuslt = "网址失败错误";
                break;
            case TVS_LOGIN_ERROR_CODE:
                reuslt = "登录TVS错误";
                break;
            case ALEARDY_CONNECT_ERROR_CODE:
                reuslt = "已经有设备连接音箱";
                break;
            case TVS_ERROR_CODE:
                reuslt = "注册tvs失败";
                break;
            case ON_SERAIL_ERROR_CODE:
                reuslt = "没有序列号错误";
                break;
            case REGISTER_ROBOT_ERROR_CODE:
                reuslt = "绑定音箱失败";
                break;
            case GET_CLIENT_ID_ERROR_CODE:
                reuslt = "获取clientId失败";
                break;
            case BLUETOOTH_SEND_FIAL:
                reuslt = "蓝牙发送失败";
                break;
            case BLE_LOST_CONNECT:
                reuslt = "蓝牙断开连接";
                break;
            case ALREADY_BADING:
                reuslt = "ALREADY_BADING";
                break;
            default:
                reuslt = "未知错误" + errorCode;
                break;
        }

        return reuslt;
    }
}

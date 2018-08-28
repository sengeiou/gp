package com.ubtechinc.alpha.im;

import com.ubtechinc.nets.BuildConfig;

/**
 * Created by Administrator on 2017/5/29.
 * 手机与机器人通信的命令字
 * 命名规范： IM_XXX_REQUEST,IM_XXX_RESPONSE
 */

public class IMCmdId {

    public static final  String IM_VERSION = "1";

    public static final String IM_CHANNEL = BuildConfig.IM_CHANNEL;

    //request
    public static final int RESPONSE_BASE = 1000;

/***************************************客户端请求************************************************/

    /**
     * 删除动作文件
     */
    static public final int IM_DELETE_ACTIONFILE_REQUEST = 4;
    /**
     * 执行动作表
     */
    static public final int IM_PLAY_ACTION_REQUEST = 5;
    /**
     * 获取舵机角度值
     */
    static public final int IM_GET_MOTORANGLE_REQUEST = 10;
    /**
     * 停止执行动作
     */
    static public final int IM_STOP_PLAY_REQUEST = 11;
    /** 启动第三方app **/
    public static final int IM_START_APP_REQUEST = 30;
    /** 退出第三方app **/
    public static final int IM_STOP_APP_REQUEST = 31;
    /** 获取全部第三方app **/
    public static final int IM_GET_ALLAPPS_REQUEST  = 35;
    /** 设置调试模式 **/
    public static final int IM_SET_DEBUGMODE_REQUEST = 36;
    /** 设置单个舵机角度 **/
    public static final int IM_SET_MOTOANGLE_REQUEST = 9;
    /** 安装第三方 **/
    public static final int IM_INSTALL_PACKAGES_REQUEST = 38;
    /** 卸载第三方 **/
    public static final int IM_UNINSTALL_PACKAGES_REQUEST = 39;
    /** 更新第三方 **/
    public static final int IM_UPDATE_PACKAGES_REQUEST = 40;
    /** 获取第三方应用配置信息 **/
    public static final int IM_GET_APPCONFIG_REQUEST = 41;
    /** 保存第三方配置信息 **/
    public static final int IM_SAVE_APPCONFIG_REQUEST = 42;
    /** 获取当前APP **/
    public static final int IM_GET_TOP_APP_REQUEST = 44;
    /** 请求获取app 按钮事件 **/
    public static final int IM_GET_APP_BUTTONEVENT_REQUEST = 45;
    /** 请求执行app 按钮 **/
    public static final int IM_CLICK_APP_BUTTON_REQUEST = 46;
    /** 动作下载 **/
    public static final int IM_DOWNLOAD_ACTIONFILE_REQUEST = 47;
    /** 闹钟功能请求 **/
    public static final int IM_DESKCLOCK_MANAGE_REQUEST = 49;
    /** 获取有效的闹钟列表请求 **/
    public static final int IM_DESKCLOCK_ACTIVIE_LIST_REQUEST = 50;
    /** 传送照片 **/
    public static final int IM_TRANSFER_PHOTO_REQUEST = 53;
    /** 恢复出厂设置 **/
    public static final int IM_MASTER_CLEAR_REQUEST = 58;
    /** 设置RTC时间 **/
    public static final int IM_SET_RTC_TIME_REQUEST = 59;
    /** 边充边玩 **/
    public static final int IM_SET_CHARGE_AND_PLAY_REQUEST = 60;
    /** 找手机 **/
    public static final int IM_FIND_MOBILEPHONE_REQUEST = 62;

    /**批量删除闹钟*/
    public static final int IM_DELETE_FORMER_CLOCK_REQUEST=63;
    /**获取所有闹钟*/
    public static final int IM_GET_FORMER_CLOCK_REQUEST=64;
    /**请求所有的缩略图*/
    public static final int IM_GET_ALL_THUMBNAIL_REQUEST =66;
    /**查询机器人电量*/
    public static final int IM_QUERY_ROBOT_POWER_REQUEST=68;
    /**获取机器人软硬件版本号*/
    public static final int IM_QUERY_HARD_SOFT_WARE_VERSION_REQUEST=69;
    /**获取动作列表*/
    public static final int IM_GET_NEW_ACTION_LIST_REQUEST =70;
    /**获取机器人初始化参数 状态信息  与72合并*/
    public static final int IM_GET_ROBOT_INIT_STATUS_REQUEST =72;
    /**管理机器人蓝牙 待确认*/
    public static final int IM_MANAGER_ROBOT_BLUETOOTH_REQUEST=74;
    /**闲聊tts动作 &呼吸动作开关*/
    public static final int IM_TTS_BREATH_ACTION_ON_OFF_REQUEST=90;
    /**语音合成指令*/
    public static final int IM_SYN_SPEECH_REQUEST=91;
    /**机器人错误日志开关*/
    public static final int IM_CLOSE_ROBOT_ERROR_LOG_REQUEST =92;
    /**查询机器人空间 app列表*/
    public static final int IM_QUERY_ROBOT_STORAGE_APP_LIST_REQUEST =93;
    /**批量卸载apps*/
    public static final int IM_UNINSTALL_BATCH_APPS_REQUEST=94;
    /**重新播放答案内容*/
    public static final int IM_RETRY_PLAY_ANSWER_REQUEST =95;
    /**连接机器人 control*/
    public static final int IM_CONNECT_ROBOT_REQUEST = 97;
    /**断开机器人 control*/
    public static final int IM_DISCONNECT_ROBOT_REQUEST = 98;
    /**设置主人名称*/
    public static final int IM_SET_MASTER_NAME_REQUEST = 99;
    /**获取闲聊tts动作 &呼吸动作开关*/
    public static final int IM_GET_TTS_BREATH_ACTION_ON_OFF_REQUEST=100;
    /** 获取边充边玩 **/
    public static final int IM_GET_CHARGE_AND_PLAY_REQUEST = 101;

    public static final int IM_GET_AGORA_ROOM_INFO_REQUEST = 109;

    public static final int IM_START_FACE_DETECT_REQUEST = 113;//启动人脸识别
    public static final int IM_FACE_UPDATE_REQUEST = 114; //修改人脸信息
    public static final int IM_FACE_DELETE_REQUEST = 115; //删除人脸信息
    public static final int IM_FACE_LIST_REQUEST = 116; //人脸列表信息
    public static final int IM_FACE_EXIT_REQUEST = 117; //退出人脸识别
    public static final int IM_FACE_CHECK_STATE_REQUEST = 118; //人脸状态检测

    public static final int IM_CHECK_SIM_REQUEST = 119; //确定是否有SIM
    public static final int IM_CONTACT_IMOPORT_REQUEST = 120; //联系人导入
    public static final int IM_CONTACT_QUERY_REQUEST = 121; //联系人查询
    public static final int IM_CONTACT_ADD_REQUEST = 122; //联系人添加
    public static final int IM_CONTACT_MODIFY_REQUEST = 123; //联系人修改
    public static final int IM_CONTACT_DELETE_REQUEST = 124; //联系人删除
    public static final int IM_CALLRECORD_REQUEST = 125; //通话记录
    public static final int IM_MOBILE_NET_STATE_REQUEST = 126; //蜂窝网络状态
    public static final int IM_MOBILE_NET_MODIFY_REQUEST = 127; //蜂窝网络开关
    public static final int IM_MOBILE_ROAM_MODIFY_REQUEST = 128; //蜂窝网络漫游开关

    /** 发送文件到机器人 **/
    public static final int IM_GET_TRANS_FILE_REQUEST= 201 ;
    /** 导出文件名，获取导出资源 **/
    public static final int IM_GET_EXPORT_FILE__REQUEST= 203 ;
    /** 获取某一导出文件详细信息 **/
    public static final int IM_GET_DETAIL_FILE_INFO_REQUEST= 204 ;
    /** 仿真机器人Led灯 **/
    public static final int IM_GET_EMULATE_LED_REQUEST= 205 ;
    /** 停止仿真机器人Led灯 **/
    public static final int IM_STOP_EMULATE_LED_REQUEST= 206 ;
    /** 读取软件版本 **/
    public static final int IM_GET_SOFTWARE_VERSION_REQUEST= 207 ;
    /** 设置所有舵机角度 **/
    public static final int IM_SET_ALL_MOTORS_ANGLE_REQUEST= 209 ;
    /** 进入fastboot模式 **/
    public static final int IM_ENTER_FASTBOOT_MODE_REQUEST= 211 ;

    /** 舵机信息处理**/
    public static final int IM_ENTER_MOTOR_INFO_REQUEST= 301 ;

    /**PC通信灯控制**/
    public static final int IM_GET_EMULATING_LED_REQUEST=102;
    public static final int IM_CONFIRM_ONLINE_REQUEST=106; //手机端发起的Ping包协议，用来判断机器人是否在线

	/** 接入腾讯TVS专用，用来传输 accessToken,freshToken等账号相关的信息 **/
    public static final int IM_SEND_TVS_ACCESSTOKEN_REQUEST = 107;
    public static final int IM_GET_TVS_PRODUCTID_REQUEST = 108;

    /**pc获取设置序列号**/
    public static final int IM_GET_SERIAL_NUMBER_REQUEST=303;
    public static final int IM_SET_SERIAL_NUMBER_REQUEST=302;

    /***获取机器人的配置信息***/
    public static final int IM_GET_ROBOT_CONFIG_REQUEST = 305;

    /****发送WiFi名称给机器人***/
    public static final int IM_SEND_WIFI_TO_ROBOT_REQUEST = 306;

    /***发送获取机器人WiFi列表**/
    public static final int IM_GET_WIFI_LIST_TO_ROBOT_REQUEST = 307;

    /***解绑机器人****/
    public static final int IM_UNBUNDLING_ROBOT_REQUEST = 308;

    /***执行行为配置文件****/
    public static final int IM_PLAY_BEHAVIOR_REQUEST = 309;
    public static final int IM_GET_BEHAVIOR_LIST_REQUEST = 310;
    /**获取机器人ip,Wifi名称*/
    public static final int IM_GET_ROBOT_IP_REQUEST = 311;

    public static final int IM_DETECT_UPGRADE_REQUEST = 312;   //检查升级包
    public static final int IM_FIRMWARE_DOWNLOAD_REQUEST = 313; //启动下载
    public static final int IM_FIRMWARE_UPGRADE_REQUEST = 314; // 启动升级
    public static final int IM_FIRMWARE_DOWNLOAD_PROGRESS_REQUEST = 315; // 启动升级

    /**切换编程模式*/
    public static final int IM_SWITCH_CODE_MAO_REQUEST = 340;


    public static final int IM_ADB_SWITCH_REQUEST = 316; // adb 开关指令


    public static final int IM_AVATAR_CONTROL_REQUEST = 317;//avatar控制指令
    public static final int IM_UPLOAD_LOG_REQUEST = 318;//上传log到七牛云

    public static final int IM_GET_CAMREA_PRIVACY_REQUEST = 320;//获取摄像头隐私

    public static final int IM_SET_CAMREA_PRIVACY_REQUEST = 321;//设置摄像头隐私


    public static final int IM_GET_MULTI_CONVERSATION_STATE_REQUEST = 360;//获取多轮交互的开关状态
    public static final int IM_SET_MULTI_CONVERSATION_STATE_REQUEST = 361;//设置多轮交互的开关状态

    /**************************************后台推送********************************************/
    static public final int IM_OFFLINE_FROM_SERVER_RESPONSE = 2001;//后台推送IM状态变更
    //升级skill,后台推送到终端，检测是否有新版本更新。完整流程：语音询问“有新版本可更新吗？”—>TVS后台—> UBT 升级skill —> Push该命令到机器人
    static public final int IM_CHECK_UPDATE_FROM_SERVICE_RESPONSE = 2002;
    static public final int IM_ACCOUNT_APPLY_RESPONSE = 2003;
    static public final int IM_ACCOUNT_HANDLE_APPLY_RESPONSE = 2004;
    static public final int IM_ACCOUNT_PERMISSION_CHANGE_RESPONSE = 2005;
    static public final int IM_ACCOUNT_MASTER_UNBINDED_RESPONSE = 2006;
    static public final int IM_ACCOUNT_BEEN_UNBIND_RESPONSE = 2007;
    static public final int IM_ACCOUNT_SLAVER_UNBIND_RESPONSE = 2008;
    static public final int IM_ACCOUNT_INVITATION_ACCEPTED_RESPONSE = 2009;
    static public final int IM_ACCOUNT_PERMISSION_REQUEST_RESPONSE = 2047;


    /**************************************主服务推送，预留100个********************************************/
    static public final int IM_APP_INSTALL_STATE_RESPONSE = 1901;
    static public final int IM_ACTIONFILE_DOWNLOAD_STATE_RESPONSE = 1902;
    static public final int IM_APP_LEAVE_AGORA_ROOM = 1903;

    static public final int IM_AVATAR_STOPPED_RESPONSE = 1903;

    static public final int IM_AVATAR_FAIL_RESPONSE = 1904;

    static public final int IM_AVATAR_USER_CHANGE_RESPONSE = 1905;


    static public final int IM_AVATAR_LOWPOWER_TIPS_RESPONSE = 1906;



    /**************************************小车指令********************************************/

    public static final int IM_JIMU_CAR_QUERY_POWER_REQUEST = 400; //小车当前电量信息
    public static final int IM_JIMU_CAR_CHANGE_DRIVE_MODE_REQUEST = 401; //进入/退出开车模式
    public static final int IM_JIMU_CAR_GET_IR_DISTANCE_REQUEST = 402; //获取小车红外距离
    public static final int IM_JIMU_CAR_CONTROL_REQUEST = 403; //操作（向前、向后、向左、向右走）指令
    public static final int IM_JIMU_CAR_ROBOT_CHAT_REQUEST = 404; //控制机器人（播放语句指令等）
    public static final int IM_JIMU_CAR_GET_BLE_CAR_LIST_REQUEST = 405; //获取小车蓝牙列表
    public static final int IM_JIMU_CAR_CHECK_REQUEST = 406; //检查小车零件
    public static final int IM_JIMU_CAR_CONNECT_CAR_REQUEST = 407; //连接指定的小车指令
    public static final int IM_JIMU_CAR_QUERY_CONNECT_STATE_REQUEST = 408;  //查询机器人和小车蓝牙是否连接
    public static final int IM_JIMU_CAR_CHANGE_LEVEL_REQUEST = 409;//警车模式和普通模式
    public static final int IM_JIMU_CAR_CHECK_PREPARED = 411;   //机器人与小车连接好了


    public static final int IM_JIMU_CAR_QUERY_POWER_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_QUERY_POWER_REQUEST; //小车当前电量信息
    public static final int IM_JIMU_CAR_CHANGE_DRIVE_MODE_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_CHANGE_DRIVE_MODE_REQUEST; //进入/退出开车模式
    public static final int IM_JIMU_CAR_GET_IR_DISTANCE_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_GET_IR_DISTANCE_REQUEST; //获取小车红外距离
    public static final int IM_JIMU_CAR_CONTROL_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_CONTROL_REQUEST; //操作（向前、向后、向左、向右走）指令
    public static final int IM_JIMU_CAR_ROBOT_CHAT_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_ROBOT_CHAT_REQUEST; //控制机器人（播放语句指令等）
    public static final int IM_JIMU_CAR_GET_BLE_CAR_LIST_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_GET_BLE_CAR_LIST_REQUEST; //获取小车蓝牙列表
    public static final int IM_JIMU_CAR_CHECK_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_CHECK_REQUEST;   //检查小车零件回复
    public static final int IM_JIMU_CAR_CONNECT_CAR_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_CONNECT_CAR_REQUEST; //连接指定的小车指令
    public static final int IM_JIMU_CAR_QUERY_CONNECT_STATE_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_QUERY_CONNECT_STATE_REQUEST; //机器人和小车蓝牙连接状态
    public static final int IM_JIMU_CAR_CHECK_PREPARED_RESPONSE = RESPONSE_BASE + IM_JIMU_CAR_CHECK_PREPARED;   //机器人与小车连接好了

    static public final int IM_RESPONSE_DRIVE_READY = 1907; //通知手机端机器人已经坐在小车上
    static public final int IM_ROBOT_LOW_POWER = 1908; //机器人低电量指令
    static public final int IM_JIMU_CAR_LOW_POWER = 1909; //jimu端低电量指令
    public static final int IM_ROBOT_NO_NETWORK = 1910; //机器人断网指令
    public static final int IM_ROBOT_NO_BLUETOOTH = 1911; //机器人蓝牙断开指令（与jimu小车蓝牙断开连接）
    public static final int IM_ROBOT_GET_CAR_LIST = 1912; //获取jimu小车列表指令
    public static final int IM_RESPONSE_CHECK_COMPONENT = 1913; //回复检查零件哪块出问题
    /**************************************小车指令********************************************/




    public static final int IM_GET_ALBUM_LIST_REQUEST=112;
    public static final int IM_GET_MOBILE_ALBUM_PUSH_REQUEST=110;
    public static final int IM_GET_MOBILE_ALBUM_DOWNLOAD_REQUEST=111;

    /***************************************返回************************************************/
    static public final int IM_CONNECT_ROBOT_RESPONSE = RESPONSE_BASE + IM_CONNECT_ROBOT_REQUEST;

    static public final int IM_DISCONNECT_ROBOT_RESPONSE = RESPONSE_BASE + IM_DISCONNECT_ROBOT_REQUEST;


    static public final int IM_DELETE_ACTIONFILE_RESPONSE = RESPONSE_BASE + IM_DELETE_ACTIONFILE_REQUEST;
    static public final int IM_PLAY_ACTION_RESPONSE = RESPONSE_BASE + IM_PLAY_ACTION_REQUEST;
    static public final int IM_GET_MOTORANGLE_RESPONSE = RESPONSE_BASE + IM_GET_MOTORANGLE_REQUEST;
    static public final int IM_STOP_PLAY_RESPONSE = RESPONSE_BASE + IM_STOP_PLAY_REQUEST;
    static public final int IM_START_APP_RESPONSE = RESPONSE_BASE + IM_START_APP_REQUEST;
    static public final int IM_STOP_APP_RESPONSE = RESPONSE_BASE + IM_STOP_APP_REQUEST;

    static public final int IM_GET_ALLAPPS_RESPONSE = RESPONSE_BASE + IM_GET_ALLAPPS_REQUEST;
    static public final int IM_SET_DEBUGMODE_RESPONSE = RESPONSE_BASE + IM_SET_DEBUGMODE_REQUEST;
    static public final int IM_SET_MOTOANGLE_RESPONSE = RESPONSE_BASE + IM_SET_MOTOANGLE_REQUEST;
    static public final int IM_INSTALL_PACKAGES_RESPONSE = RESPONSE_BASE + IM_INSTALL_PACKAGES_REQUEST;
    static public final int IM_UNINSTALL_PACKAGES_RESPONSE = RESPONSE_BASE + IM_UNINSTALL_PACKAGES_REQUEST;
    static public final int IM_UPDATE_PACKAGES_RESPONSE = RESPONSE_BASE + IM_UPDATE_PACKAGES_REQUEST;

    public static final int IM_GET_FORMER_CLOCK_RESPONSE = RESPONSE_BASE +IM_GET_FORMER_CLOCK_REQUEST;
    public static final int IM_DELETE_FORMER_CLOCK_RESPONSE = RESPONSE_BASE +IM_DELETE_FORMER_CLOCK_REQUEST;
    public static final int IM_QUERY_ROBOT_POWER_RESPONSE = RESPONSE_BASE + IM_QUERY_ROBOT_POWER_REQUEST;
    public static final int IM_QUERY_HARD_SOFT_WARE_VERSION_RESPONSE =RESPONSE_BASE +IM_QUERY_HARD_SOFT_WARE_VERSION_REQUEST;
    public static final int IM_MANAGER_ROBOT_BLUETOOTH_RESPONSE = RESPONSE_BASE+IM_MANAGER_ROBOT_BLUETOOTH_REQUEST;
    public static final int IM_TTS_BREATH_ACTION_ON_OFF_RESPONSE =RESPONSE_BASE +IM_TTS_BREATH_ACTION_ON_OFF_REQUEST;
    public static final int IM_GET_TTS_BREATH_ACTION_ON_OFF_RESPONSE =RESPONSE_BASE +IM_GET_TTS_BREATH_ACTION_ON_OFF_REQUEST;

    public static final int IM_SYN_SPEECH_RESPONSE =RESPONSE_BASE +IM_SYN_SPEECH_REQUEST;
    public static final int IM_CLOSE_ROBOT_ERROR_LOG_RESPONSE =RESPONSE_BASE +IM_CLOSE_ROBOT_ERROR_LOG_REQUEST;
    public static final int IM_CLOSE_ROBOT_ERROR_LOG_REQPONSE=RESPONSE_BASE +IM_CLOSE_ROBOT_ERROR_LOG_REQUEST;
    public static final int IM_UNINSTALL_BATCH_APPS_RESPONSE =RESPONSE_BASE + IM_UNINSTALL_BATCH_APPS_REQUEST;
    public static final int IM_RETRY_PLAY_ANSWER_RESPONSE =RESPONSE_BASE + IM_RETRY_PLAY_ANSWER_REQUEST;
    public static final int IM_QUERY_ROBOT_STORAGE_APP_LIST_RESPONSE =RESPONSE_BASE + IM_QUERY_ROBOT_STORAGE_APP_LIST_REQUEST;

    static public final int IM_GET_APPCONFIG_RESPONSE = RESPONSE_BASE + IM_GET_APPCONFIG_REQUEST;
    static public final int IM_SAVE_APPCONFIG_RESPONSE = RESPONSE_BASE + IM_SAVE_APPCONFIG_REQUEST;
    static public final int IM_GET_TOP_APP_RESPONSE = RESPONSE_BASE + IM_GET_TOP_APP_REQUEST;

    static public final int IM_GET_APP_BUTTONEVENT_RESPONSE = RESPONSE_BASE + IM_GET_APP_BUTTONEVENT_REQUEST;
    static public final int IM_CLICK_APP_BUTTON_RESPONSE = RESPONSE_BASE + IM_CLICK_APP_BUTTON_REQUEST;
    static public final int IM_DOWNLOAD_ACTIONFILE_RESPONSE = RESPONSE_BASE + IM_DOWNLOAD_ACTIONFILE_REQUEST;
    static public final int IM_DESKCLOCK_RESPONSE = RESPONSE_BASE + IM_DESKCLOCK_MANAGE_REQUEST;


    static public final int IM_DESKCLOCKLIST_RESPONSE = RESPONSE_BASE + IM_DESKCLOCK_ACTIVIE_LIST_REQUEST;

    static public final int IM_TRANSFER_PHOTO_RESPONSE = RESPONSE_BASE + IM_TRANSFER_PHOTO_REQUEST;
    static public final int IM_MASTER_CLEAR_RESPONSE = RESPONSE_BASE + IM_MASTER_CLEAR_REQUEST;
    static public final int IM_SET_RTC_TIME_RESPONSE = RESPONSE_BASE + IM_SET_RTC_TIME_REQUEST;
    static public final int IM_CHARGE_AND_PLAY_RESPONSE = RESPONSE_BASE + IM_SET_CHARGE_AND_PLAY_REQUEST;
    static public final int IM_FIND_MOBILEPHONE_RESPONSE = RESPONSE_BASE + IM_FIND_MOBILEPHONE_REQUEST;
    static public final int IM_GET_ROBOT_INIT_STATUS_RESPONSE =RESPONSE_BASE +IM_GET_ROBOT_INIT_STATUS_REQUEST ;
    static public final int IM_GET_ALL_THUMBNAIL_RESPONSE = RESPONSE_BASE + IM_GET_ALL_THUMBNAIL_REQUEST;
    static public final int IM_GET_NEW_ACTION_LIST_RESPONSE = RESPONSE_BASE + IM_GET_NEW_ACTION_LIST_REQUEST;
    static public final int IM_SET_MASTER_NAME_RESPONSE = RESPONSE_BASE + IM_SET_MASTER_NAME_REQUEST;
    static public final int IM_GET_CHARGE_AND_PLAY_RESPONSE = RESPONSE_BASE + IM_GET_CHARGE_AND_PLAY_REQUEST;

    public static final int IM_GET_TRANS_FILE_RESPONSE= RESPONSE_BASE + IM_GET_TRANS_FILE_REQUEST ;
    public static final int IM_GET_EXPORT_FILE__RESPONSE= RESPONSE_BASE + IM_GET_EXPORT_FILE__REQUEST ;
    public static final int IM_GET_DETAIL_FILE_INFO_RESPONSE= RESPONSE_BASE + IM_GET_DETAIL_FILE_INFO_REQUEST ;
    public static final int IM_GET_EMULATE_LED_RESPONSE= RESPONSE_BASE + IM_GET_EMULATE_LED_REQUEST ;
    public static final int IM_STOP_EMULATE_LED_RESPONSE= RESPONSE_BASE + IM_STOP_EMULATE_LED_REQUEST ;
    public static final int IM_GET_SOFTWARE_VERSION_RESPONSE= RESPONSE_BASE + IM_GET_SOFTWARE_VERSION_REQUEST ;
    public static final int IM_SET_ALL_MOTORS_ANGLE_RESPONSE= RESPONSE_BASE + IM_SET_ALL_MOTORS_ANGLE_REQUEST ;
    public static final int IM_ENTER_FASTBOOT_MODE_RESPONSE= RESPONSE_BASE + IM_ENTER_FASTBOOT_MODE_REQUEST ;
    public static final int IM_ENTER_MOTOR_INFO_RESPONSE= RESPONSE_BASE + IM_ENTER_MOTOR_INFO_REQUEST ;

    static public final int IM_GET_EMULATING_LED_RESPONSE = RESPONSE_BASE+ IM_GET_EMULATING_LED_REQUEST ;

    static public final int IM_CONFIRM_ONLINE_RESPONSE = RESPONSE_BASE+ IM_CONFIRM_ONLINE_REQUEST ;

    public static final int IM_SEND_TVS_ACCESSTOKEN_RESPONSE = RESPONSE_BASE + IM_SEND_TVS_ACCESSTOKEN_REQUEST;
    public static final int IM_GET_TVS_PRODUCTID_RESPONSE = RESPONSE_BASE + IM_GET_TVS_PRODUCTID_REQUEST;

    static public final int IM_GET_SERIAL_NUMBER_RESPONSE = RESPONSE_BASE+ IM_GET_SERIAL_NUMBER_REQUEST ;
    static public final int IM_SET_SERIAL_NUMBER_RESPONSE = RESPONSE_BASE+ IM_SET_SERIAL_NUMBER_REQUEST ;
    static public final int IM_GET_AGORA_ROOM_INFO_RESPONSE = RESPONSE_BASE+ IM_GET_AGORA_ROOM_INFO_REQUEST ;
    public static final int IM_GET_ROBOT_CONFIG_RESPONSE = RESPONSE_BASE + IM_GET_ROBOT_CONFIG_REQUEST;
    public static final int IM_SEND_WIFI_TO_ROBOT_RESPONSE = RESPONSE_BASE + IM_SEND_WIFI_TO_ROBOT_REQUEST;
    public static final int IM_GET_WIFI_LIST_TO_ROBOT_RESPONSE =RESPONSE_BASE +  IM_GET_WIFI_LIST_TO_ROBOT_REQUEST;
    public static final int IM_UNBUNDLINGROBOT_RESPONSE = RESPONSE_BASE + IM_UNBUNDLING_ROBOT_REQUEST;

    public static final int IM_START_FACE_DETECT_RESPONSE = RESPONSE_BASE + IM_START_FACE_DETECT_REQUEST;//启动人脸识别
    public static final int IM_FACE_UPDATE_RESPONSE = RESPONSE_BASE + IM_FACE_UPDATE_REQUEST; //修改人脸信息
    public static final int IM_FACE_DELETE_RESPONSE = RESPONSE_BASE + IM_FACE_DELETE_REQUEST; //删除人脸信息
    public static final int IM_FACE_LIST_RESPONSE = RESPONSE_BASE + IM_FACE_LIST_REQUEST; //人脸列表信息
    public static final int IM_FACE_EXIT_RESPONSE = RESPONSE_BASE + IM_FACE_EXIT_REQUEST; //人脸识别退出
    public static final int IM_FACE_CHECK_STATE_RESPONSE = RESPONSE_BASE + IM_FACE_CHECK_STATE_REQUEST; //人脸状态检测
    public static final int IM_GET_BEHAVIOR_LIST_RESPONSE = RESPONSE_BASE + IM_GET_BEHAVIOR_LIST_REQUEST;
    public static final int IM_GET_ROBOT_IP_RESPONSE = RESPONSE_BASE + IM_GET_ROBOT_IP_REQUEST;

    public static final int IM_CHECK_SIM_RESPONSE = RESPONSE_BASE+IM_CHECK_SIM_REQUEST; //确定是否有SIM
    public static final int IM_CONTACT_IMOPORT_RESPONSE = RESPONSE_BASE+IM_CONTACT_IMOPORT_REQUEST; //联系人导入
    public static final int IM_CONTACT_QUERY_RESPONSE = RESPONSE_BASE+IM_CONTACT_QUERY_REQUEST; //联系人查询
    public static final int IM_CONTACT_ADD_RESPONSE = RESPONSE_BASE+IM_CONTACT_ADD_REQUEST; //联系人添加
    public static final int IM_CONTACT_MODIFY_RESPONSE = RESPONSE_BASE+IM_CONTACT_MODIFY_REQUEST; //联系人修改
    public static final int IM_CONTACT_DELETE_RESPONSE = RESPONSE_BASE+IM_CONTACT_DELETE_REQUEST; //联系人删除
    public static final int IM_CALLRECORD_RESPONSE = RESPONSE_BASE+IM_CALLRECORD_REQUEST; //通话记录
    public static final int IM_MOBILE_NET_STATE_RESPONSE  = RESPONSE_BASE+IM_MOBILE_NET_STATE_REQUEST; //蜂窝网络状态
    public static final int IM_MOBILE_NET_MODIFY_RESPONSE  = RESPONSE_BASE+IM_MOBILE_NET_MODIFY_REQUEST; //蜂窝网络开关
    public static final int IM_MOBILE_ROAM_MODIFY_RESPONSE = RESPONSE_BASE+IM_MOBILE_ROAM_MODIFY_REQUEST; //蜂窝网络漫游开关

    public static final int IM_DETECT_UPGRADE_RESPONSE = RESPONSE_BASE + IM_DETECT_UPGRADE_REQUEST;   //检查升级包
    public static final int IM_FIRMWARE_DOWNLOAD_RESPONSE = RESPONSE_BASE + IM_FIRMWARE_DOWNLOAD_REQUEST; //启动下载
    public static final int IM_FIRMWARE_UPGRADE_RESPONSE  = RESPONSE_BASE + IM_FIRMWARE_UPGRADE_REQUEST; // 启动升级
    public static final int IM_FIRMWARE_DOWNLOAD_PROGRESS_RESPONSE  = RESPONSE_BASE + IM_FIRMWARE_DOWNLOAD_PROGRESS_REQUEST; // 启动升级

    /** 编程猫切换 */
    public static final int IM_SWITCH_CODE_MAO_RESPONSE= RESPONSE_BASE + IM_SWITCH_CODE_MAO_REQUEST;


    public static final int IM_AVATAR_CONTROL_RESPONSE = RESPONSE_BASE + IM_AVATAR_CONTROL_REQUEST;//avatar控制指令

}

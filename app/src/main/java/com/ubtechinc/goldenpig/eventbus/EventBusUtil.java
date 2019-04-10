package com.ubtechinc.goldenpig.eventbus;


import com.ubtechinc.goldenpig.eventbus.modle.Event;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtil {

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void sendEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    public static void sendStickyEvent(Event event) {
        EventBus.getDefault().postSticky(event);
    }


    public static final int CONTACT_CHECK_SUCCESS = 10002;
    public static final int SET_REPEAT_SUCCESS = 10003;
    public static final int SET_ALARM_SUCCESS = 10004;
    public static final int DELETE_RECORD_SUCCESS = 10005;
    public static final int SET_QUESTTION_SUCCESS = 10006;
    public static final int SET_ANSWER_SUCCESS = 10007;
    public static final int ADD_INTERLO_SUCCESS = 10008;
    public static final int ADD_REMIND_SUCCESS = 10009;
    public static final int ADD_REMIND_REPEAT_SUCCESS = 10010;
    public static final int CONTACT_PIC_SUCCESS = 10011;
    public static final int EDIT_RECORD_CALLBACK = 10012;
    public static final int INVISE_RECORD_POINT = 10013;
    public static final int TVS_LOGIN_SUCCESS = 10014;
    public static final int PUSH_NOTIFICATION_RECEIVED = 10015;
    public static final int PUSH_MESSAGE_RECEIVED = 10016;
    public static final int PUSH_NOTIFICATION_CLICKED_RECEIVED = 10017;
    public static final int USER_PIG_UPDATE = 10018;
    public static final int PAIR_PIG_UPDATE = 10019;
    public static final int TVS_LOGOUT_SUCCESS = 10020;
    public static final int DO_UPDATE_PAIR_PIG = 10021;
    public static final int UPDATE_HOME_FUNCTION_CARD = 10022;
    public static final int NEW_MESSAGE_NOTIFICATION = 20001;
    public static final int NEW_CALL_RECORD = 20002;
    public static final int NETWORK_STATE_CHANGED = 20003;
    public static final int DO_GET_NATIVE_INFO = 20004;
    public static final int RECEIVE_NATIVE_INFO = 20005;
    public static final int RECEIVE_PIG_VERSION = 20006;
    public static final int RECEIVE_CONTINUOUS_VOICE_STATE = 20007;
    public static final int RECEIVE_CONTINUOUS_VOICE_RESPONSE = 20008;
    public static final int RECEIVE_PIG_DEVICE_INFO = 20009;
    public static final int RECEIVE_DELETE_CONTACTS = 20010;
    public static final int RECEIVE_PIG_WIFI_LIST = 20011;
    public static final int RECEIVE_PIG_WIFI_CONNECT = 20012;
    public static final int RECEIVE_CLEAR_PIG_INFO = 20013;
    public static final int RECEIVE_ROBOT_ONLINE_STATE = 20014;
    public static final int RECEIVE_ROBOT_VERSION_STATE = 20015;
    public static final int APP_UPDATE_CHECK = 20016;

    public static final int SERVER_RESPONSE_UNAUTHORIZED = 401;
    public static final int RECEIVE_SHUTDOWN_STATE = 20017;
    public static final int RECEIVE_SHUTDOWN_SWITCH_STATE = 20018;
    public static final int RECEIVE_NO_DELAY_WAKEUP_STATE = 20019;
    public static final int RECEIVE_NO_DELAY_WAKEUP_SWITCH_STATE = 20020;

    public static final int DOWNLOAD_APK_STAR = 20021;
    public static final int DOWNLOAD_APK_PROGRESS = 20022;
    public static final int DOWNLOAD_APK_SUCCESS = 20023;
    public static final int DOWNLOAD_APK_FAILED = 20024;
    public static final int DOWNLOAD_APK_CANCLE = 20025;
    public static final int CHECK_NO_UPDATE = 20026;
    public static final int CHECK_UPDATE_ERROR = 20027;

}

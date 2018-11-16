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

    public static final int SERVER_RESPONSE_UNAUTHORIZED = 401;


}

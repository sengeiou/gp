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

}

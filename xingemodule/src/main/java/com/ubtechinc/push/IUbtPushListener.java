package com.ubtechinc.push;

import android.content.Context;

/**
 * @author：ubt
 * @date：2018/10/5 15:37
 * @modifier：ubt
 * @modify_date：2018/10/5 15:37
 * [A brief description]
 */

public interface IUbtPushListener {

    void onRegisterResult(Context context, int i, UbtPushModel ubtPushModel);

    void onUnregisterResult(Context context, int i);

    void onSetTagResult(Context context, int i, String s);

    void onDeleteTagResult(Context context, int i, String s);

    /**
     * 应用内消息推送，默认不会在通知栏显示
     */
    void onTextMessage(Context context, UbtPushModel ubtPushModel);

    /**
     * 通知栏消息，会自动显示到通知栏里，样式可定制参考类说明网址
     *
     * @param context
     * @param ubtPushModel
     */
    void onNotifactionShowedResult(Context context, UbtPushModel ubtPushModel);

    /**
     * 点击通知
     *
     * @param context
     * @param ubtPushModel
     */
    void onNotifactionClickedResult(Context context, UbtPushModel ubtPushModel);

}

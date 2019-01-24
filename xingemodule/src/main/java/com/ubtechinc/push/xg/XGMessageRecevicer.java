package com.ubtechinc.push.xg;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.ubtechinc.push.UbtPushManager;
import com.ubtechinc.push.UbtPushModel;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/8 15:04
 * @描述: 消息接收Receiver
 * API 参考网址：http://docs.developer.qq.com/xg/android_access/api.html
 */

public class XGMessageRecevicer extends XGPushBaseReceiver {

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.d("TPush", "onRegisterResult:" + xgPushRegisterResult.toString());
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onRegisterResult(context, i, new UbtPushModel(xgPushRegisterResult,
                    null, null, null));
        }
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.d("TPush", "onUnregisterResult:");
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onUnregisterResult(context, i);
        }
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.d("TPush", "onSetTagResult:");
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onSetTagResult(context, i, s);
        }
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.d("TPush", "onDeleteTagResult:");
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onDeleteTagResult(context, i, s);
        }
    }

    //应用内消息推送，默认不会再通知栏显示
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.d("TPush", "msgContent:" + xgPushTextMessage.getContent() +
                "   title:" + xgPushTextMessage.getTitle());
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onTextMessage(context, new UbtPushModel(null,
                    xgPushTextMessage, null, null));
        }
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.d("TPush", "onNotifactionClickedResult content=" + xgPushClickedResult.getContent());
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onNotifactionClickedResult(context, new UbtPushModel(null,
                    null, null, xgPushClickedResult));
        }
    }

    //通知栏消息，会自动显示到通知栏里，样式可定制参考类说明网址
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        if (xgPushShowedResult == null) {
            return;
        }
        Log.d("TPush", "onNotifactionShowedResult result=" + xgPushShowedResult.getCustomContent() +
                " content==" + xgPushShowedResult.getContent());
        if (UbtPushManager.getInstance().getListener() != null) {
            UbtPushManager.getInstance().getListener().onNotifactionShowedResult(context, new UbtPushModel(null,
                    null, xgPushShowedResult, null));
        }
    }
}

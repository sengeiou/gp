package com.ubtechinc.goldenpig.push;

import android.content.Context;
import android.text.TextUtils;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.push.IUbtPushListener;
import com.ubtechinc.push.UbtPushModel;

/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :推送监听
 *@time          :2019/5/10 17:38
 *@change        :
 *@changetime    :2019/5/10 17:38
*/
public class PushListener implements IUbtPushListener {

    @Override
    public void onRegisterResult(Context context, int i, UbtPushModel ubtPushModel) {
        String pushToken = ubtPushModel.getToken();
        if (!TextUtils.isEmpty(pushToken)) {
            PushAppInfo pushAppInfo = AuthLive.getInstance().getPushAppInfo();
            pushAppInfo.setPushToken(pushToken);

            String userId = AuthLive.getInstance().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                int appId = pushAppInfo.getAppId();
                AuthLive.getInstance().getUserId();
                String appVersion = ContextUtils.getVerName(context);
                PushHttpProxy pushHttpProxy = new PushHttpProxy();
                pushHttpProxy.bindToken(appId, pushToken, userId, appVersion, BuildConfig.product, null);
            }
        } else {
            LogUtils.d(UBTPGApplication.TAG, "xg push token is null");
        }
    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, UbtPushModel ubtPushModel) {
        Event<UbtPushModel> event = new Event<>(EventBusUtil.PUSH_MESSAGE_RECEIVED);
        event.setData(ubtPushModel);
        EventBusUtil.sendEvent(event);
    }

    @Override
    public void onNotifactionShowedResult(Context context, UbtPushModel ubtPushModel) {
        Event<UbtPushModel> event = new Event<>(EventBusUtil.PUSH_NOTIFICATION_RECEIVED);
        event.setData(ubtPushModel);
        EventBusUtil.sendEvent(event);
    }

    @Override
    public void onNotifactionClickedResult(Context context, UbtPushModel ubtPushModel) {
        Event<UbtPushModel> event = new Event<>(EventBusUtil.PUSH_NOTIFICATION_CLICKED_RECEIVED);
        event.setData(ubtPushModel);
        EventBusUtil.sendEvent(event);
    }
}

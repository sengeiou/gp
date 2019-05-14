package com.ubtechinc.push;

import android.app.Activity;
import android.content.Context;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * @author：ubt
 * @date：2018/10/5 15:36
 * @modifier：ubt
 * @modify_date：2018/10/5 15:36
 * [A brief description]
 */

public class UbtPushManager {

    private static UbtPushManager instance;

    private Context mContext;

    private IUbtPushListener listener;

    public static UbtPushManager getInstance() {
        if (instance == null) {
            synchronized (UbtPushManager.class) {
                if (instance == null) {
                    instance = new UbtPushManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context, long accessID, String accessKey, PushBrandType type, IUbtPushListener listener) {
        this.mContext = context;
        this.listener = listener;
        switch (type) {
            case XG:
                //开启信鸽日志输出
                XGPushConfig.enableDebug(mContext, false);
                XGPushConfig.setAccessId(mContext, accessID);
                XGPushConfig.setAccessKey(mContext, accessKey);
                //信鸽注册代码
                XGPushManager.registerPush(mContext);
                break;
            case JPUSH:

                break;
            case GT:

                break;
            default:
        }
    }

    public String getPushClickResultForXG(Activity activity) {
        XGPushClickedResult message = XGPushManager.onActivityStarted(activity);
        String customContent = message.getCustomContent();
        return customContent;
    }

    public IUbtPushListener getListener() {
        return listener;
    }

    public void setListener(IUbtPushListener listener) {
        this.listener = listener;
    }
}

package com.ubt.robot.dmsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.ai.tvs.ConstantValues;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.core.account.AccountInfoManager;
import com.tencent.ai.tvs.core.account.TVSAuthDelegate;
import com.tencent.ai.tvs.core.account.UserInfoManager;
import com.tencent.ai.tvs.core.common.TVSCallback;
import com.tencent.ai.tvs.core.common.TVSCallback1;
import com.tencent.ai.tvs.core.common.TVSDevice;
import com.tencent.ai.tvs.core.common.TVSDeviceBindType;
import com.tencent.ai.tvs.env.ELoginEnv;
import com.tencent.ai.tvs.tskm.TVSAlarm;
import com.tencent.ai.tvs.tskm.TVSChildMode;
import com.tencent.ai.tvs.tskm.TVSReminder;
import com.ubt.robot.dmsdk.model.TVSWrapAccountInfo;
import com.ubt.robot.dmsdk.model.TVSWrapUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description: dmsdk封装组件，对外调用
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/19 14:21
 */
public class TVSWrapBridge {

    /**
     * 需在application中执行初始化
     * @param context
     */
    public static void init(Context context) {
        LoginProxy.getInstance().registerApp(context, TVSWrapConstant.APP_ID_WX, TVSWrapConstant.APP_ID_QQ_OPEN, new TVSAuthDelegate());
        LoginProxy.getInstance().setEnv(ELoginEnv.FORMAL);
    }

    /**
     * 微信是否支持，LoginProxy-isWXAppSupportAPI
     * @return
     */
    public static boolean isWXAppSupportAPI() {
        return LoginProxy.getInstance().isWXAppSupportAPI();
    }

    /**
     * 微信是否安装，LoginProxy-isWXAppInstalled
     * @return
     */
    public static boolean isWXAppInstalled() {
        return LoginProxy.getInstance().isWXAppInstalled();
    }

    /**
     * 是否已登录，LoginProxy-isTokenExist
     * @return
     */
    public static boolean isTokenExist() {
        return LoginProxy.getInstance().isTokenExist();
    }

    /**
     * tvs登录，LoginProxy-tvsLogin
     *
     * @param tvsPlatform
     * @param tvsWrapCallback
     */
    public static void tvsLogin(final TVSWrapPlatform tvsPlatform, Activity activityForQQ, final TVSWrapCallback tvsWrapCallback) {
        LoginProxy.getInstance().tvsLogin(tvsPlatform.toReal(), activityForQQ, new TVSCallback() {
            @Override
            public void onSuccess() {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(null);
                }

            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 设置tvs环境，LoginProxy-setEnv
     * @param tvsEnv
     */
    public static void setTvsEnv(TVSWrapLoginEnv tvsEnv) {
        LoginProxy.getInstance().setEnv(tvsEnv.toReal());
    }

    /**
     * 获取tvs环境，LoginProxy-getEnv
     * @return
     */
    public static TVSWrapLoginEnv getTvsEnv() {
        return TVSWrapLoginEnv.index(LoginProxy.getInstance().getEnv());
    }

    /**
     * 处理QQ登录
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public static boolean handleQQOpenIntent(int requestCode, int resultCode, Intent data) {
        return LoginProxy.getInstance().handleQQOpenIntent(requestCode, resultCode, data);
    }

    /**
     * tvs退出登录，LoginProxy-logout
     */
    public static void tvsLogout() {
        LoginProxy.getInstance().logout();
    }

    /**
     * tvs刷票，LoginProxy-tvsTokenVerify
     *
     * @param tvsWrapCallback
     */
    public static void tvsTokenVerify(final TVSWrapCallback tvsWrapCallback) {
        LoginProxy.getInstance().tvsTokenVerify(new TVSCallback() {
            @Override
            public void onSuccess() {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(null);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 获取闹钟 blobInfo
     *
     * @param eCloud_type     0,为查看; 1为添加;2为删除;3为更新
     * @param eRepeatType
     * @param lAlarmId
     * @param lStartTimeStamp
     * @return
     */
    public static String getAlarmBlobInfo(int eCloud_type, int eRepeatType, long
            lAlarmId, long lStartTimeStamp) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("eType", 0);
            JSONObject stCloudAlarmReq = new JSONObject();
            JSONObject stAccountBaseInfo = new JSONObject();
            stAccountBaseInfo.put("strAcctId", getTVSAccountInfo().getOpenID());
            stCloudAlarmReq.put("stAccountBaseInfo", stAccountBaseInfo);
            stCloudAlarmReq.put("eCloud_type", eCloud_type);
            stCloudAlarmReq.put("sPushInfo", "test alarm");
            JSONArray vCloudAlarmData = new JSONArray();
            JSONObject vCloudAlarmData0 = new JSONObject();
            JSONObject stAIDeviceBaseInfo = new JSONObject();
            //BaseApplication.currentRobotGuid
//            stAIDeviceBaseInfo.put("strGuid", "");
            stAIDeviceBaseInfo.put("strAppKey", TVSWrapConstant.APP_KEY);
            vCloudAlarmData0.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            vCloudAlarmData0.put("sNote", "起床");
            vCloudAlarmData0.put("eRepeatType", eRepeatType);
            //0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
            vCloudAlarmData0.put("lAlarmId", lAlarmId);
            vCloudAlarmData0.put("lStartTimeStamp", lStartTimeStamp / 1000);
            vCloudAlarmData0.put("vRingId", new JSONArray());
            vCloudAlarmData.put(vCloudAlarmData0);
            stCloudAlarmReq.put("vCloudAlarmData", vCloudAlarmData);
            obj.put("stCloudAlarmReq", stCloudAlarmReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 获取提醒 blobInfo
     *
     * @param sNote
     * @param eCloud_type     //0,为查看; 1为添加;2为删除;3为更新
     * @param eRepeatType
     * @param lReminderId
     * @param lStartTimeStamp
     * @return
     */
    public static String getRemindBlobInfo(String sNote, int eCloud_type, int eRepeatType, long
            lReminderId, long lStartTimeStamp) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("eType", 0);
            //obj.put("eType", 0);
            JSONObject stCloudAlarmReq = new JSONObject();
            JSONObject stAccountBaseInfo = new JSONObject();
            stAccountBaseInfo.put("strAcctId", getTVSAccountInfo().getOpenID());
            stCloudAlarmReq.put("stAccountBaseInfo", stAccountBaseInfo);
            stCloudAlarmReq.put("eCloud_type", eCloud_type);
            //stCloudAlarmReq.put("sPushInfo", "");
            JSONArray vCloudAlarmData = new JSONArray();
            JSONObject vCloudAlarmData0 = new JSONObject();
            JSONObject stAIDeviceBaseInfo = new JSONObject();
            //BaseApplication.currentRobotGuid
            stAIDeviceBaseInfo.put("strGuid", "");
            stAIDeviceBaseInfo.put("strAppKey", TVSWrapConstant.APP_KEY);
            vCloudAlarmData0.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            stCloudAlarmReq.put("stAIDeviceBaseInfo", stAIDeviceBaseInfo);
            vCloudAlarmData0.put("eRepeatType", eRepeatType);
            //0为异常类型，1为一次性，2为每天，3为每周，4为每月，5为工作日，6为节假日
            vCloudAlarmData0.put("lReminderId", lReminderId);
            vCloudAlarmData0.put("lStartTimeStamp", lStartTimeStamp / 1000);
            vCloudAlarmData0.put("sNote", sNote);
            vCloudAlarmData0.put("vRingId", new JSONArray());
            vCloudAlarmData.put(vCloudAlarmData0);
            stCloudAlarmReq.put("vCloudReminderData", vCloudAlarmData);
            obj.put("stCloudAlarmReq", stCloudAlarmReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 获取儿童模式 blobInfo
     *
     * @return
     */
    public static String getChildModeBlobInfo() {
        JSONObject obj = new JSONObject();
        JSONObject stChildReq = new JSONObject();
        try {
            stChildReq.put("deviceId", "");
            obj.put("childModeInfo", stChildReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 设置儿童模式 blobInfo
     *
     * @param flag 1 打开儿童模式，0 关闭儿童模式
     * @return
     */
    public static String setChildModeBlobInfo(int flag) {
        JSONObject obj = new JSONObject();
        JSONObject stChildReq = new JSONObject();
        try {
            stChildReq.put("eActionType", flag);
            //被控制的设备guid
            stChildReq.put("deviceId", "");
            //被控制的设备Appkey
            stChildReq.put("deviceAppkey", TVSWrapConstant.APP_KEY);
            //1,Android;2,ios，一般填1
            stChildReq.put("ePlatformType", 1);
            obj.put("childControlInfo", stChildReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 闹钟管理，TVSAlarm-manage
     *
     * @param blobInfo
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    public static void tvsAlarmManage(String blobInfo, String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        TVSAlarm tvsAlarm = new TVSAlarm(productID, dsn);
        tvsAlarm.manage(blobInfo, new TVSCallback1<String>() {
            @Override
            public void onSuccess(String s) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(s);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 闹钟查询，TVSAlarm-sync
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    @Deprecated
    public static void tvsAlarmQuery(String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        TVSAlarm tvsAlarm = new TVSAlarm(productID, dsn);
        tvsAlarm.sync("", new TVSCallback1<String>() {
            @Override
            public void onSuccess(String s) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(s);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }



    /**
     * 提醒管理，TVSReminder-manage
     *
     * @param blobInfo
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    public static void tvsReminderManage(String blobInfo, String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        TVSReminder tvsReminder = new TVSReminder(productID, dsn);
        tvsReminder.manage(blobInfo, new TVSCallback1<String>() {
            @Override
            public void onSuccess(String s) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(s);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 设置儿童模式，TVSChildMode-setConfig
     *
     * @param blobInfo
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    public static void tvsSetChildMode(String blobInfo, String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        TVSChildMode tvsChildMode = new TVSChildMode(productID, dsn);
        tvsChildMode.setConfig(blobInfo, new TVSCallback1<String>() {
            @Override
            public void onSuccess(String s) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(s);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 获取儿童模式，TVSChildMode-getConfig
     *
     * @param blobInfo
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    public static void tvsGetChildMode(String blobInfo, String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        TVSChildMode tvsChildMode = new TVSChildMode(productID, dsn);
        tvsChildMode.getConfig(blobInfo, new TVSCallback1<String>() {
            @Override
            public void onSuccess(String s) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(s);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 设备绑定,LoginProxy-bindPushDevice
     *
     * @param tvsWrapType     金猪：TVS ; 飞猪：SDK
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    public static void tvsBindDevice(TVSWrapType tvsWrapType, String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        LoginProxy.getInstance().bindPushDevice(getDevice(tvsWrapType, productID, dsn), new TVSCallback() {
            @Override
            public void onSuccess() {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(null);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    /**
     * 设备解绑,LoginProxy-unbindPushDevice
     *
     * @param tvsWrapType
     * @param productID
     * @param dsn
     * @param tvsWrapCallback
     */
    public static void tvsUnbindDevice(TVSWrapType tvsWrapType, String productID, String dsn, final TVSWrapCallback tvsWrapCallback) {
        LoginProxy.getInstance().unbindPushDevice(getDevice(tvsWrapType, productID, dsn), new TVSCallback() {
            @Override
            public void onSuccess() {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onSuccess(null);
                }
            }

            @Override
            public void onError(int i) {
                if (tvsWrapCallback != null) {
                    tvsWrapCallback.onError(i);
                }
            }
        });
    }

    public static TVSDevice getDevice(TVSWrapType tvsWrapType, String productID, String dsn) {
        TVSDevice device = new TVSDevice();
        device.productID = productID;
        device.dsn = dsn;
        switch (tvsWrapType) {
            case SDK:
                device.bindType = TVSDeviceBindType.SDK_SPEAKER;
                device.pushIDExtra = ConstantValues.PUSH_ID_EXTRA_SDK;
                break;
            case TVS:
                device.bindType = TVSDeviceBindType.TVS_SPEAKER;
                device.pushIDExtra = ConstantValues.PUSH_ID_EXTRA_TVS;
                break;
            default:
        }
        return device;
    }

    /**
     * 获取TVS-AccountInfoManager，无clientid
     *
     * @return
     */
    public static TVSWrapAccountInfo getTVSAccountInfo() {
        AccountInfoManager m = AccountInfoManager.getInstance();
        return new TVSWrapAccountInfo(m.getPlatformType().name(), m.getAppID(), m.getOpenID(), m.getTvsID(), m.getAccessToken(), m.getRefreshToken(),
                m.getUserId());
    }

    /**
     * 获取TVS-AccountInfoManager，带clientid
     *
     * @param productID
     * @param dsn
     * @return
     */
    public static TVSWrapAccountInfo getTVSAccountInfo(String productID, String dsn) {
        AccountInfoManager m = AccountInfoManager.getInstance();
        return new TVSWrapAccountInfo(m.getPlatformType().name(), m.getAppID(), m.getOpenID(), m.getTvsID(), m.getAccessToken(), m.getRefreshToken(),
                m.getUserId(), m.getClientId(productID, dsn));
    }

    public static TVSWrapUserInfo getTVSWrapUserInfo() {
        UserInfoManager m = UserInfoManager.getInstance();
        return new TVSWrapUserInfo(m.getNickname(), m.getSex(), m.getHeadImgUrl());
    }

    public interface TVSWrapCallback<T> {

        void onError(int errCode);

        void onSuccess(T result);
    }

}

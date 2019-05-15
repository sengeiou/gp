package com.ubtechinc.goldenpig.utils;

import android.text.TextUtils;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.nets.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PigUtils {

    public static void getPigList(String response, String userId, ArrayList<PigInfo> pigInfos) {
        if (!TextUtils.isEmpty(response)) {
            try {
                // [{"serialNumber":"040002KFK2222222221","userId":812882,"isAdmin":1,
                // "relationDate":"2018-09-13 15:10:43","bindingId":4512}]
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray != null) {
                    pigInfos.clear();
                    int length = jsonArray.length();
                    for (int index = 0; index < length; index++) {
                        JSONObject object = jsonArray.getJSONObject(index);
                        if (object.has("userId")) {

                            PigInfo pigInfo = new PigInfo();
                            pigInfo.setRobotUserId(userId);
                            pigInfo.setMasterUserId(userId);
                            pigInfo.setRobotName(object.getString("serialNumber"));
                            pigInfo.setBindingId(object.getInt("bindingId"));
                            if (object.getInt("isAdmin") == 1) {
                                pigInfo.isAdmin = true;
                                pigInfos.add(0, pigInfo);
                            } else {
                                pigInfos.add(pigInfo);
                            }
                        }
                    }

                    EventBusUtil.sendEvent(new Event<>(EventBusUtil.USER_PIG_UPDATE));

                    PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                    if (pigInfo != null) {
                        UbtTIMManager ubtTIMManager = UbtTIMManager.getInstance();
                        ubtTIMManager.setPigAccount(pigInfo.getRobotName());
                        if (pigInfo.isAdmin && !ubtTIMManager.isLoginedTIM()) {
                            ubtTIMManager.loginTIM(userId, pigInfo.getRobotName(), BuildConfig.IM_CHANNEL);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

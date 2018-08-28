package com.ubtechinc.bluetooth.command;

import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.WifiSendBean;
import com.ubtechinc.bluetooth.utils.Utils;
import com.ubtechinc.nets.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @desc : Json格式的命令生成器
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/3
 */

public class JsonCommandProduce implements ICommandProduce{
    @Override
    public String getClientId(String clientId) {
        return Utils.pactkClientIdCommandToRobot(clientId);
    }

    @Override
    public String getWifiState() {
        return Utils.pactkCommandToRobot(Constants.ROBOT_WIFI_IS_OK_TRANS);
    }

    @Override
    public String getBleSuc() {
        return Utils.pactkCommandToRobot(Constants.CONNECT_SUCCESS);
    }

    @Override
    public String getWifiList() {
        return Utils.pactkCommandToRobot(Constants.WIFI_LIST_TRANS);
    }

    @Override
    public String getWifiPasswdInfo(String type, String name, String passwd) {
        WifiSendBean wifiSendBean = new WifiSendBean();
        if(UbtBluetoothManager.getInstance().isChangeWifi()) {
            wifiSendBean.setCo(Constants.WIFI_INFO_TRANS);
        } else {
            wifiSendBean.setCo(Constants.WIFI_INFO_TRANS_FOR_BAINGD);
        }
        wifiSendBean.setC(type);
        wifiSendBean.setS(name);
        wifiSendBean.setP(passwd);
        return JsonUtil.object2Json(wifiSendBean);
    }

    @Override
    public String getNetworkNotAvailable() {
        JSONObject sendJsonObj = new JSONObject();
        try {
            sendJsonObj.put(Constants.DATA_COMMAND, Constants.ROBOT_NETWORK_NOT_AVAILABLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObj.toString();
    }
}

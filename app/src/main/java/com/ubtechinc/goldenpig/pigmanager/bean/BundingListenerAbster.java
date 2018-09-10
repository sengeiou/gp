package com.ubtechinc.goldenpig.pigmanager.bean;

import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.BungdingManager;

/**
 * @author：wululin
 * @date：2017/11/3 15:07
 * @modifier：ubt
 * @modify_date：2017/11/3 15:07
 * [A brief description]
 * version
 */

public  class BundingListenerAbster implements BungdingManager.BanddingListener {

    @Override
    public void connWifiSuccess() {

    }

    @Override
    public void onSuccess(RegisterRobotModule.Response response) {

    }

    @Override
    public void onFaild(int errorCode) {

    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void devicesConnectByOther() {

    }

    @Override
    public void bindByOthers(CheckBindRobotModule.User user) {

    }

    @Override
    public void robotNotWifi() {

    }

    @Override
    public void connectFailed() {

    }

    @Override
    public void hasWifi(String wifi) {

    }

    @Override
    public void onMaster() {

    }

    @Override
    public void onUnBind() {

    }

    @Override
    public void onPigConnected(String wifiName) {

    }
}

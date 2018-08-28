package com.ubtechinc.goldenpig.pigmanager.event;

import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;

/**
 * 当前机器人改变是的事件
 *
 * Created by ubt on 2018/1/4.
 */

public class CurrentRobotChangeEvent {

    public PigInfo currentRobotInfo;

    public CurrentRobotChangeEvent(PigInfo currentRobotInfo){
        this.currentRobotInfo = currentRobotInfo;
    }
}

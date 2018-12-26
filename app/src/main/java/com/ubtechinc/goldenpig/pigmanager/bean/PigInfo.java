package com.ubtechinc.goldenpig.pigmanager.bean;

import android.util.Log;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.goldenpig.login.observable.AuthLive;

import java.io.Serializable;
import java.util.List;

/*************************
 * @date 2016/7/4
 * @author
 * @Description 机器人详细信息
 * @modify
 * @modify_time
 **************************/
public class PigInfo implements Serializable {

    //机器人在线
    public static final int ROBOT_STATE_UNAVAILABLE = -1;
    public static final int ROBOT_STATE_OFFLINE = 0;
    public static final int ROBOT_STATE_ONLINE = 1;
    // 已连接
    public static final int ROBOT_STATE_CONNECTING = 2;
    public static final int ROBOT_STATE_CONNECTED = 3;
    public static final int ROBOT_STATE_DISCONNECT = 4;
    public static final int ROBOT_STATE_CONNECT_FAIL = 5;


    private String robotName; //机器人名称
    private String masterUserId; //masterUserId
    private String robotUserId; //机器人端账号ID
    private String masterUserName; //主账号昵称
    private String equipmentSeq; //主账号昵称

    private String guid;//闹钟，提醒等需要
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isAdmin;
    private int bindingId;
    private long confirmOffline;

    private List<PigPermission> robotPermissionList;

    private int connectionState = ROBOT_STATE_DISCONNECT; //连接状态

    private int onlineState = ROBOT_STATE_ONLINE;//在线状态

    public int getOnlineState() {
        return onlineState;
    }

    public boolean isOnline() {
        return onlineState == ROBOT_STATE_ONLINE;
    }

    public void setOnlineState(int onlineState) {
        this.onlineState = onlineState;
    }

    public int getConnectionState() {
        if (onlineState != ROBOT_STATE_ONLINE) {
            return ROBOT_STATE_DISCONNECT;
        }
        return connectionState;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    public void confirmOffline() {
        confirmOffline = System.currentTimeMillis();
        onlineState = ROBOT_STATE_OFFLINE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PigInfo that = (PigInfo) o;

        return StringUtils.isEquals(robotUserId, that.robotUserId);

    }


    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public String getMasterUserId() {
        return masterUserId;
    }

    public void setMasterUserId(String masterUserId) {
        this.masterUserId = masterUserId;
    }

    public String getRobotUserId() {
        Log.d("0422", " getRobotUserId : " + robotUserId);
        return robotUserId;
    }

    public void setRobotUserId(String robotUserId) {
        this.robotUserId = robotUserId;
    }

    @Override
    public String toString() {
        return "{robotUserId = " + robotUserId + " onlineState=" + onlineState + " connectState=" + connectionState + " isAdmin=" + isAdmin + "}";
    }

    public String getUserId() {
        return robotUserId;
    }

    public String getShareUser() {
        // TODO 返回共享用户
        return "todo";
    }

    public boolean isMaster() {
        if (masterUserId != null) {
            return masterUserId.equals(AuthLive.getInstance().getUserId());
        }
        return false;
    }

    public List<PigPermission> getRobotPermissionList() {
        return robotPermissionList;
    }

    public void setRobotPermissionList(List<PigPermission> robotPermissionList) {
        this.robotPermissionList = robotPermissionList;
    }

    public String getMasterUserName() {
        return masterUserName;
    }

    public void setMasterUserName(String masterUserName) {
        this.masterUserName = masterUserName;
    }

    public boolean hasPermission(String permissionCode) {
        boolean result = false;
        if (!CollectionUtils.isEmpty(getRobotPermissionList())) {
            for (PigPermission robotPermission : robotPermissionList) {
                if (robotPermission.getPermissionCode().equals(permissionCode)) {
                    result = robotPermission.getStatus() == 1;
                    break;
                }
            }
        }
        return result;
    }

    public String getEquipmentSeq() {
        return equipmentSeq;
    }

    public void setEquipmentSeq(String equipmentSeq) {
        this.equipmentSeq = equipmentSeq;
    }

    public long getConfirmOffline() {
        return confirmOffline;
    }

    public void setConfirmOffline(long confirmOffline) {
        this.confirmOffline = confirmOffline;
    }

    public int getBindingId() {
        return bindingId;
    }

    public void setBindingId(int bindingId) {
        this.bindingId = bindingId;
    }
}

package com.ubtechinc.goldenpig.pigmanager.bean;


import java.io.Serializable;

public class PigPermission implements Serializable{

    public static final int STATUS_OFF = 0;
    public static final int STATUS_ON = 1;

    private String permissionCode;

    private String permissionDesc;

    private int status;

    private String robotUserId;

    private String userId;

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRobotUserId() {
        return robotUserId;
    }

    public void setRobotUserId(String robotUserId) {
        this.robotUserId = robotUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.ubtechinc.goldenpig.pigmanager.event;

 import com.ubtechinc.goldenpig.pigmanager.bean.PigPermission;

/**
 * 权限发生改变时的消息
 * <p>
 * Created by ubt on 2018/1/4.
 */

public class PermissionChangeEvent {

    public PigPermission permission;
    public String robotId;

    public PermissionChangeEvent(String robotId, PigPermission permission){
        this.permission = permission;
        this.robotId = robotId;
    }
}

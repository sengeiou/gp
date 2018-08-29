package com.ubtechinc.goldenpig.pigmanager.observeable;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.PigPermission;
import com.ubtechinc.goldenpig.pigmanager.event.CurrentRobotChangeEvent;
import com.ubtechinc.goldenpig.pigmanager.event.PermissionChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可被监听的机器人列表类
 */

public class MyPigsLive {

  /*  public static final String TAG = "MyRobotsLive";

    public static final String LAST_CONNECTED_ROBOT = "last_connected_robot";

    private Map<String, PigInfo> robotMap; //机器人map,用于快速查询机器人

    private MutableLiveData<String> currentRobotId;//当前选中机器人 id 的 liveData

    private CurrentRobot currentRobot; //当前选中机器人 的 liveData

    private SelectToRobot selectToRobot; //将要选择的机器人 的 liveData

    private MutableLiveData<String> selectToRobotId; //将要选择的机器人 的 liveData

    private LiveResult<List<PigInfo>> robots; //机器人列表的liveData

    private Map<String, List<PigPermission>> robotPermissionMap = new HashMap<>(); //机器人权限的列表

    private static class MyRobotsLiveHolder {
        public static MyPigsLive instance = new MyPigsLive();
    }

    private MyPigsLive() {
        currentRobotId = new MutableLiveData<>();
        selectToRobotId = new MutableLiveData<>();
        robotMap = new HashMap<>();
        robots = new LiveResult<>();
        currentRobot = new CurrentRobot();
        selectToRobot = new SelectToRobot();
        currentRobot.addSource(currentRobotId, new Observer<String>() { //当前机器人id 变更，刷新当前机器人
            @Override
            public void onChanged(String id) {
                refreshCurrentRobot(id);
            }
        });
        currentRobot.addSource(robots, new Observer<LiveResult>() { //机器人列表变化刷新当前机器人
            @Override
            public void onChanged(LiveResult liveResult) {
                String id = currentRobotId.getValue();
                Log.d(TAG, "currentRobotId = " + id);
                if (!TextUtils.isEmpty(id)) {
                    refreshCurrentRobot(id);
                }
                autoConnect(id);
            }
        });
        selectToRobot.addSource(selectToRobotId, new Observer<String>() { //当前机器人id 变更，刷新当前机器人
            @Override
            public void onChanged(String id) {
                refreshSelectToRobot(id);
            }
        });
        selectToRobot.addSource(robots, new Observer<LiveResult>() { //机器人列表变化刷新当前机器人
            @Override
            public void onChanged(LiveResult liveResult) {
                String id = selectToRobotId.getValue();
                refreshSelectToRobot(id);
            }
        });
    }

    private void autoConnect(String id) {
        if (!TextUtils.isEmpty(id)) {
            PigInfo robotInfo = getRobotById(id);
            if (robotInfo == null) {
                PigInfo info = getLastConnectRobot();
                Log.d(TAG, "getLastConnectRobot = " + info);
                if (info != null) {
                    selectToRobotId.setValue(info.getRobotUserId());
                    currentRobotId.setValue(info.getRobotUserId());
                }
            }
        } else {
            PigInfo info = getLastConnectRobot();
            Log.d(TAG, "getLastConnectRobot = " + info);
            if (info != null) {
                selectToRobotId.setValue(info.getRobotUserId());
                currentRobotId.setValue(info.getRobotUserId());
            }
        }
    }

    private void refreshSelectToRobot(String id) {
        PigInfo info = null;
        if (!TextUtils.isEmpty(id)) {
            info = getRobotById(id);
        }
        Log.d(TAG, "refreshSelectToRobot " + info);
        PigInfo selectRobotInfo = selectToRobot.getValue();
        Log.d(TAG, "refreshSelectToRobot selectTO =" + selectRobotInfo);
        if (selectRobotInfo != null && info != null) {
            if (!selectRobotInfo.getRobotUserId().equals(info.getRobotUserId())) {
                selectToRobot.setValue(info);
            }
        } else if (selectRobotInfo != null || info != null) {//同为null时不需要更新
            selectToRobot.setValue(info);
        }

    }

    private void refreshCurrentRobot(String robotId) {
        if (!TextUtils.isEmpty(robotId)) {
            PigInfo robotInfo = robotMap.get(currentRobotId.getValue());
            if (robotInfo != null) {
                List<PigPermission> permissions = robotPermissionMap.get(robotId);
                if (!CollectionUtils.isEmpty(permissions)) {
                    robotInfo.setRobotPermissionList(permissions);
                }
                if (currentRobot.getValue() != null && !currentRobot.getValue().getRobotUserId().equals(robotId)) {
                    sendCurrentChangeEvent(robotInfo);
                }
                currentRobot.setValue(robotInfo);
            } else if (currentRobot.getValue() != null) {
                currentRobot.setValue(robotInfo);
                sendCurrentChangeEvent(null);
            }
        } else {
            if (currentRobot.getValue() != null) {
                currentRobot.setValue(null);
                sendCurrentChangeEvent(null);
            }
        }
    }

    private void sendCurrentChangeEvent(PigInfo newCurrentRobot) {
        EventBus.getDefault().post(new CurrentRobotChangeEvent(newCurrentRobot));
    }


    public void updateList(List<PigInfo> list) {
        list2Map(list);
        robots.success(list);
    }

    public void selectRobot(String id) { //切换当前机器人信息
        setSelectToRobotId(null);
        SPUtils.get().put(LAST_CONNECTED_ROBOT + AuthLive.getInstance().getUserId(), id);
        if (!id.equals(currentRobotId.getValue())) {
            currentRobotId.setValue(id);
        }
    }

    public void clearSelectToRobot() {
        setSelectToRobotId(null);
    }

    private void list2Map(List<PigInfo> list) {
        robotMap.clear();
        if (list != null) {
            for (PigInfo robot : list) {
                robotMap.put(robot.getRobotUserId(), robot);
            }
        }
    }

    public void updatePermission(String robotUserId, List<PigPermission> robotPermissions) {
        robotPermissionMap.put(robotUserId, robotPermissions);
        updateCurrentPermission(robotUserId, robotPermissions);
    }

    public void permissionChange(String robotUserId, PigPermission updatePermission) {
        if (!TextUtils.isEmpty(robotUserId) && updatePermission != null) {
            List<PigPermission> robotPermissions = robotPermissionMap.get(robotUserId);
            if (!CollectionUtils.isEmpty(robotPermissions)) {
                for (PigPermission robotPermission : robotPermissions) {
                    if (robotPermission.getPermissionCode().equals(updatePermission.getPermissionCode())) {
                        robotPermission.setStatus(updatePermission.getStatus());
                    }
                }
            }
            updateCurrentPermission(robotUserId, robotPermissions);
            if (robotUserId.equals(currentRobotId.getValue())) {//当前机器人权限发生改变，发送事件
                EventBus.getDefault().post(new PermissionChangeEvent(robotUserId, updatePermission));
            }
        }
    }

    private void updateCurrentPermission(String robotUserId, List<PigPermission> robotPermissions) {
        if (robotUserId.equals(currentRobotId.getValue())) {//如果是当前的机器人直接刷新当前机器人权限
            PigInfo info = currentRobot.getValue();
            info.setRobotPermissionList(robotPermissions);
            currentRobot.postValue(info);
        }
    }

    public void clear() {
        if (robotMap != null) {
            robotMap.clear();
        }
        robots.clear();
        currentRobotId.setValue(null);
    }

    *//**
     * 获取当前的机器人流水号
     * @return
     *//*
    public String getRobotUserId() {
        PigInfo robotInfo = currentRobot.getValue();
        if (robotInfo != null) {
            Log.i(TAG, " getRobotUserId dsn : " + robotInfo.getRobotUserId());
            return robotInfo.getRobotUserId();
        } else {
            Log.i(TAG, " getRobotUserId dsn : null");
            return null;
        }
    }

    *//**
     * 获取当前的机器人序列号
     * @return
     *//*
    public String getRobotEquipmentId() {
        PigInfo robotInfo = currentRobot.getValue();
        if (robotInfo != null) {
            Log.i(TAG, " getRobotUserId dsn : " + robotInfo.getRobotUserId());
            return robotInfo.getEquipmentSeq();
        } else {
            Log.i(TAG, " getRobotUserId dsn : null");
            return null;
        }
    }

    public PigInfo getRobotById(String equipmentId) {
        PigInfo result = null;
        if (robotMap != null) {
            result = robotMap.get(equipmentId);
        }
        return result;
    }

    public void remove(String equipmentId) {
        PigInfo robotInfo = getRobotById(equipmentId);
        if (robotInfo != null) {
            robotMap.remove(equipmentId);
            robotPermissionMap.remove(equipmentId);
            List<PigInfo> robotInfos = robots.getData();
            if (equipmentId.equals(currentRobotId.getValue())) {
                currentRobotId.postValue(null);
            }
            if (!CollectionUtils.isEmpty(robotInfos)) {
                robotInfos.remove(robotInfo);
                robots.postSuccess(robotInfos);
            }
        }
    }

    public MutableLiveData<String> getCurrentRobotId() {
        return currentRobotId;
    }

    public MediatorLiveData<PigInfo> getCurrentRobot() {
        return currentRobot;
    }

    public MediatorLiveData<PigInfo> getSelectToRobot() {
        return selectToRobot;
    }

    public LiveResult<List<PigInfo>> getRobots() {
        Log.e(TAG, " robots : " + robots);
        return robots;
    }


    public static MyPigsLive getInstance() {
        return MyRobotsLiveHolder.instance;
    }


    public PigInfo getLastConnectRobot() {
        PigInfo result;
        String lastId = selectToRobotId.getValue();
        if (TextUtils.isEmpty(lastId)) {
            lastId = SPUtils.get().getString(LAST_CONNECTED_ROBOT + AuthLive.getInstance().getUserId());
        }
        if (lastId == null) {
            result = getFistRobotInfo();
        } else {
            result = getRobotById(lastId);
            if (result == null) {
                result = getFistRobotInfo();
            }
        }
        return result;
    }

    public void postUpdateRobotState(String robotUserId, int onlineState) {
        PigInfo robotInfo = getRobotById(robotUserId);
        Log.d(TAG, "postUpdateRobotState " + robotInfo);
        Log.d(TAG, "postUpdateRobotState currentRobotId " + currentRobotId.getValue());
        if (robotInfo != null) {
            if (robotUserId.equals(currentRobotId.getValue()) && PigInfo.ROBOT_STATE_ONLINE == onlineState
                    && PigInfo.ROBOT_STATE_ONLINE != robotInfo.getOnlineState()) {//当前机器人从下线转为上线重新连接IM
                robotInfo.setOnlineState(onlineState);
                selectToRobotId.postValue(robotUserId);
            } else {
                robotInfo.setOnlineState(onlineState);
            }
            robots.postValue();
        }
    }

    private PigInfo getFistRobotInfo() {
        List<PigInfo> robotInfos = robots.getData();
        if (!CollectionUtils.isEmpty(robotInfos)) {
            return robotInfos.get(0);
        }
        return null;
    }

    public void confirmOffline(String robotUserId) {
        Log.d(TAG, "confirmOffline" + robotUserId);
        PigInfo info = getRobotById(robotUserId);
        Log.d(TAG, "confirmOffline" + info);
        if (info != null) {
            info.confirmOffline();
            robots.postUpdate();
        }
    }

    public int getRobotCount() {
        return CollectionUtils.isEmpty(robots.getData()) ? 0 : robots.getData().size();
    }

    public void setSelectToRobotId(String robotUserId) {
        LogUtils.d("setSelectToRobotId = " + robotUserId);
        selectToRobotId.postValue(robotUserId);
    }

    *//**
     * 切换到该机器人并将该机器人的改为在线状态，给配网后调用，解决切换时机器人为改变状态无法连接的问题
     *
     * @param robotUserId
     *//*
    public void setSelectToRobotIdOnline(String robotUserId) {
        PigInfo info = MyPigsLive.getInstance().getRobotById(robotUserId);
        if (info != null) {//切网成功直接将机器人的状态改为在线，以便自动连接该机器人
            info.setOnlineState(PigInfo.ROBOT_STATE_ONLINE);
        }
        selectToRobotId.postValue(robotUserId);
    }

    private static class CurrentRobot extends MediatorLiveData<PigInfo> {

    }

    private static class SelectToRobot extends MediatorLiveData<PigInfo> {

    }

    public void insertNewRobot(PigInfo robotInfo) {
        List<PigInfo> infos = getRobots().getData();
        if (infos == null) {
            infos = new ArrayList<>();
        }
        robotMap.put(robotInfo.getRobotUserId(),robotInfo);
        infos.remove(robotInfo);
        infos.add(0, robotInfo);
        robots.success(infos);
    }*/

}

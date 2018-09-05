package com.ubtechinc.goldenpig.pigmanager.model;

import android.util.Log;


import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.pigmanager.observeable.LiveResult;
import com.ubtechinc.goldenpig.pigmanager.observeable.RobotBindStateLive;
import com.ubtechinc.goldenpig.pigmanager.observeable.RobotUnbindLive;
import com.ubtechinc.goldenpig.pigmanager.register.CheckRobotRepository;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.ArrayList;
import java.util.List;


/**
 * @Date: 2017/10/26.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 获取机器人关联所有账号的业务逻辑
 */

public class RobotAllAccountViewModel {

    private String TAG = getClass().getSimpleName();

    private CheckRobotRepository mCheckRobotRepository;

    public RobotAllAccountViewModel() {
        mCheckRobotRepository = new CheckRobotRepository();
    }

    public RobotBindStateLive checkRobotBindState(String searialNumber,String token,String appId) {
        final RobotBindStateLive robotBindStateLive = new RobotBindStateLive();
        mCheckRobotRepository.getRobotBindUsers(searialNumber,token,appId, new CheckRobotRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                robotBindStateLive.networkError();
                doOnError(e);
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                if (response.isSuccess()) {
                    List<CheckBindRobotModule.User> bindUsers = response.getData().getResult();//机器人相关联的账号
                    if (bindUsers != null && bindUsers.size() > 0) {
                        boolean isMaster = checkRobotIsBanding(bindUsers);
                        if (isMaster) {
                            robotBindStateLive.bindByMyself();
                        } else {
                            robotBindStateLive.bindByOthers(bindUsers);
                        }
                    } else {
                        robotBindStateLive.haventBind();
                    }
                }
            }
        });
        return robotBindStateLive;
    }


    public LiveResult getBindUsers(String searialNumber){
        final LiveResult result = new LiveResult();
        mCheckRobotRepository.getRobotBindUsers(searialNumber, new CheckRobotRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                result.fail("network error");
                doOnError(e);
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                if (response.isSuccess()) {
                    List<CheckBindRobotModule.User> bindUsers = response.getData().getResult();//机器人相关联的账号
                    result.success(bindUsers);
                }else{
                    result.setData(null);
                    result.fail("network error");
                }
            }
        });
        return result;
    }

    public RobotUnbindLive getRobotUnBindWays(String searialNumber) {
        final RobotUnbindLive robotUnbindLive = new RobotUnbindLive();
        Log.i("getRobotUnBindWays", "getRobotUnBindWays: robotUnbindLive:" + robotUnbindLive);
        mCheckRobotRepository.getRobotBindUsers(searialNumber, new CheckRobotRepository.ICheckBindStateCallBack() {
            @Override
            public void onError(ThrowableWrapper e) {
                robotUnbindLive.networkError();
                doOnError(e);
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                if (response.isSuccess()) {
                    List<CheckBindRobotModule.User> bindUsers = response.getData().getResult();//机器人相关联的账号
                    if (bindUsers != null && bindUsers.size() > 0) {
                        boolean isMaster = checkRobotMaster(bindUsers);
                        if (!isMaster) {
                            robotUnbindLive.setSimpleCategory();
                        } else {
                            List<CheckBindRobotModule.User> robotOwners = getSlaverUsers(bindUsers);
                            if (robotOwners.size() > 0) {
                                robotUnbindLive.setMaterCategory(robotOwners);
                            } else {
                                robotUnbindLive.setSimpleCategory();
                            }
                        }
                    } else {
                        robotUnbindLive.noNeedUnbind();
                    }
                }
            }
        });
        return robotUnbindLive;
    }

    public void doOnError(ThrowableWrapper e) {
        UbtLogger.e(TAG, "",e);
    }

    private List<CheckBindRobotModule.User> getSlaverUsers(List<CheckBindRobotModule.User> bindUsers) {
        List<CheckBindRobotModule.User> robotOwners = new ArrayList<>();
        for (CheckBindRobotModule.User user : bindUsers) {
            if (user.getUpUser() == 1) {
                robotOwners.add(user);
            }
        }
        return robotOwners;
    }

    private boolean checkRobotMaster(List<CheckBindRobotModule.User> bindUsers) {
        boolean flag = false;
        for (CheckBindRobotModule.User user : bindUsers) {
            if (user.getUpUser() == 0 && String.valueOf(user.getUserId()).equalsIgnoreCase(AuthLive.getInstance().getUserId())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    private boolean checkRobotIsBanding(List<CheckBindRobotModule.User> bindUsers) {
        boolean flag = false;
        for (CheckBindRobotModule.User user : bindUsers) {
            if (String.valueOf(user.getUserId()).equalsIgnoreCase(AuthLive.getInstance().getUserId())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}

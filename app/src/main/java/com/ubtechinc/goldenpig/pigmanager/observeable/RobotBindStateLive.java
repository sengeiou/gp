package com.ubtechinc.goldenpig.pigmanager.observeable;

import android.arch.lifecycle.LiveData;


import com.ubtechinc.goldenpig.net.CheckBindRobotModule;

import java.util.List;

/**
 * @Date: 2017/10/26.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人绑定状态
 */

public class RobotBindStateLive extends LiveData<RobotBindStateLive> {

    public enum BindState{
        MySelf, Others, HaventBind, Networkerror
    }

    private List<CheckBindRobotModule.User> mRobotOwners;

    public List<CheckBindRobotModule.User> getRobotOwners() {
        return mRobotOwners;
    }

    private BindState state = BindState.HaventBind;

    public BindState getCurBindState(){
        return state;
    }

    public void bindByMyself(){
        state = BindState.MySelf;
        setValue(this);
    }

    public void bindByOthers(List<CheckBindRobotModule.User> robotOwners){
        mRobotOwners = robotOwners;
        state = BindState.Others;
        setValue(this);
    }


    public void haventBind(){
        state = BindState.HaventBind;
        setValue(this);
    }

    public void networkError(){
        state = BindState.Networkerror;
        setValue(this);
    }

}



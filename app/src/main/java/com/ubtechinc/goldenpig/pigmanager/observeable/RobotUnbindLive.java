package com.ubtechinc.goldenpig.pigmanager.observeable;

import android.arch.lifecycle.LiveData;


import com.ubtechinc.goldenpig.net.CheckBindRobotModule;

import java.util.List;

/**
 * @Date: 2017/11/8.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class RobotUnbindLive extends LiveData<RobotUnbindLive> {

    public enum UnbindWays{
        MASTER, SIMPLE, NO_NEED, NETWORKERROR
    }
    private List<CheckBindRobotModule.User> mRobotOwners;

    public List<CheckBindRobotModule.User> getRobotOwners() {
        return mRobotOwners;
    }

    private UnbindWays category = UnbindWays.MASTER;

    public UnbindWays getUnbindCategory(){
        return category;
    }

    public void setMaterCategory(List<CheckBindRobotModule.User> users){
        mRobotOwners = users;
        category = UnbindWays.MASTER;
        setValue(this);
    }


    public void setSimpleCategory(){
        category = UnbindWays.SIMPLE;
        setValue(this);
    }

    public void noNeedUnbind(){
        category = UnbindWays.NO_NEED;
        setValue(this);
    }

    public void networkError(){
        category = UnbindWays.NETWORKERROR;
        setValue(this);
    }

}

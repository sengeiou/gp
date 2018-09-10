package com.ubtechinc.goldenpig.login.observable;

import android.arch.lifecycle.LiveData;
import android.os.Looper;


import com.ubtechinc.goldenpig.comm.entity.UserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.goldenpig.login.observable.AuthLive.AuthState.NORMAL;


/**
 * Created by ubt on 2017/9/30.
 */

public class AuthLive extends LiveData<AuthLive> {

    public enum AuthState {
        LOGINING, LOGINED, LOGOUTTING, LOGOUTTED, FORCE_OFFLINE, FORBIDDEN, ERROR, CANCEL, NORMAL
    }

    private static class AuthLiveHolder {
        public static AuthLive instance = new AuthLive();
    }

    private AuthLive() {
        EventBus.getDefault().register(this);
    }

    public static AuthLive getInstance() {
        return AuthLiveHolder.instance;
    }


    private UserInfo currentUser;

    private AuthState state;
    private String loginToken;
    public void logining() {
        this.state = AuthState.LOGINING;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }

    public void logined(UserInfo userInfo) {
        currentUser = userInfo;
        this.state = AuthState.LOGINED;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }


    public void logout() {
        this.state = AuthState.LOGOUTTED;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }

    public void forceOffline() {
        this.state = AuthState.FORCE_OFFLINE;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }

    public void error() {
        this.state = AuthState.ERROR;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }

    public void cancel() {
        this.state = AuthState.CANCEL;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }

    public void forbidden() {
        this.state = AuthState.FORBIDDEN;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public AuthState getState() {
        return state;
    }

    public String getUserId() {
        if (currentUser != null) {
            return currentUser.getUserId();
        }
        return "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object event) {
       /* if (event.getState() == IMStateChange.STATE_FORCE_OFFLINE) {
            forceOffline();
        }*/
    }

    public void reset() {
        this.state = NORMAL;
        if(isMainThread()){
            setValue(this);
        }else {
            postValue(this);
        }
    }


    public boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }
}

package com.ubtechinc.goldenpig.login.observable;

import android.arch.lifecycle.LiveData;
import android.os.Looper;

import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.push.PushAppInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.ubtechinc.goldenpig.login.observable.AuthLive.AuthState.NORMAL;


/**
 * Created by ubt on 2017/9/30.
 */

public class AuthLive extends LiveData<AuthLive> {

    public enum AuthState {
        LOGINING, TVSLOGINED, TIMLOGINED, TIMLOGINERROR, LOGOUTTING, LOGOUTTED, FORCE_OFFLINE, FORBIDDEN, ERROR, CANCEL, NORMAL
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
    private ArrayList<PigInfo> currentPigList;
    private AuthState state;
    private String loginToken;

    private PushAppInfo pushAppInfo;

    public PushAppInfo getPushAppInfo() {
        if (pushAppInfo == null) {
            pushAppInfo = new PushAppInfo();
        }
        return pushAppInfo;
    }

    public void setPushAppInfo(PushAppInfo pushAppInfo) {
        this.pushAppInfo = pushAppInfo;
    }

    public void logining() {
        this.state = AuthState.LOGINING;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void logined(UserInfo userInfo) {
        Event<Integer> event = new Event<>(EventBusUtil.TVS_LOGIN_SUCCESS);
        EventBusUtil.sendEvent(event);
        currentUser = userInfo;
        this.state = AuthState.TVSLOGINED;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void timLogined() {
        this.state = AuthState.TIMLOGINED;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void timLoginError() {
        this.state = AuthState.TIMLOGINERROR;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }


    public PigInfo getCurrentPig() {
        if (currentPigList != null && currentPigList.size() > 0) {
            return currentPigList.get(0);
        }
        return null;
    }

    public ArrayList<PigInfo> getCurrentPigList() {
        if (currentPigList == null) {
            currentPigList = new ArrayList<>();
        }
        return currentPigList;
    }

    public void setCurrentPig(PigInfo currentPig) {
        if (this.currentPigList == null) {
            currentPigList = new ArrayList<>();
        }
        this.currentPigList.add(currentPig);

    }

    public void logout() {
        this.state = AuthState.LOGOUTTED;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void forceOffline() {
        this.state = AuthState.FORCE_OFFLINE;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void error() {
        this.state = AuthState.ERROR;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void cancel() {
        this.state = AuthState.CANCEL;
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }

    public void forbidden() {
        this.state = AuthState.FORBIDDEN;
        if (isMainThread()) {
            setValue(this);
        } else {
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
        if (isMainThread()) {
            setValue(this);
        } else {
            postValue(this);
        }
    }


    public boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }
}

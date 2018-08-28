package com.ubtechinc.goldenpig.pigmanager.observeable;

import android.arch.lifecycle.LiveData;
import android.os.Looper;

public class LiveResult<T> extends LiveData<LiveResult> {

    public enum LiveState {
        LOADING, SUCCESS, FAIL
    }

    private LiveState state = LiveState.LOADING;

    private T data;

    private String msg;

    private int errorCode;

    public LiveResult() {
        setValue(this);
    }

    public void loading(T data) {
        this.state = LiveState.LOADING;
        this.data = data;
        if (isMainThread()) {
            setValue(this);
        }else{
            postValue(this);
        }
    }

    public void success(T data) {
        this.state = LiveState.SUCCESS;
        this.data = data;
        if (isMainThread()) {
            setValue(this);
        }else{
            postValue(this);
        }
    }

    public void postSuccess(T data) {
        this.state = LiveState.SUCCESS;
        this.data = data;
        postValue(this);
    }

    public void fail(String msg) {
        this.state = LiveState.FAIL;
        this.msg = msg;
        if (isMainThread()) {
            setValue(this);
        }else{
            postValue(this);
        }
    }

    public void fail(int code, String msg) {
        this.state = LiveState.FAIL;
        this.msg = msg;
        this.errorCode = code;
        if (isMainThread()) {
            setValue(this);
        }else{
            postValue(this);
        }
    }

    public void clear() {
        this.data = null;
        if (isMainThread()) {
            setValue(this);
        }else{
            postValue(this);
        }
    }

    public void update() {
        if (isMainThread()) {
            setValue(this);
        }else{
            postValue(this);
        }
    }

    public void postUpdate() {
        postValue(this);
    }

    public LiveState getState() {
        return state;
    }

    public void setState(LiveState state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void postValue() {
        this.postValue(this);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }
}

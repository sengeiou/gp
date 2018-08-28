package com.ubtechinc.nets.phonerobotcommunite;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.nets.im.IMErrorUtil;
import com.ubtechinc.nets.im.service.RobotPhoneCommuniteProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IM 超时 管理类
 * Created by ubt on 2017/12/2.
 */

public abstract class ITimeoutEngine {

    Map<Long, RobotPhoneCommuniteProxy.Callback> callbackMap = new HashMap<>();

    List<Integer> whileList;

    public void setTimeout(String peer, int cmId, Long requestId, RobotPhoneCommuniteProxy.Callback dataCallback) {
        if (isInWhileList(cmId)) {
            LogUtils.D("setTimeout cmdId = %d requestId= %d in while list", cmId, requestId);
        } else {
            LogUtils.D("setTimeout cmdId = %d requestId= %d ", cmId, requestId);
            callbackMap.put(requestId, dataCallback);
            onSetTimeout(peer, cmId, requestId);
        }
    }

    public abstract void onSetTimeout(String peer, int cmId, Long requestId);

    public void cancelTimeout(Long requestId) {
        LogUtils.D("cancelTimeout requestId= %d ", requestId);
        onCancelTimeout(requestId);
        callbackMap.remove(requestId);
    }

    public abstract void onCancelTimeout(Long requestId);

    public void onTimeout(Long requestId) {
        LogUtils.D("onTimeout requestId= %d ", requestId);
        RobotPhoneCommuniteProxy.Callback callback = callbackMap.get(requestId);
        if (callback != null) {
            callback.onSendError(requestId, IMErrorUtil.ERROR.TIMEOUT_ERROR);
        }
    }

    public void release() {
        callbackMap.clear();
    }

    private boolean isInWhileList(int cmdId) {
        if (whileList != null) {
            return whileList.contains(cmdId);
        }
        return false;
    }

    public void setWhileList(List<Integer> whileList) {
        this.whileList = whileList;
    }
}

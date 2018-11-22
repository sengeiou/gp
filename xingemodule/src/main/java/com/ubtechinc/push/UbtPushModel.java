package com.ubtechinc.push;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * @author：ubt
 * @date：2018/10/5 17:13
 * @modifier：ubt
 * @modify_date：2018/10/5 17:13
 * [A brief description]
 */

public class UbtPushModel {

    private XGPushRegisterResult mXGPushRegisterResult;

    private XGPushTextMessage mXGPushTextMessage;

    private XGPushShowedResult mXGPushShowedResult;

    private XGPushClickedResult mXGPushClickedResult;

    public UbtPushModel(XGPushRegisterResult XGPushRegisterResult, XGPushTextMessage XGPushTextMessage, XGPushShowedResult XGPushShowedResult, XGPushClickedResult XGPushClickedResult) {
        mXGPushRegisterResult = XGPushRegisterResult;
        mXGPushTextMessage = XGPushTextMessage;
        mXGPushShowedResult = XGPushShowedResult;
        mXGPushClickedResult = XGPushClickedResult;

    }

    public XGPushRegisterResult getXGPushRegisterResult() {
        return mXGPushRegisterResult;
    }

    public void setXGPushRegisterResult(XGPushRegisterResult XGPushRegisterResult) {
        mXGPushRegisterResult = XGPushRegisterResult;
    }

    public XGPushTextMessage getXGPushTextMessage() {
        return mXGPushTextMessage;
    }

    public void setXGPushTextMessage(XGPushTextMessage XGPushTextMessage) {
        mXGPushTextMessage = XGPushTextMessage;
    }

    public XGPushShowedResult getXGPushShowedResult() {
        return mXGPushShowedResult;
    }

    public void setXGPushShowedResult(XGPushShowedResult XGPushShowedResult) {
        mXGPushShowedResult = XGPushShowedResult;
    }

    public XGPushClickedResult getXGPushClickedResult() {
        return mXGPushClickedResult;
    }

    public void setXGPushClickedResult(XGPushClickedResult XGPushClickedResult) {
        mXGPushClickedResult = XGPushClickedResult;
    }

    public String getToken() {
        if (mXGPushRegisterResult != null) {
            return mXGPushRegisterResult.getToken();
        }
        return null;
    }

    public String getContent() {
        if (mXGPushShowedResult != null) {
            return mXGPushShowedResult.getContent();
        }
        return "";
    }
}

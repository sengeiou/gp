package com.ubt.im.listener;

import com.tencent.imsdk.TIMMessage;

public interface OnTIMConversationListener {
    void  onError(int code, String desc);
    void onSuccess(TIMMessage msg);
}

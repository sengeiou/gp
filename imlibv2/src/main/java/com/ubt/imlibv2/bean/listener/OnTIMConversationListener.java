package com.ubt.imlibv2.bean.listener;


import com.tencent.TIMMessage;

public interface OnTIMConversationListener {
    void  onError(int code, String desc);
    void onSuccess(TIMMessage msg);
}

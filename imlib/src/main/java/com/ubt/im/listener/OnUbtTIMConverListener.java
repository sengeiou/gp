package com.ubt.im.listener;

import com.tencent.imsdk.TIMMessage;

public interface OnUbtTIMConverListener {
    void onError(int i, String s);

     void onSuccess(TIMMessage timMessage);
}

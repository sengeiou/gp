package com.ubt.imlibv2.bean.listener;

public interface OnPigOnlineStateListener {
    void onFailure(String erroe);
    void OnSuccess(String account, String state, String msg);
}

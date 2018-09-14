package com.ubt.imlibv2.bean;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.ubt.imlibv2.bean.listener.OnTIMConversationListener;


public class UbtConversation {
    private final static String tag="UbtConversation";
    private TIMConversation conversation;
    private OnTIMConversationListener listener;
    public UbtConversation(String peer) {
        conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);
    }

    public void sendMsg(String msg) {
        if (conversation != null && !TextUtils.isEmpty(msg)) {
            //构造一条消息
            TIMMessage timmsg = new TIMMessage();
            //添加文本内容
            TIMTextElem elem = new TIMTextElem();
            elem.setText(msg);

            //将elem添加到消息
            if (timmsg.addElement(elem) != 0) {
                Log.d("UbtConversation", "addElement failed");
                return;
            }
            //发送消息
            conversation.sendMessage(timmsg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
                @Override
                public void onError(int code, String desc) {//发送消息失败
                    //错误码 code 和错误描述 desc，可用于定位请求失败原因
                    //错误码 code 含义请参见错误码表
                    Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
                    if (listener!=null){
                        listener.onError(code,desc);
                    }
                }

                @Override
                public void onSuccess(TIMMessage msg) {//发送消息成功
                    Log.e(tag, "SendMsg ok");
                    if (listener!=null){
                        listener.onSuccess(msg);
                    }
                }
            });
        }
    }

    public void setListener(OnTIMConversationListener listener) {
        this.listener = listener;
    }
}

package com.ubt.imlibv2.bean.event;


import android.util.Log;

import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMMessageLocator;
import com.tencent.TIMMessageRevokedListener;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtechinc.commlib.log.UbtLogger;

import java.util.List;
import java.util.Observable;

/**
 * 消息通知事件，上层界面可以订阅此事件
 */
public class MessageEvent extends Observable implements TIMMessageListener, TIMMessageRevokedListener {


    private volatile static MessageEvent instance;

    private MessageEvent(){
        //注册消息监听器
        TIMManager.getInstance().addMessageListener(this);


    }

    /*public TIMUserConfig init(TIMUserConfig config) {
        TIMUserConfigMsgExt ext = new TIMUserConfigMsgExt(config);
        ext.enableAutoReport(false)
                .setMessageRevokedListener(this);

        return ext;
    }
*/
    public static MessageEvent getInstance(){
        if (instance == null) {
            synchronized (MessageEvent.class) {
                if (instance == null) {
                    instance = new MessageEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        for (TIMMessage item:list){
            checkMsgType(item);
            setChanged();
            notifyObservers(item);
        }
        return false;
    }

    private void checkMsgType(TIMMessage item) {
        if (item.getElement(0).getType() != TIMElemType.Sound) {
            UbtLogger.d("MessageEvent","checkMsgType "+ item.getElement(0).getType());
            item.getConversation().setReadMessage();
        }else {
            UbtLogger.d("MessageEvent","checkMsgType "+ item.getElement(0).getType());
            UbtTIMManager.getInstance().setUnReadVoiceMailMessage(UbtTIMManager.getInstance().unReadMessage());
        }
    }


    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message){
        UbtLogger.d("MessageEvent","onNewMessage "+"active push notification");
        setChanged();
        notifyObservers(message);
    }

    /**
     * 清理消息监听
     */
    public void clear(){
        instance = null;
    }


    /**
     *     消息被撤回通知监听器
     */
    @Override
    public void onMessageRevoked(TIMMessageLocator timMessageLocator) {
        setChanged();
        notifyObservers(timMessageLocator);
    }


}

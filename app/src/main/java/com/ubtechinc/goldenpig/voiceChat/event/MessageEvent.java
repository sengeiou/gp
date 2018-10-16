package com.ubtechinc.goldenpig.voiceChat.event;


import android.util.Log;

import com.tencent.TIMCustomElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.umeng.analytics.pro.bi;

import java.util.List;
import java.util.Observable;

/**
 * 消息通知事件，上层界面可以订阅此事件
 */
public class MessageEvent extends Observable implements TIMMessageListener {


    private volatile static MessageEvent instance;
    private String TAG="MessageEvent";
    private MessageEvent(){
        //注册消息监听器
        TIMManager.getInstance().addMessageListener(this);
    }

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
            Log.d(TAG,"MessageEvent(Receive TIMMessageListener)--->ChatPresenter(observe update)----->ChatView showMessage -->ChatAdapter notify---->getView --->VoiceMessage(showMessage)   "+list  +item.getElement(0).toString());
//           try {
//               TIMCustomElem customElem = (TIMCustomElem) item.getElement(0);
//               ChannelMessageContainer.ChannelMessage msg = ChannelMessageContainer.ChannelMessage
//                       .parseFrom((byte[]) customElem.getData());
//               Log.d(TAG, "MessageEvent " + msg.getHeader().getAction());
//               if (!msg.getHeader().getAction().equals("/im/voicemail/receiver")) {
//                   return false;
//               }
//           }catch(Exception e){
//              e.printStackTrace();
//           }

            setChanged();
            notifyObservers(item);
        }
        return false;
    }

    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message){
        Log.d(TAG,"onNewMessage  ");
        setChanged();
        notifyObservers(message);
    }

    /**
     * 清理消息监听
     */
    public void clear(){
        instance = null;
    }
}

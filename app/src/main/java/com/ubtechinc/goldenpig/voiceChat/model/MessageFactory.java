package com.ubtechinc.goldenpig.voiceChat.model;


import com.tencent.TIMMessage;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;

/**
 * 消息工厂
 */
public class MessageFactory {
    private MessageFactory() {}

    /**
     * 消息工厂方法
     */
    public static Message getMessage(TIMMessage message){
        switch (message.getElement(0).getType()){
            case Text:
            case Face:
                return new TextMessage(message);
            case Image:
                return new ImageMessage(message);
            case Sound:
                return new VoiceMessage(message);
            case Video:
                return new VideoMessage(message);
            case GroupTips:
                return new GroupTipMessage(message);
            case File:
                return new FileMessage(message);
            case Custom:
                if(ChatActivity.VERSION_BYPASS) {
                    return new CustomMessage(message);
                }else {
                    return new VoiceMessage(message);
                }
            default:
                return null;
        }
    }
}

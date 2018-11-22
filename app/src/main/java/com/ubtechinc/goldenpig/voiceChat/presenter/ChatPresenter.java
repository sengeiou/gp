package com.ubtechinc.goldenpig.voiceChat.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMValueCallBack;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.imlibv2.bean.event.MessageEvent;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.voiceChat.event.RefreshEvent;
import com.ubtechinc.goldenpig.voiceChat.viewfeatures.ChatView;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 聊天界面逻辑
 */
public class ChatPresenter implements Observer {
    private ChatView view;
    private TIMConversation conversation;
    private boolean isGetingMessage = false;
    private final int LAST_MESSAGE_NUM = 200;
    public static int MESSAGE_TEXT=0;
    public static int MESSAGE_VOICE=1;
    public static int MESSAGE_VIDEO=2;
    public static int MESSAGE_FILE=3;
    public static int MESSAGE_IMAGE=4;
    public static int SHOW_MESSAGE_MAX=200;

    public ChatPresenter(ChatView view, String identify, TIMConversationType type){
        this.view = view;
        conversation = TIMManager.getInstance().getConversation(type,identify);
    }

    /**
     * 加载页面逻辑
     */
    public void start() {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
        getMessage(null);
        if (conversation.hasDraft()){
            view.showDraft(conversation.getDraft());
        }
    }

    /**
     * 中止页面逻辑
     */
    public void stop() {
        //注销消息监听
        MessageEvent.getInstance().deleteObserver(this);
        RefreshEvent.getInstance().deleteObserver(this);
    }

    /**
     * 获取聊天TIM会话
     */
    public TIMConversation getConversation(){
        return conversation;
    }

    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
        public void sendMessage(final TIMMessage message, int messageType) {
                    conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int code, String desc) {//发送消息失败
                        Log.d("NYLive", "sendMessage error => code: " + code + ", desc: " + desc);
                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        view.onSendMessageFail(code, desc, message);
                    }
                    @Override
                    public void onSuccess(TIMMessage msg) {
                        Log.d("NYLive", "sendMessage success");
                        //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
                        MessageEvent.getInstance().onNewMessage(null);

                    }
                });
                //message对象为发送中状态
                MessageEvent.getInstance().onNewMessage(message);
        }

//    public void sendMessage(final TIMMessage message, ChannelInfo info) {
//                    conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
//                    @Override
//                    public void onError(int code, String desc) {//发送消息失败
//                        Log.d("NYLive", "sendMessage error => code: " + code + ", desc: " + desc);
//                        //错误码code和错误描述desc，可用于定位请求失败原因
//                        //错误码code含义请参见错误码表
//                        view.onSendMessageFail(code, desc, message);
//                    }
//
//                    @Override
//                    public void onSuccess(TIMMessage msg) {
//                        Log.d("NYLive", "sendMessage success");
//                        //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
//                        MessageEvent.getInstance().onNewMessage(null);
//
//                    }
//                });
//                //message对象为发送中状态
//                MessageEvent.getInstance().onNewMessage(message);
//            }
//        LiveHelper.reqGetAuthority(LiveHelper.getUserId(), info.Id, false, new LiveHelper.Callback<AuthorityInfo>() {
//            @Override
//            public void onData(AuthorityInfo data) {
//                if (data.IsWords) { // 被禁言
//                    LiveHelper.toast("您已被禁言，无法发送消息");
//                    return;
//                }
//                conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
//                    @Override
//                    public void onError(int code, String desc) {//发送消息失败
//                        Log.d("NYLive", "sendMessage error => code: " + code + ", desc: " + desc);
//                        //错误码code和错误描述desc，可用于定位请求失败原因
//                        //错误码code含义请参见错误码表
//                        view.onSendMessageFail(code, desc, message);
//                    }
//
//                    @Override
//                    public void onSuccess(TIMMessage msg) {
//                        Log.d("NYLive", "sendMessage success");
//                        //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
//                        MessageEvent.getInstance().onNewMessage(null);
//
//                    }
//                });
//                //message对象为发送中状态
//                MessageEvent.getInstance().onNewMessage(message);
//            }
//            @Override
//            public void onDataList(List<AuthorityInfo> dataList) {
//            }
//        });
//   }

    /**
     * 发送在线消息
     *
     * @param message 发送的消息
     */
    public void sendOnlineMessage(final TIMMessage message){
        conversation.sendOnlineMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                Log.d("NYLive", "sendOnlineMessage error => code: " + i + ", desc: " + s);
                view.onSendMessageFail(i, s, message);
            }

            @Override
            public void onSuccess(TIMMessage message) {
                Log.d("NYLive", "sendOnlineMessage success");
            }
        });
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            TIMMessage msg = (TIMMessage) data;
            //delete other actions event
            try {
                view.showMessage(msg);
                //msg, sender, read or unread: my pig(me--->pig)  pip group(pig--->pig)
                if(msg!=null) {
                    if (msg.isSelf()) {
                        Log.d("ChatPresenter","SAVE THE MESSAGE ");
                        conversation.saveMessage(msg, UbtTIMManager.userId, true);
                    } else {
                        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                        if (pigInfo != null)
                            conversation.saveMessage(msg, pigInfo.getRobotName(), true);
                    }
                }
                //当前聊天界面已读上报，用于多终端登录时未读消息数同步
                readMessages();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (msg==null||msg.getConversation().getPeer().equals(conversation.getPeer())&&msg.getConversation().getType()==conversation.getType()){
//                view.showMessage(msg);
//                //当前聊天界面已读上报，用于多终端登录时未读消息数同步
//                readMessages();
//            } else if (TIMConversationType.System == msg.getConversation().getType()) {  // 系统消息
//                view.handleSystemMessage(msg);
//            }
        } else if (observable instanceof RefreshEvent) {
            view.clearAllMessage();
            getMessage(null);
        }
    }

    /**
     * 获取消息
     *
     * @param message 最后一条消息
     */
    public void getMessage(@Nullable TIMMessage message){
        if (!isGetingMessage){
            isGetingMessage = true;
            conversation.getLocalMessage(LAST_MESSAGE_NUM, message, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    isGetingMessage = false;
//                    for(int i=0;i<timMessages.size();i++){
//                        Log.d("NYLive","receive the customeMessae local "+i+"    "+timMessages.get(i).getElement(0).getType());
//                    }
                }
            });
            conversation.getMessage(LAST_MESSAGE_NUM, message, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    isGetingMessage = false;
                    Log.e("NYLive","get message error, i: " + i + " s: "+s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    Log.e("NYLive", "get message success" + timMessages.size());
                    isGetingMessage = false;
                    //TODO


                   // TIMMessage mHeadMessage=timMessages.get(0);
                    for(int i=0;i<timMessages.size();i++){
                        Log.e("NYLive", "before get message success index "+i+" type :" + timMessages.get(i).getElement(0).getType());
                    }
                    filter(timMessages);
                    for(int i=0;i<timMessages.size();i++){
                        Log.e("NYLive", "after get message success index "+i+" type :" + timMessages.get(i).getElement(0).getType());
                    }
//                    if(timMessages.size()==0){
//                        Log.e("NYLive", "next get message success" + timMessages.size());
//                        if(message==null) {
//                            getMessage(mHeadMessage);
//                            return;
//                        }
//                    }
                    view.showMessage(timMessages);
                }
            });
        }
    }

    /**
     * 设置会话为已读
     *
     */
    public void readMessages(){
        conversation.setReadMessage();
    }


    /**
     * 保存草稿
     *
     * @param message 消息数据
     */
    public void saveDraft(TIMMessage message){
        conversation.setDraft(null);
        if (message != null && message.getElementCount() > 0){
            TIMMessageDraft draft = new TIMMessageDraft();
            for (int i = 0; i < message.getElementCount(); ++i){
                draft.addElem(message.getElement(i));
            }
            conversation.setDraft(draft);
        }
    }

    private void filter(List<TIMMessage> timMessages) {
        Iterator<TIMMessage> iterable = timMessages.iterator();
        while (iterable.hasNext()) {
           TIMMessage message =  iterable.next();
            try {
                if (message.getElement(0).getType()==TIMElemType.Custom) {
                    iterable.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package com.ubt.imlibv2.bean;


import android.text.TextUtils;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUser;
import com.tencent.TIMUserStatusListener;
import com.tencent.TIMValueCallBack;
import com.ubt.imlibv2.BuildConfig;
import com.ubt.imlibv2.bean.event.MessageEvent;
import com.ubt.imlibv2.bean.listener.OnPigOnlineStateListener;
import com.ubt.imlibv2.bean.listener.OnTIMLoginListener;
import com.ubt.imlibv2.bean.listener.OnUbtTIMConverListener;
import com.ubt.improtolib.UserRecords;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;



/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :处理IM功能管理类
 * @time :2018/9/10 18:10
 * @change :
 * @changetime :2018/9/10 18:10
 */
public class UbtTIMManager {
    private static volatile UbtTIMManager instance;
    private boolean isLoginedTIM; ///是否登录IM
    private TIMRepository repository;
    private TIMPigOnLineRepository onLineRepository;
    private String pigAccount;
    private String channel;
    public static String userId = "";
    private UbtIMCallBack ubtIMCallBack;
    private TIMConversation conversation;
    private final static String TAG = "UbtTIMManager";

    private String accountType;
    private String userSig;
    private int appidAt3rd;
    public static String avatarURL;

    private OnUbtTIMConverListener onUbtTIMConverListener;
    private ArrayBlockingQueue<UbtTIMMsg> msgQueue = new ArrayBlockingQueue<>(16); //IM信息队列
    private long unreadVoiceNumber=-1;

    private UbtTIMManager() {
        repository = new TIMRepository();
        onLineRepository = new TIMPigOnLineRepository();
        onLineRepository.setLoginListener(new OnPigOnlineStateListener() {
            @Override
            public void onFailure(String erroe) {

            }

            @Override
            public void OnSuccess(String account, String state, String msg) {
                //sendTIMMsg( msg);
            }
        });
        setTIMLoginListener();
    }

    public static UbtTIMManager getInstance() {
        if (instance == null) {
            synchronized (UbtTIMManager.class) {
                if (instance == null) {
                    instance = new UbtTIMManager();
                }
            }
        }
        return instance;
    }

    private void sendTIMMsg(String msg) {
        if (conversation != null && !TextUtils.isEmpty(msg)) {
            //构造一条消息
            TIMMessage timmsg = new TIMMessage();
            //添加文本内容
            TIMTextElem elem = new TIMTextElem();
            elem.setText(msg);
            conversation.sendOnlineMessage(timmsg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
                @Override
                public void onError(int code, String desc) {//发送消息失败
                    //错误码 code 和错误描述 desc，可用于定位请求失败原因
                    //错误码 code 含义请参见错误码表
                    Log.d(TAG, "send message failed. code: " + code + " errmsg: " + desc);
                }

                @Override
                public void onSuccess(TIMMessage msg) {//发送消息成功
                    Log.e(TAG, "SendMsg ok");
                }
            });
        }
    }

    public synchronized void loginTIM(String userId, String pigAccount, String channel) {
        this.userId = userId;
        this.pigAccount = pigAccount;
        this.channel = channel;
        if (repository != null && !isLoginedTIM) {
            long time = System.currentTimeMillis();
            String singa = Utils.getSingal(time);
            repository.login(singa, String.valueOf(time), userId, channel);
        }
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setUserId(String userId) {
        UbtTIMManager.userId = userId;
    }

    public void setPigAccount(String account) {
        if (!TextUtils.isEmpty(account)) {
            conversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C,    //会话类型：单聊
                    account);                      //会话对方用户帐号//对方ID
        }

    }

    public void setTIMLoginListener() {
        if (repository != null) {
            repository.setLoginListener(new OnTIMLoginListener() {
                @Override
                public void onFailure(String error) {
                    UbtLogger.e("doTIMLogin", error);
                    ubtIMCallBack.onLoginError(1001, error);

                }

                @Override
                public void OnSuccess(String msg) {
                    try {
                        dealIMResponse(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void dealIMResponse(String msg) throws JSONException {
        if (!TextUtils.isEmpty(msg)) {
            JSONObject jsonObject = new JSONObject(msg);
            if ("success".equals(jsonObject.getString("returnMsg"))) {
                jsonObject = jsonObject.getJSONObject("returnMap");
                accountType = jsonObject.getString("accountType");
                userSig = jsonObject.getString("userSig");
                appidAt3rd = jsonObject.getInt("appidAt3rd");
                doTIMLogin();
            }
        }
    }

    public synchronized void doTIMLogin() {
        if (isLoginedTIM) return;
        final int type = Integer.valueOf(accountType);
        InitBusiness.start(com.ubtech.utilcode.utils.Utils.getContext(), 0, type);
        initUserConfig();
        TIMUser user = new TIMUser();
        user.setIdentifier(userId);
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d("TIMManager", "TIM onForceOffline");
                isLoginedTIM = false;
                if (ubtIMCallBack != null) {
                    ubtIMCallBack.onForceOffline();
                }
            }

            @Override
            public void onUserSigExpired() {
                Log.d("TIMManager", "TIM onUserSigExpired");
                isLoginedTIM = false;
                if (ubtIMCallBack != null) {
                    ubtIMCallBack.onForceOffline();
                }
            }
        });
        try {
            TIMManager.getInstance().login(
                    appidAt3rd,
                    user,
                    userSig,
                    timCallBack);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        sendCache();
    }

    /**
     * TIM登出
     */
    public void doTIMLogout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "TIMCallBack|logout|onError:i:" + i + " s:" + s);
                //TODO 兼容处理，IM退出成功
                isLoginedTIM = false;
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "TIMCallBack|logout|onSuccess");
                isLoginedTIM = false;
            }
        });
    }

    public void setMsgObserve(Observer observe) {
        MessageEvent.getInstance().addObserver(observe);
    }

    public void deleteMsgObserve(Observer observer) {
        MessageEvent.getInstance().deleteObserver(observer);
    }

    private void sendCache() {
        if (msgQueue != null && msgQueue.size() > 0) {
            while (msgQueue.peek() != null) {
                UbtTIMMsg msg = msgQueue.poll();
                send(msg.account, msg.msg);
            }
        }
    }

    public void send(String account, String msg) {
        if (!isLoginedTIM) {//
            UbtTIMMsg ubtMsg = new UbtTIMMsg();
            ubtMsg.msg = msg;
            ubtMsg.account = account;
            msgQueue.add(ubtMsg);
            loginTIM(userId, pigAccount, channel);
        } else {
            //检验八戒是否在线

            long time = System.currentTimeMillis();
            String singa = Utils.getSingal(time);
            onLineRepository.getPligOnLineState(singa, String.valueOf(time), account, BuildConfig.IM_Channel, msg);
        }
    }

    public boolean isLoginedTIM() {
        return isLoginedTIM;
    }


    public void setUbtIMCallBack(UbtIMCallBack callBack) {
        this.ubtIMCallBack = callBack;
    }

    public void removeUbtCallBack() {
        this.ubtIMCallBack = null;
    }

    private TIMCallBack timCallBack = new TIMCallBack() {
        @Override
        public void onError(int i, String s) {
            Log.e(TAG, "TIMCallBack|login|onError:i:" + i + " s:" + s);
            if (ubtIMCallBack != null) {
                ubtIMCallBack.onLoginError(i, s);
            }
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "TIMCallBack|login|onSuccess");
            isLoginedTIM = true;
            setPigAccount(pigAccount);
            if (ubtIMCallBack != null) {
                ubtIMCallBack.onLoginSuccess();
            }
        }
    };

    private void initUserConfig() {
        /*TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "receive force offline message");

            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录

            }
        })
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(TAG, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(TAG, "onWifiNeedAuth");
                    }
                });*/

        //设置刷新监听
        //RefreshEvent.getInstance().init(userConfig);
       /* userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);*/
        // userConfig = MessageEvent.getInstance().init(userConfig);
        //TIMManager.getInstance().setUserConfig(userConfig);
    }

    public void addUser(String nikeName, String number) {

        byte[] data = ContactsProtoBuilder.getAddContactsInfo(nikeName, number);
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void addUser(List<MyContact> list) {

        byte[] data = ContactsProtoBuilder.getAddContactsInfo(list);
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void deleteUser(String nikeName, String number, String userId) {

        byte[] data = ContactsProtoBuilder.getDeleteContactsInfo(nikeName, number, userId);
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void deleteUser(List<AddressBook> list) {
        byte[] data = ContactsProtoBuilder.getDeleteContactsInfo(list);
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void updateUser(String nikeName, String number, String userId) {

        byte[] data = ContactsProtoBuilder.getUpdateContactsInfo(nikeName, number, userId);
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void queryUser() {
        byte[] data = ContactsProtoBuilder.getQueryData();
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    /**
     * 查询金猪基本消息
     */
    public void queryNativeInfo(){
        if (isLoginedTIM) {
            sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getNativeInfo()));
        }
    }

    public void sendTIM(TIMMessage msg) {
        if (!isLoginedTIM) {
            return;
        }
        if (conversation == null) {
            if (onUbtTIMConverListener != null) {
                onUbtTIMConverListener.onError(0, "TIMConversation 未初始化");
            }
            return;
        }
        if (conversation.getType() == TIMConversationType.Invalid) {
            conversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, pigAccount);
        }
        if (conversation.getType() == TIMConversationType.Invalid) {
//            ToastUtils.showShortToast("八戒不在线");
            return;
        }
        conversation.sendOnlineMessage(msg, new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onError(int i, String s) {
                UbtLogger.d("UbtTIMManager","sendTIM|onError");
                UbtLogger.d("UbtTIMManager","1unRead message "+ UbtTIMManager.getInstance().unReadVoiceMailMessage());
                if (onUbtTIMConverListener != null) {
                    onUbtTIMConverListener.onError(i, s);
                }
                if (i == 6200) {
                    ToastUtils.showShortToast("网络异常");
                } else {
                    onSendMsgHook(false);
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                onSendMsgHook(true);
                UbtLogger.d("UbtTIMManager","sendTIM|onSuccess");
                UbtLogger.d("UbtTIMManager","unRead message"+ UbtTIMManager.getInstance().unReadVoiceMailMessage());
                if (onUbtTIMConverListener != null) {
                    onUbtTIMConverListener.onSuccess();
                }
            }
        });
    }

    private void onSendMsgHook(boolean success) {
        if (!success && !isLoginedTIM) {
            loginTIM(userId, pigAccount, channel);
        }
    }

    public void sendTIM(TIMMessage msg, TIMConversation conversation) {
        if (!isLoginedTIM) {
            return;
        }
        if (conversation.getType() == TIMConversationType.Invalid) {
//            ToastUtils.showShortToast("八戒不在线");
            return;
        }
        conversation.sendOnlineMessage(msg, new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "TIMCallBack|sendTIM|onError:i:" + i + " s:" + s);
                onSendMsgHook(false);
                if (onUbtTIMConverListener != null) {
                    onUbtTIMConverListener.onError(i, s);
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                Log.d(TAG, "TIMCallBack|sendTIM|onSuccess");
                onSendMsgHook(true);
                if (onUbtTIMConverListener != null) {
                    onUbtTIMConverListener.onSuccess();
                }
            }
        });
    }

    private TIMMessage creatElem(byte[] data) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();

        elem.setData(data);
        msg.addElement(elem);
        return msg;
    }

    public OnUbtTIMConverListener getOnUbtTIMConverListener() {
        return onUbtTIMConverListener;
    }

    public void setOnUbtTIMConverListener(OnUbtTIMConverListener onUbtTIMConverListener) {
        this.onUbtTIMConverListener = onUbtTIMConverListener;
    }

    public void removeOnUbtTIMConverListener() {
        this.onUbtTIMConverListener = null;
    }


    public interface UbtIMCallBack {
        void onLoginError(int i, String s);

        void onLoginSuccess();

        void onForceOffline();
    }

    public void queryRecord() {
        byte[] data = ContactsProtoBuilder.getQueryRecord();
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void deleteRecord(List<UserRecords.Record> list) {
        byte[] data = ContactsProtoBuilder.getDeleteRecordInfo(list);
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void getGuid() {
        byte[] data = ContactsProtoBuilder.getGuidIM();
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void queryLatestRecord() {
        byte[] data = ContactsProtoBuilder.getLastRecord();
        TIMMessage msg = creatElem(data);
        sendTIM(msg);
    }

    public void setUnReadVoiceMailMessage(long number){
        Log.e(TAG, "set unRead message (voice) " +number );
        unreadVoiceNumber=number;
    }

    /**
     *  messageEVENT接收到IM消息，目前遇到问题是，在进入热点时候，在退出从腾讯返回的接口数值不对
     * @return
     */
    public long unReadVoiceMailMessage() {
        if (conversation == null) {
            return -1;
        }
        if(conversation.getUnreadMessageNum()==0){
            setUnReadVoiceMailMessage(0);
            Log.e(TAG, "get system unRead message " +conversation.getUnreadMessageNum());
            return conversation.getUnreadMessageNum();
        }
        Log.e(TAG, "get variable unRead message " +unreadVoiceNumber);
        return unreadVoiceNumber;
    }

    public long unReadMessage(){
        if (conversation == null) {
            return -1;
        }
      return conversation.getUnreadMessageNum();
    }

}

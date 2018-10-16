package com.ubt.imlibv2.bean;


import android.text.TextUtils;
import android.util.Log;

import com.tencent.TIMConversation;
import com.tencent.TIMCallBack;
import com.tencent.TIMConnListener;
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
import com.ubt.improtolib.UserContacts;
import com.ubt.improtolib.UserRecords;
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
    private String channel;
    public static  String userId="";
    private UbtIMCallBack ubtCallBack;
    private TIMConversation conversation;
    private final static String TAG = "UbtTIMManager";

    private String accountType;
    private String userSig;
    private int appidAt3rd;
    public static String avatarURL;

    private OnUbtTIMConverListener onUbtTIMConverListener;
    private ArrayBlockingQueue<UbtTIMMsg> msgQueue=new ArrayBlockingQueue<>(16); //IM信息队列


    private UbtTIMManager() {
        repository = new TIMRepository();
        onLineRepository=new TIMPigOnLineRepository();
        onLineRepository.setLoginListener(new OnPigOnlineStateListener() {
            @Override
            public void onFailure(String erroe) {

            }

            @Override
            public void OnSuccess(String account,String state,String msg) {
                //sendTIMMsg( msg);
            }
        });
        doTIMLogin();
    }

    public static UbtTIMManager getInstance() {
        if (instance == null)
            synchronized (UbtTIMManager.class) {
                if (instance == null) {
                    instance = new UbtTIMManager();
                }
            }
        return instance;
    }
    private void sendTIMMsg(String msg){
        if (conversation!=null&&!TextUtils.isEmpty(msg)){
            //构造一条消息
            TIMMessage timmsg = new TIMMessage();
            //添加文本内容
            TIMTextElem elem = new TIMTextElem();
            elem.setText(msg);
            conversation.sendMessage(timmsg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
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
    public void loginTIM(String userId, String channel) {
        this.userId = userId;
        this.channel = channel;
        if (repository != null) {
            long time = System.currentTimeMillis();
            String singa = Utils.getSingal(time);
            repository.login(singa, String.valueOf(time), userId, channel);
        }
    }
    public void setPigAccount(String account){
        conversation=TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                account);                      //会话对方用户帐号//对方ID

    }
    public void doTIMLogin() {
        if (repository != null) {
            repository.setLoginListener(new OnTIMLoginListener() {
                @Override
                public void onFailure(String error) {
                    UbtLogger.e("doTIMLogin", error);
                    ubtCallBack.onError(1001, error);

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
                final int type = Integer.valueOf(accountType);
                InitBusiness.start(com.ubtech.utilcode.utils.Utils.getContext(), 0, type);
                initUserConfig();
                TIMUser user=new TIMUser();
                user.setIdentifier(userId);
                try {
                    TIMManager.getInstance().login(
                            appidAt3rd,
                            user,
                            userSig,
                            timCallBack);
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
                isLoginedTIM = true;
                sendCache();
        }
        }
    }
    public void setMsgObserve(Observer observe){
        MessageEvent.getInstance().addObserver(observe);
    }
    public void deleteMsgObserve(Observer observer){
        MessageEvent.getInstance().deleteObserver(observer);
    }
    private void sendCache(){
        if (msgQueue!=null&&msgQueue.size()>0){
            while (msgQueue.peek() != null ) {
                UbtTIMMsg msg=msgQueue.poll();
                send(msg.account,msg.msg);
            }
        }
    }
    public void send(String account,String msg) {
        if (!isLoginedTIM) {//
            UbtTIMMsg ubtMsg=new UbtTIMMsg();
            ubtMsg.msg=msg;
            ubtMsg.account=account;
            msgQueue.add(ubtMsg);
            loginTIM(userId, channel);
        } else {
            //检验小猪是否在线

            long time = System.currentTimeMillis();
            String singa= Utils.getSingal(time);
            onLineRepository.getPligOnLineState(singa,String.valueOf(time),account, BuildConfig.IM_Channel,msg);
        }
    }

    public boolean isLoginedTIM() {
        return isLoginedTIM;
    }


    public void setUbtCallBack(UbtIMCallBack callBack) {
        this.ubtCallBack = callBack;
    }
    public void removeUbtCallBack() {
        this.ubtCallBack = null;
    }
    private TIMCallBack timCallBack = new TIMCallBack() {
        @Override
        public void onError(int i, String s) {
            if (ubtCallBack != null) {
                ubtCallBack.onError(i, s);
            }
        }

        @Override
        public void onSuccess() {
            if (ubtCallBack != null) {
                ubtCallBack.onSuccess();
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
    public void  addUser(String nikeName,String number) {


        byte[] data = ContactsProtoBuilder.getAddContactsInfo(nikeName, number);
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }
    public void  deleteUser(String nikeName,String number,String userId) {


        byte[] data = ContactsProtoBuilder.getDeleteContactsInfo(nikeName, number,userId);
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }
    public void updateUser(String nikeName,String number,String userId) {


        byte[] data = ContactsProtoBuilder.getUpdateContactsInfo(nikeName, number,userId);
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }
    public void queryUser() {
        byte[] data = ContactsProtoBuilder.getQueryData();
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }
    public void sendTIM(TIMMessage msg){
        if (conversation==null){
            if (onUbtTIMConverListener!=null){
                onUbtTIMConverListener.onError(0,"TIMConversation 未初始化");
            }
            return;
        }
        conversation.sendMessage(msg,new TIMValueCallBack<TIMMessage>(){

            @Override
            public void onError(int i, String s) {
                if (onUbtTIMConverListener!=null){
                    onUbtTIMConverListener.onError(i,s);
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (onUbtTIMConverListener!=null){
                    onUbtTIMConverListener.onSuccess();
                }
            }
        });
    }

    private TIMMessage creatElem(byte[] data){
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
    public void removeOnUbtTIMConverListener(){
        this.onUbtTIMConverListener=null;
    }



    public interface UbtIMCallBack {
        void onError(int i, String s);

        void onSuccess();
    }

    public void queryRecord() {
        byte[] data = ContactsProtoBuilder.getQueryRecord();
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }

    public void  deleteRecord(List<UserRecords.Record> list) {
        byte[] data = ContactsProtoBuilder.getDeleteRecordInfo(list);
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }

    public void getGuid(){
        byte[] data = ContactsProtoBuilder.getGuidIM();
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }

    public void queryLatestRecord() {
        byte[] data = ContactsProtoBuilder.getLastRecord();
        TIMMessage msg=creatElem(data);
        sendTIM(msg);
    }
}

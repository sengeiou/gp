package com.ubtechinc.nets.im.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.GeneratedMessageV3;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.ImMsgDispathcer;
import com.ubtechinc.nets.http.Utils;
import com.ubtechinc.nets.im.IMErrorUtil;
import com.ubtechinc.nets.im.business.LoginBussiness;
import com.ubtechinc.nets.im.business.ReceiveMessageBussinesss;
import com.ubtechinc.nets.im.business.SendMessageBusiness;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtechinc.nets.phonerobotcommunite.IReceiveMsg;
import com.ubtechinc.nets.phonerobotcommunite.ISendMsg;
import com.ubtechinc.nets.phonerobotcommunite.ITimeoutEngine;
import com.ubtechinc.nets.phonerobotcommunite.NetCheckEngine;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by Administrator on 2017/5/24.
 */

public class RobotPhoneCommuniteProxy<T> {

    HashMap<Long, RequestBean> requestCache = new HashMap<>();
    AtomicLong requestSerial = new AtomicLong();

    private ISendMsg sendMsgEngine = SendMessageBusiness.getInstance();
    private IReceiveMsg receiveMsgEngine = ReceiveMessageBussinesss.getInstance();
    private ITimeoutEngine timeoutEngine;
    private NetCheckEngine.ConnectListener connectListener;

    private static final String TAG = "RobotPhoneComProxy";

    private static RobotPhoneCommuniteProxy sInstance;

    public static RobotPhoneCommuniteProxy getInstance() {
        if (sInstance == null) {
            synchronized (RobotPhoneCommuniteProxy.class) {
                if(sInstance == null){
                    sInstance = new RobotPhoneCommuniteProxy();
                }
            }
        }
        return sInstance;
    }

    private synchronized void sendData(int cmdId, String version, long responseSerail, GeneratedMessageLite requestBody, String peer, Callback callback) {
        long requestId = requestSerial.incrementAndGet();
        AlphaMessageOuterClass.AlphaMessage msgRequest = ProtoBufferDispose.buildAlphaMessage(cmdId, version, requestBody, requestId, responseSerail);
        LogUtils.d("sendData--cmdId = " + msgRequest.getHeader().getCommandId() + ", sendSerail = " + msgRequest.getHeader().getSendSerial()
                + ", responseSerail = " + msgRequest.getHeader().getResponseSerial());

        byte[] bytes = ProtoBufferDispose.getPackMData(msgRequest);
        requestCache.put(requestId, new RequestBean(cmdId, callback));

        sendMsgEngine.sendMsg(requestId, peer, bytes, callback);

    }

    private synchronized void sendData(int cmdId, String version, long responseSerail, byte[] requestBody, String peer, Callback callback) {
        long requestId = requestSerial.incrementAndGet();
        AlphaMessageOuterClass.AlphaMessage msgRequest = ProtoBufferDispose.buildAlphaMessage(cmdId, version, requestBody, requestId, responseSerail);
        LogUtils.d("sendData--cmdId = " + msgRequest.getHeader().getCommandId() + ", sendSerail = " + msgRequest.getHeader().getSendSerial()
                + ", responseSerail = " + msgRequest.getHeader().getResponseSerial());

        byte[] bytes = ProtoBufferDispose.getPackMData(msgRequest);
        requestCache.put(requestId, new RequestBean(cmdId, callback));

        sendMsgEngine.sendMsg(requestId, peer, bytes, callback);

    }


    private synchronized void sendData(int cmdId, String version, long responseSerail, GeneratedMessageV3 requestBody, String peer, Callback callback) {
        long requestId = requestSerial.incrementAndGet();
        AlphaMessageOuterClass.AlphaMessage msgRequest = ProtoBufferDispose.buildAlphaMessage(cmdId, version, requestBody, requestId, responseSerail);
        LogUtils.d("sendData--cmdId = " + msgRequest.getHeader().getCommandId() + ", sendSerail = " + msgRequest.getHeader().getSendSerial()
                + ", responseSerail = " + msgRequest.getHeader().getResponseSerial());

        byte[] bytes = ProtoBufferDispose.getPackMData(msgRequest);
        requestCache.put(requestId, new RequestBean(cmdId, callback));
        sendMsgEngine.sendMsg(requestId, peer, bytes, callback);

    }

    public void init() {
        sendMsgEngine.init();
        receiveMsgEngine.init();
    }

    public void setIMsgDispatcher(ImMsgDispathcer msgDispathcer) {
        receiveMsgEngine.setIMsgDispatcher(msgDispathcer);
    }

    public void setTimeoutEngine(ITimeoutEngine timeoutEngine) {
        this.timeoutEngine = timeoutEngine;
    }

    public void setConnectListener(NetCheckEngine.ConnectListener connectListener){
        this.connectListener = connectListener;
    }
    /**
     * 发送回复消息
     */
    public void sendResponseMessage(final int cmdId, String version, long responseSerial, final GeneratedMessageLite requestBody, final String peer, final ICallback dataCallback) {
        sendData(cmdId, version, responseSerial, requestBody, peer, new Callback() {

            @Override
            public void onSendSuccess(long requestId) {
                //消息发送成功，等待回调
                onSendDataSuccess(peer, cmdId, requestId, this);
            }

            @Override
            public void onSendError(long requestId, int errorCode) {
                onSendDataError(peer, requestId, errorCode, dataCallback);
            }

            @Override
            public void onReturnMessage(long requestId, AlphaMessageOuterClass.AlphaMessage response) {
                cancelTimeout(requestId);
                if (dataCallback != null) {
                    dataCallback.onSuccess(response);
                    requestCache.remove(requestId);
                }
            }

        });
    }

    public void sendMessage(final int cmdId, String version, @NonNull GeneratedMessageLite requestBody, final String peer, final ICallback<AlphaMessageOuterClass.AlphaMessage> dataCallback) {
        sendData(cmdId, version, 0, requestBody, peer, new Callback() {

            @Override
            public void onSendSuccess(long requestId) {
                //消息发送成功，等待回调
                onSendDataSuccess(peer, cmdId, requestId, this);
            }

            @Override
            public void onSendError(long requestId, int errorCode) {
                onSendDataError(peer, requestId, errorCode, dataCallback);
            }

            @Override
            public void onReturnMessage(long requestId, AlphaMessageOuterClass.AlphaMessage response) {
                cancelTimeout(requestId);
                if (dataCallback != null) {
                    dataCallback.onSuccess(response);
                    requestCache.remove(requestId);
                }
            }

        });
    }

    public void sendMessage2Robot(final int cmdId, String version, final @NonNull GeneratedMessageLite requestBody, final String peer, final ICallback<T> dataCallback) {
        sendData(cmdId, version, 0, requestBody, peer, new Callback() {

            @Override
            public void onSendSuccess(long requestId) {
                //消息发送成功，等待回调
                onSendDataSuccess(peer, cmdId, requestId, this);
            }

            @Override
            public void onSendError(long requestId, int errorCode) {
                onSendDataError(peer, requestId, errorCode, dataCallback);
            }

            @Override
            public void onReturnMessage(long requestId, AlphaMessageOuterClass.AlphaMessage response) {
                NetCheckEngine.get().stop();
                cancelTimeout(requestId);
                if (dataCallback != null) {
                    if (response != null && response.getBodyData() != null) {

                        Type[] types = ((ParameterizedType) dataCallback.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
                        if (Utils.hasUnresolvableType(types[0])) {
                            return;
                        }
                        Class<T> entityClass = (Class<T>) ((ParameterizedType) dataCallback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                        GeneratedMessageLite generatedMessageLite = ProtoBufferDispose.unPackData(entityClass, response.getBodyData().toByteArray());
                        if (generatedMessageLite != null) {
                            dataCallback.onSuccess((T) generatedMessageLite);

                        } else {
                            dataCallback.onError(IMErrorUtil.handleException(IMErrorUtil.ERROR.NULL_DATA));
                        }
                    }

                    requestCache.remove(requestId);
                }
            }

        });
    }

    public void sendCheckNet2Robot(final int cmdId, String version, final @NonNull GeneratedMessageLite requestBody, final String peer, Callback dataCallback) {
        sendData(cmdId, version, 0, requestBody, peer, dataCallback);
    }

    public void sendMessage2Robot(final int cmdId, String version, byte[] bytes, final String peer, final ICallback<T> dataCallback) {
        sendData(cmdId, version, 0, bytes, peer, new Callback() {

            @Override
            public void onSendSuccess(long requestId) {
                //消息发送成功，等待回调
                onSendDataSuccess(peer, cmdId, requestId, this);
            }

            @Override
            public void onSendError(long requestId, int errorCode) {
                onSendDataError(peer, requestId, errorCode, dataCallback);
            }

            @Override
            public void onReturnMessage(long requestId, AlphaMessageOuterClass.AlphaMessage response) {
                cancelTimeout(requestId);
                if (dataCallback != null) {
                    if (response != null && response.getBodyData() != null) {


                        Type[] types = ((ParameterizedType) dataCallback.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
                        if (Utils.hasUnresolvableType(types[0])) {
                            return;
                        }
                        Class<T> entityClass = (Class<T>) ((ParameterizedType) dataCallback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                        GeneratedMessageLite generatedMessageLite = ProtoBufferDispose.unPackData(entityClass, response.getBodyData().toByteArray());
                        if (generatedMessageLite != null) {
                            dataCallback.onSuccess((T) generatedMessageLite);

                        } else {
                            dataCallback.onError(IMErrorUtil.handleException(IMErrorUtil.ERROR.NULL_DATA));
                        }
                    }

                    requestCache.remove(requestId);
                }
            }

        });
    }

    private void setTimeout(String peer, int cmId, long id, Callback callback) {
        if (timeoutEngine != null) {
            timeoutEngine.setTimeout(peer, cmId, id, callback);
        }
    }

    private void cancelTimeout(long id) {
        if (timeoutEngine != null) {
            timeoutEngine.cancelTimeout(id);
        }
    }

    public void dispatchResponse(long responseId, AlphaMessageOuterClass.AlphaMessage cmdBody) {
        RequestBean bean = requestCache.get(responseId);
        if (bean != null && bean.callback != null) {
            bean.callback.onReturnMessage(responseId, cmdBody);
        }
        requestCache.remove(responseId);
    }


    static class RequestBean {
        int cmdId;
        Callback callback;

        RequestBean(int cmdId, Callback callback) {
            this.cmdId = cmdId;
            this.callback = callback;
        }
    }


    public interface Callback {
        void onSendSuccess(long requestId); //消息发送成功

        void onSendError(long requestId, int errorCode);

        void onReturnMessage(long requestId, AlphaMessageOuterClass.AlphaMessage response); //收到返回的数据
    }

    private void onSendDataError(String peer, long requestId, int errorCode, ICallback dataCallback) {
        cancelTimeout(requestId);
        if (dataCallback != null) {
            dataCallback.onError(IMErrorUtil.handleException(errorCode));
            requestCache.remove(requestId);
        }
        NetCheckEngine.get().startCheck(peer, connectListener);
    }

    private void onSendDataSuccess(String peer, int cmdId, long requestId, Callback callback) {
        Log.d(TAG, "onSendSuccess---");
        setTimeout(peer, cmdId, requestId, callback);
    }

}
package com.ubtechinc.nets.phonerobotcommunite;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.CmConfirmOnline;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.nets.im.service.RobotPhoneCommuniteProxy;

public class NetCheckEngine {

    public static final String TAG = "NetCheckEngine";

    public static final int MSG_WHAT_NET_CHECK = 100;

    private static NetCheckEngine instance;

    private HandlerThread handlerThread;

    private Handler handler;

    private ConnectListener connectListener;


    public static NetCheckEngine get() {
        if (instance == null) {
            synchronized (NetCheckEngine.class) {
                if (instance == null) {
                    instance = new NetCheckEngine();
                }
            }
        }
        return instance;
    }

    private NetCheckEngine() {

    }

    private void init() {
        handlerThread = new HandlerThread("net_check_engine");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                if (MSG_WHAT_NET_CHECK == msg.what) {
                    checkNet((String) msg.obj);
                }
            }

        };
    }

    private void checkNet(final String peer) {
        LogUtils.d(TAG, "do net check");
        CmConfirmOnline.CmConfirmOnlineRequest request = CmConfirmOnline.CmConfirmOnlineRequest.getDefaultInstance();
        RobotPhoneCommuniteProxy.getInstance().sendCheckNet2Robot(IMCmdId.IM_CONFIRM_ONLINE_REQUEST, IMCmdId.IM_VERSION, request, peer, new RobotPhoneCommuniteProxy.Callback() {
            @Override
            public void onSendSuccess(long requestId) {
                LogUtils.d(TAG, "do net check onSendSuccess");
                stop();
                if (connectListener != null) {
                    connectListener.onReconnected();
                }
            }

            @Override
            public void onSendError(long requestId, int errorCode) {
                LogUtils.d(TAG, "do net check onSendError" + errorCode);
                startCheck(peer);
                if (connectListener != null) {
                    connectListener.onDisconnected();
                }
            }

            @Override
            public void onReturnMessage(long requestId, AlphaMessageOuterClass.AlphaMessage response) {
                stop();
            }
        });
    }

    public void startCheck(String peer, ConnectListener listener) {
        this.connectListener = listener;
        startCheck(peer);
    }

    private void startCheck(String peer) {
        LogUtils.d(TAG, "Start net check");
        if (handler == null) {
            init();
        }
        if (!handler.hasMessages(MSG_WHAT_NET_CHECK)) {
            Message message = Message.obtain();
            message.what = MSG_WHAT_NET_CHECK;
            message.obj = peer;
            handler.sendMessageDelayed(message, 5000);
        }
    }

    public void stop() {
        LogUtils.d(TAG, "Stop net check");
        if (handler != null && handler.hasMessages(MSG_WHAT_NET_CHECK)) {
            handler.removeMessages(MSG_WHAT_NET_CHECK);
        }
    }

    public interface ConnectListener {

        public void onReconnected();

        public void onDisconnected();
    }
}

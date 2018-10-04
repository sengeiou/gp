package com.ubtechinc.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.bluetooth.command.IAbstractBleCommandFactory;
import com.ubtechinc.bluetooth.command.ICommandEncode;
import com.ubtechinc.bluetooth.command.JsonAbstractBleCommandFactory;
import com.ubtechinc.bluetooth.utils.BLEcryption;

import java.util.UUID;

import static android.util.Log.d;
import static android.util.Log.e;

/**
 * @author：wululin
 * @date：2017/10/18 18:34
 * @modifier：ubt
 * @modify_date：2017/10/18 18:34
 * [A brief description]
 * version
 * 蓝牙连接管理者 -- 只处理连接和断开
 */

class UbtBluetoothConnector {
    private static final String TAG = UbtBluetoothConnector.class.getSimpleName();
    private static final String UUID_SERVICE = "10057e66-28bd-4c3e-a030-08b4982739bc";
    private static final String UUID_NOTIFY_CHARACTER = "7e0dffa2-c44f-48ac-a39e-3fce46d9aabb";
    private static final String UUID_WRITE_CHARACTER = "d2f9ff26-ad08-4bd3-8436-9b8f33bdea76";
    private static final int MSG_WRITE_REQUEST = 1000;
    private static final int MSG_CONNECT_FAILED = 0x001;
    private static final int MSG_CONNECT_SUCCESS = 0x002;
    private static final int MSG_RECEIVER_DATA_FROM_ROBOT = 0x003;
    private static final int MSG_SEND_DATA_FAILED = 0x004;
    private static final int MSG_SEND_DATA_SUCCESS = 0x005;
    private final Context mContext;
    private boolean mIsShutdown = false;
    private HandlerThread mMessageThread;
    private Handler mMessageHandler = null;
    private BLEcryption mEncryption;
    private volatile UbtBluetoothDevice mCurrentDevice;
    private BluetoothGattCallback mBluetoothGattCallback;
    private BluetoothGatt mCurrentBluetoothGatt;
    private final byte[] mSyncLock = new byte[0];
    private BleConnectListener mBleConnectListener;
    private volatile ConnectState connectState = ConnectState.INIT;
    private ICommandEncode commandEncode;
    // 判断是否写入成功
    private volatile OnceResponse onceResponse = new OnceResponse();//记录每一次写入是否成功的变量
    //private Object semaphore = new Object();
    private volatile int mConnectTime; /// 重连次数

    public enum ConnectState {
        INIT, CONNECTING, CONNECTED, DISCOVERING, DISCOVERED, DISCONNECTING, DISCONNECTED,
    }

    UbtBluetoothConnector(Context context) {
        this.mContext = context;
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_FAILED:
                    if (mBleConnectListener != null) {
                        mBleConnectListener.connectFailed();//重连扫描失败就算连接失败
                    }
                    break;
                case MSG_CONNECT_SUCCESS:
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    if (mBleConnectListener != null) {
                        mBleConnectListener.connectSuccess(bluetoothDevice);
                    }
                    break;
                case MSG_RECEIVER_DATA_FROM_ROBOT:
                    String decryptStr = (String) msg.obj;
                    if (mBleConnectListener != null) {
                        mBleConnectListener.receiverDataFromRobot(decryptStr);
                    }
                    break;
                case MSG_SEND_DATA_FAILED:
                    String message = (String) msg.obj;
                    if (mBleConnectListener != null) {
                        mBleConnectListener.sendDataFailed(message);
                    }
                    break;
                case MSG_SEND_DATA_SUCCESS:
                    String message1 = (String) msg.obj;
                    if (mBleConnectListener != null) {
                        mBleConnectListener.sendDataSuccess(message1);
                    }
                    break;
            }
        }
    };

    UbtBluetoothDevice getCurrentDevice() {
        return mCurrentDevice;
    }

    /**
     * 连接特定设备
     *
     * @param device 设备
     */

    void connect(UbtBluetoothDevice device) {
        synchronized (mSyncLock) {
            if (isNotInConnecting()) {
                mCurrentDevice = device;
                mIsShutdown = false;
                if (device!=null) {
                    encrypt(device.getSn(), device.needEncrption);
                    connectInner(device.getDevice());
                    IAbstractBleCommandFactory abstractBleCommandFactory =
                            new JsonAbstractBleCommandFactory(device.getSn(), device.needEncrption);
                    commandEncode = abstractBleCommandFactory.getCommandEncode();
                }
            } else {
                Log.e(TAG, "Illegal State: 蓝牙正在建立连接...");
            }
        }
    }

    boolean isNotInConnecting() {
        return connectState == ConnectState.INIT
                || connectState == ConnectState.DISCONNECTED
                || mCurrentDevice == null;
    }

    /**
     * 生成秘钥, 获取机器人是否需要加密
     */
    private void encrypt(String searialNumber, boolean needEncrption) {
        if (mEncryption == null) {
            mEncryption = new BLEcryption();
        }
        mEncryption.setNeedEncrpt(needEncrption);
        mEncryption.genatorPassWord(searialNumber);
    }

    /**
     * 连接设备
     *
     * @param device 设备
     */
    private void connectInner(final BluetoothDevice device) {
        Log.v(TAG, "connect to device : " + device.getAddress());
        if (mBluetoothGattCallback == null) {
            mBluetoothGattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    Log.d(TAG, "onConnectionStateChange status = "
                            + Integer.toHexString(status)
                            + ", newState = "
                            + newState);
                    if (status == BluetoothGatt.GATT_SUCCESS) {//本次操作成功
                        if (newState == BluetoothGatt.STATE_CONNECTED) {//本次操作是连接, 连接成功
                            synchronized (mSyncLock) {
                                UbtBluetoothConnector.this.connectState = ConnectState.CONNECTED;
                                gatt.discoverServices();//中央client已经与周边Server连上了,则开始发现周边Server提供的Services
                                UbtBluetoothConnector.this.connectState = ConnectState.DISCOVERING;
                            }
                        } else {//本次操作是断开, 且断开成功, 则close gatt.
                            synchronized (mSyncLock) {
                                if (mIsShutdown) closeGatt(gatt);
                            }
                        }
                    } else {//本次操作失败,考虑是否重新触发操作
                        if (newState == BluetoothGatt.STATE_CONNECTED
                                || newState == BluetoothGatt.STATE_CONNECTING) {
                            synchronized (mSyncLock) {
                                if (mCurrentDevice != null) {
                                    Log.w(TAG, "connect error, status = "
                                            + Integer.toHexString(status)
                                            + ", newState = "
                                            + newState
                                            + ", sn ="
                                            + mCurrentDevice.getSn()
                                            + ", rssi = "
                                            + mCurrentDevice.getRssi());
                                }
                            }
                            mMainHandler.sendEmptyMessage(MSG_CONNECT_FAILED);
                        } else {//断开连接操作失败,考虑再次断开
                            synchronized (mSyncLock) {
                                stopConnection(gatt, false);
                                if (!mIsShutdown) {
                                    Log.w(TAG, "connect error, status = "
                                            + Integer.toHexString(status)
                                            + ", newState = "
                                            + newState
                                            + ", sn ="
                                            + mCurrentDevice.getSn()
                                            + ", rssi = "
                                            + mCurrentDevice.getRssi());
                                    mMainHandler.sendEmptyMessageDelayed(MSG_CONNECT_FAILED, 20);
                                }
                            }
                        }
                    }
                }

                @Override //中央已经发现了周边Server
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);

                    synchronized (mSyncLock) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            UUID serviceUUID = UUID.fromString(UUID_SERVICE);
                            UUID notifyUUID = UUID.fromString(UUID_NOTIFY_CHARACTER);

                            //查找这个周边设备的指定serviceUUID所对应的服务
                            BluetoothGattService service = gatt.getService(serviceUUID);

                            if (service != null) {
                                BluetoothGattCharacteristic notifyCharacteristic =
                                        service.getCharacteristic(notifyUUID);//查找这个服务的指定特征
                                if (notifyCharacteristic != null) {
                                    UbtBluetoothConnector.this.connectState =
                                            ConnectState.DISCOVERED;//发现周边设备指定服务的指定特征
                                    boolean success = gatt.setCharacteristicNotification(notifyCharacteristic, true);
                                    if (success) {
                                        Message msg = Message.obtain();
                                        msg.what = MSG_CONNECT_SUCCESS;//特征激活成功, 通知外部模块写入数据,
                                        msg.obj = device;
                                        mMainHandler.sendMessage(msg);
                                    }else {
                                        LogUtils.w("enable CharacteristicNotification fail ");
                                    }
                                }else {
                                    LogUtils.w("cannot find notify = " + UUID_NOTIFY_CHARACTER);
                                }
                            }else {
                                LogUtils.w("cannot find service = " + UUID_SERVICE);
                            }
                        }
                    }
                }

                @Override //当向Characteristic写数据时会回调该函数
                public void onCharacteristicWrite(BluetoothGatt gatt,
                                                  BluetoothGattCharacteristic characteristic, int status) {
                    synchronized (mSyncLock) {
                        onceResponse.success = (status == BluetoothGatt.GATT_SUCCESS);
                        onceResponse.hasResponse = true;
                        Log.w(TAG, "onCharacteristicWrite...success = " + onceResponse.success);
                        mSyncLock.notifyAll();
                    }
                }

                @Override //设备发出通知时会调用到该接口
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    if (commandEncode.addData(characteristic.getValue())) {
                        String result = commandEncode.getCommand();
                        Message msg = Message.obtain();
                        msg.what = MSG_RECEIVER_DATA_FROM_ROBOT;
                        msg.obj = result;
                        mMainHandler.sendMessage(msg);
                    }
                }
            };
        }
        connectState = ConnectState.CONNECTING;
        mCurrentBluetoothGatt = device.connectGatt(mContext, false, mBluetoothGattCallback);
    }

    /**
     * 发送信息
     *
     * @param msg 消息
     */
    void sendMessage(String msg) {
        if (connectState == UbtBluetoothConnector.ConnectState.DISCOVERED) {
            Message message = Message.obtain();
            message.obj = msg;
            message.what = MSG_WRITE_REQUEST;
            lazySetupMessageHandler();
            mMessageHandler.sendMessage(message);
            mConnectTime=0;
        } else {
            Log.e(TAG, "Illegal State: 还未与机器人建立服务连接...");
            if (mConnectTime<6&&mCurrentDevice!=null){
                mConnectTime++;
                connect(mCurrentDevice);
            }
        }
    }

    void setBleConnectListener(BleConnectListener bleConnectListener) {
        this.mBleConnectListener = bleConnectListener;
    }

    /**
     * 初始化MessageHandler
     */
    private void lazySetupMessageHandler() {
        synchronized (mSyncLock) {
            if (mMessageThread == null) {
                mMessageThread = new HandlerThread("thread-send-message");
                mMessageThread.start();
                mMessageHandler = new Handler(mMessageThread.getLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        String originMsg = (String) message.obj;
                        //Log.e(TAG, "发送的原始数据: " + originMsg);
                        String msg = commandEncode.encryption(originMsg);
                        if (!TextUtils.isEmpty(msg)) {
                            d(TAG, "发送加密后的数据: " + msg);
                            byte[][] packets = commandEncode.encode(msg);
                            long beforeTime = System.currentTimeMillis();
                            int tryCount = 5;// 一直发送直至超时
                            boolean sendCmdSuccess = false, sendPacketSuccess;
                            while (--tryCount > 0) {
                                sendCmdSuccess = true;
                                for (byte[] bytes : packets) {
                                    sendPacketSuccess = false;
                                    if (mCurrentBluetoothGatt != null) {
                                        BluetoothGattService service =
                                                mCurrentBluetoothGatt.getService(UUID.fromString(UUID_SERVICE));
                                        if (service != null) {
                                            BluetoothGattCharacteristic characteristic =
                                                    service.getCharacteristic(UUID.fromString(UUID_WRITE_CHARACTER));
                                            characteristic.setValue(bytes);
                                            synchronized (mSyncLock) {
                                                onceResponse.success = false;
                                                onceResponse.hasResponse = false;
                                                //发送数组内元素
                                                boolean writeSuccess =
                                                        mCurrentBluetoothGatt.writeCharacteristic(characteristic);
                                                // 等待发送成功回调
                                                Log.e(TAG, "wait ----- 1");
                                                try {
                                                    if (!onceResponse.hasResponse && writeSuccess) {
                                                        mSyncLock.wait();
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                Log.e(TAG, "wait ----- 2 -- onceResponse: " + onceResponse.success);
                                                sendPacketSuccess = onceResponse.success;
                                            }
                                        }
                                    }
                                    if (!sendPacketSuccess) {//如果发数组第一个元素失败,则直接跳出for循环,尝试重试
                                        sendCmdSuccess = false;
                                        break;
                                    }
                                }
                                if (mIsShutdown) {
                                    Log.d(TAG, "用户断开连接连接");
                                    break;
                                } else if (sendCmdSuccess) {
                                    Log.d(TAG, "命令发送成功-> costTime : " + (System.currentTimeMillis() - beforeTime));
                                    break;
                                }
                            }

                            if (sendCmdSuccess && !mIsShutdown) {
                                Message successMsg = Message.obtain();
                                successMsg.what = MSG_SEND_DATA_SUCCESS;
                                successMsg.obj = msg;
                                mMainHandler.sendMessage(successMsg);
                            } else {
                                Log.d(TAG, "send failed : " + msg);
                                Message failedMsg = Message.obtain();
                                failedMsg.what = MSG_SEND_DATA_FAILED;
                                failedMsg.obj = msg;
                                mMainHandler.sendMessage(failedMsg);
                            }
                        }
                    }
                };
            }
        }
    }

    /**
     * 停止连接
     */
    private void stopConnection(BluetoothGatt gatt, boolean needSleep) {
        if (mMessageHandler != null) {
            mMessageHandler.removeMessages(MSG_WRITE_REQUEST);
        }
        //关闭所有的gatt
        synchronized (mSyncLock) {
            connectState = ConnectState.DISCONNECTED;
            if (gatt != null) {
                d(TAG, "stop gatt : " + gatt);
                gatt.disconnect();
                //gatt.close();//close的gatt是不能重连的
                if (needSleep) {
                    SystemClock.sleep(80);//FIXME logic.peng
                }
            } else {
                connectState = ConnectState.INIT;
            }
        }
    }

    private void closeGatt(BluetoothGatt gatt) {
        if (gatt != null) {
            Log.w(TAG, "gatt close...");
            gatt.close();
            connectState = ConnectState.INIT;
            mCurrentBluetoothGatt = null;
        }
    }

    ConnectState getConnectState() {
        return connectState;
    }

    boolean isClosed() {
        synchronized (mSyncLock) {
            return mIsShutdown;
        }
    }

    /**
     * 关闭连接
     */
    void closeConnection() {
        synchronized (mSyncLock) {
            if (!mIsShutdown) {
                mIsShutdown = true;
                stopConnection(mCurrentBluetoothGatt, true);
                closeGatt(mCurrentBluetoothGatt);
                mBluetoothGattCallback = null;
                mCurrentDevice = null;
                if (mMessageThread != null) {
                    mMessageThread.quitSafely();
                    mMessageThread = null;
                    mMessageHandler = null;
                }
            }
        }
    }

    private static class OnceResponse {
        boolean success;
        boolean hasResponse;
    }
}

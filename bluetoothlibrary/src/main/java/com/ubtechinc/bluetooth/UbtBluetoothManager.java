package com.ubtechinc.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.ubtechinc.bluetooth.event.BleScanFailEvent;
import com.ubtechinc.bluetooth.event.BleScanFinishedEvent;
import com.ubtechinc.bluetooth.event.BleScanResultEvent;

import java.lang.reflect.Method;

import org.greenrobot.eventbus.EventBus;

/**
 * @author：wululin
 * @date：2017/10/19 9:39
 * @modifier：ubt
 * @modify_date：2017/10/19 9:39
 * [蓝牙管理类]---蓝牙扫描和连接--如果连接上蓝牙,由该类来处理停止扫描, 重试逻辑
 * version
 */

public class UbtBluetoothManager {
    private static final String TAG = "UbtBluetoothManager";
    private UbtBluetoothConnector btConnector;
    private UbtBluetoothScanManager btScanner;
    private boolean isChangeWifi = false;
    private boolean isFromHome = false; //是否从home页进入
    private boolean isFromCodeMao = false; //是否从home页进入
    private volatile BleConnectListener mOuterConnectListener;
    private volatile UbtBluetoothDevice device;

    private UbtBluetoothManager(Context context) {
        init(context);
    }

    public boolean isChangeWifi() {
        return isChangeWifi;
    }

    public void setChangeWifi(boolean changeWifi) {
        isChangeWifi = changeWifi;
    }

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private static Context getContext() {
        if (mContext != null) return mContext;
        synchronized (UbtBluetoothManager.class) {
            if (mContext == null) {
                try {
                    @SuppressLint("PrivateApi") final Class<?> activityThread =
                            Class.forName("android.app.ActivityThread");
                    final Method currentApplicationMethod =
                            activityThread.getDeclaredMethod("currentApplication");
                    mContext = (Context) currentApplicationMethod.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mContext;
        }
    }

    public static UbtBluetoothManager getInstance() {
        return Holder._instance;
    }

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static UbtBluetoothManager _instance =
                new UbtBluetoothManager(getContext());
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        btScanner = new UbtBluetoothScanManager(context);
        btConnector = new UbtBluetoothConnector(context);
        btScanner.setAutoConnectListener(new UbtBluetoothScanManager.AutoConnectListener() {
            @Override
            public void onAutoConnect(UbtBluetoothDevice connectDevice) {
                connectBluetooth(connectDevice);
            }
        });
        btScanner.setScanResultLister(new BleScanListener() {
            @Override
            public void scanSuccess(UbtBluetoothDevice device) {
                EventBus.getDefault().post(new BleScanResultEvent(device));
            }

            @Override
            public void scanFailed(int errorCode) {
                if (errorCode != 0) {
                    EventBus.getDefault().post(new BleScanFailEvent(errorCode));
                } else {
                    EventBus.getDefault().post(new BleScanFinishedEvent());
                }
            }
        });
        btConnector.setBleConnectListener(new BleConnectListener() {
            @Override
            public void connectFailed() {
                final UbtBluetoothDevice device = btConnector.getCurrentDevice();
                if (device != null && !btConnector.isClosed()) {
                    if (device.getRetryCount() <= 3) {
                        //FIXME --logic.peng 检查到这个设备信号强且进入连接过程中,设备突然关机了,手机端重试8次失败,则重新扫描
                        btConnector.connect(device);
                        device.incRetryCount();
                    } else {
                        startScanBluetooth();
                    }
                } else {
                    UbtBluetoothManager.this.device = null;
                }
            }

            @Override
            public void connectSuccess(BluetoothDevice device) {
                if (mOuterConnectListener != null) {
                    mOuterConnectListener.connectSuccess(device);
                }
            }

            @Override
            public void receiverDataFromRobot(String data) {
                if (mOuterConnectListener != null) {
                    mOuterConnectListener.receiverDataFromRobot(data);
                }
            }

            @Override
            public void sendDataFailed(String result) {
                if (mOuterConnectListener != null) {
                    mOuterConnectListener.sendDataFailed(result);
                }
            }

            @Override
            public void sendDataSuccess(String msg) {
                if (mOuterConnectListener != null) {
                    mOuterConnectListener.sendDataSuccess(msg);
                }
            }
        });
    }

    /**
     * 设置蓝牙连接监听
     */
    public void setBleConnectListener(BleConnectListener listener) {
        this.mOuterConnectListener = listener;
    }

    /**
     * 开启蓝牙
     */
    public void openBluetooth(Activity activity) {
        btScanner.enableBluetoothIfDisabled(activity);
    }

    /**
     * 蓝牙是否开启
     */
    public boolean isOpenBluetooth() {
        return btScanner.isBleOpen();
    }

    /**
     * 是否正在扫描
     */
    public boolean isScanning() {
        return btScanner.isScanning();
    }

    /**
     * 设置蓝牙名称前缀
     */
    public void setBleNamePrefix(String bleNamePrefix) {
        btScanner.setBleNamePrefix(bleNamePrefix);
    }

    /**
     * 开始搜索蓝牙
     */
    synchronized public void startScanBluetooth() {
        if (!btScanner.isScanning()) {
            btScanner.scanDevices();
        }
    }

    /**
     * 连接蓝牙
     */
    synchronized public void connectBluetooth(UbtBluetoothDevice device) {
        Log.i(TAG,"isNotInConnecting========" + btConnector.isNotInConnecting());
        if (btConnector.isNotInConnecting()) {
            //btScanner.stopScanDevices();
            this.device = device;
            btConnector.connect(device);
        } else {
            Log.w(TAG, "@@@@@@@@@@@@@@@@@@正在链接蓝牙设别 , state = : " + btConnector.getConnectState());
        }
    }

    public UbtBluetoothDevice getCurrentDevices() {
        return device;
    }

    /**
     * 获取当前连接机器人的序列号
     */
    public String getCurrentDeviceSerial() {
        UbtBluetoothDevice ubtBluetoothDevice = btConnector.getCurrentDevice();
        if (ubtBluetoothDevice != null) {
            return ubtBluetoothDevice.getSn();
        } else if (device != null) {
            return device.getSn();
        } else {
            return "";
        }
    }

    /**
     * 给蓝牙发送消息
     */
    public void sendMessageToBle(String message) {
        btConnector.sendMessage(message);
    }

    /**
     * 关闭蓝牙连接
     */
    public void closeConnectBle() {
        btConnector.closeConnection();
    }

    /**
     * 设置是否自动连接
     */
    public void setAutoConnect(boolean isAutoConnect) {
        btScanner.setAutoConnect(isAutoConnect);
    }

    /**
     * @param sn
     */
    public void setCurrentRobotSn(String sn) {
        btScanner.setCurrentRobotSn(sn);
    }

    public boolean isFromHome() {
        return isFromHome;
    }

    public void setFromHome(boolean fromHome) {
        isFromHome = fromHome;
    }

    public boolean isFromCodeMao() {
        return isFromCodeMao;
    }

    public void setFromCodeMao(boolean fromCodeMao) {
        isFromCodeMao = fromCodeMao;
    }
}

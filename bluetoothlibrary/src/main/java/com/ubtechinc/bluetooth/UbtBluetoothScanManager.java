package com.ubtechinc.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.bluetooth.utils.BleUtil;

import java.util.HashMap;

/**
 * @author：wululin
 * @date：2017/10/18 16:51
 * @modifier：ubt
 * @modify_date：2017/10/18 16:51
 * [A brief description]
 * 蓝牙搜索管理类---处理自动连接逻辑--转换扫描结果
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class UbtBluetoothScanManager {
    private static final String TAG = UbtBluetoothScanManager.class.getSimpleName();
    private static final double AUTO_CONNECT_DISTANCE = 1.0;

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private UbtBluetoothLeScanner mScanner;
    private BleScanListener mBleScanListener;
    private String mBleNamePrefix;
    private String mCurrentRobotSn;
    private volatile boolean mIsAutoConnect = false;
    private AutoConnectListener mAutoConnectListener;

    UbtBluetoothScanManager(Context context) {
        this.mContext = context;
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    void setAutoConnectListener(AutoConnectListener autoConnectListener) {
        this.mAutoConnectListener = autoConnectListener;
    }

    void setCurrentRobotSn(String mCurrentRobotSn) {
        this.mCurrentRobotSn = mCurrentRobotSn;
    }

    boolean isScanning() {
        return mScanner != null && mScanner.isScanning();
    }

    /**
     * 开启蓝牙，如果被关闭
     */
    void enableBluetoothIfDisabled(Activity activity) {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            activity.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    /**
     * 蓝牙是否开启
     */
    boolean isBleOpen() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        BluetoothAdapter bluetoothAdapter = null;
        if (mBluetoothManager != null) {
            bluetoothAdapter = mBluetoothManager.getAdapter();
        }
        // 2.检查设备上是否支持并开启蓝牙
        if (bluetoothAdapter == null) {
            Log.i(TAG, "设置不支持蓝牙");
            return false;
        } else {
            return bluetoothAdapter.isEnabled();
        }
    }

    /**
     * 开始扫描设备
     */
    void scanDevices() {
        if (mScanner == null) {
            mScanner = new UbtBluetoothLeScanner();
        }
        mScanner.scanLeDevice(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice btDevice = result.getDevice();
                String deviceSn = btDevice.getName();
                if (!TextUtils.isEmpty(deviceSn) && !TextUtils.isEmpty(mBleNamePrefix)) {
                    if (deviceSn.startsWith(mBleNamePrefix)) {
                        String[] snSegment = deviceSn.split("_");
                        if (snSegment[1].length() <= 4) {
                            return;
                        }

                        UbtBluetoothDevice connectDevice = new UbtBluetoothDevice();
                        connectDevice.setDevice(btDevice);
                        ScanRecord scanRecord = result.getScanRecord();
                        boolean needEncrypt = false;
                        if (scanRecord != null) {
                            needEncrypt = BleUtil.needEncryption(scanRecord.getBytes());
                        }
                        connectDevice.setDeviceEncrp(needEncrypt);
                        connectDevice.setRssi(result.getRssi());
                        connectDevice.setSn(snSegment[1]);

                        if (mIsAutoConnect) {
                            autoAutoConnect(connectDevice, result.getRssi());
                        } else {
                            mCacheDevices.put(connectDevice.getSn(), connectDevice);
                        }

                        if (mBleScanListener != null) {
                            mBleScanListener.scanSuccess(connectDevice);
                        }
                    }
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                if (mBleScanListener != null) {
                    mBleScanListener.scanFailed(errorCode);
                }
            }
        });
    }

    /**
     * 手动停止搜索蓝牙
     */
    void stopScanDevices() {
        if (mScanner != null && mScanner.isScanning()) {
            mScanner.stopScan();
            mCacheDevices.clear();
        }
    }

    /**
     * 设置扫描到蓝牙名称的前缀（用于过滤扫描到的蓝牙）
     */
    void setBleNamePrefix(String bleNamePrefix) {
        mBleNamePrefix = bleNamePrefix;
    }

    void setScanResultLister(BleScanListener bleNetwortSetListener) {
        this.mBleScanListener = bleNetwortSetListener;
    }

    private HashMap<String, UbtBluetoothDevice> mCacheDevices = new HashMap<>();

    private void autoAutoConnect(UbtBluetoothDevice connectDevice, int rssi) {
        boolean isBandOrSetting = UbtBluetoothManager.getInstance().isChangeWifi();
        float distance = BleUtil.getDistance(rssi);
        String sn = connectDevice.getSn();
        Log.i(TAG, "rssi===="
                + rssi
                + ", 距离======"
                + distance
                + " , connectSn ="
                + sn
                + " , currentBindSn = "
                + mCurrentRobotSn);
        if (isBandOrSetting) {
            if (distance <= AUTO_CONNECT_DISTANCE && sn.equals(mCurrentRobotSn)) {
                stopScanDevices();
                if (mAutoConnectListener != null) {
                    mAutoConnectListener.onAutoConnect(connectDevice);
                }
            }
        } else {
            UbtBluetoothDevice cacheDevice = mCacheDevices.get(sn);
            if (rssi >= -53 && cacheDevice != null && cacheDevice.getRssi() >= -59) {
                stopScanDevices();
                if (mAutoConnectListener != null) {
                    mAutoConnectListener.onAutoConnect(connectDevice);
                }
            } else {
                mCacheDevices.put(sn, connectDevice);
            }
        }
    }

    void setAutoConnect(boolean isAutoConnect) {
        this.mIsAutoConnect = isAutoConnect;
    }

    interface AutoConnectListener {
        void onAutoConnect(UbtBluetoothDevice connectDevice);
    }
}

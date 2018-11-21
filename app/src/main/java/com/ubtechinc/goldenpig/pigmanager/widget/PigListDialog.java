package com.ubtechinc.goldenpig.pigmanager.widget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.event.BleScanResultEvent;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseDialog;
import com.ubtechinc.goldenpig.comm.view.WrapContentLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :八戒音箱列表
 * @time :2018/8/24 20:44
 * @change :
 * @changetime :2018/8/24 20:44
 */
public class PigListDialog extends BaseDialog {
    //    private BluetoothAdapter mBluetoothadapter = null;
    private UbtBluetoothManager mUbtBluetoothManager;
    private BluetoothAdapter.LeScanCallback mLecallback;//扫描回调
    private boolean isScan = false;
    private final long SCANTIME = 1000 * 120;//设置扫描时间

    private ArrayList<UbtBluetoothDevice> mLeList;
    private RecyclerView mPigRycView;
    private PigListAdapter mPigAdapter;
    private View mLoadingView;        ///加载loading

    public PigListDialog(@NonNull Context context) {
        super(context);
        inits();
    }

    public PigListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        inits();
    }

    protected PigListDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        inits();
    }

    private void inits() {
        View root = View.inflate(getContext(), R.layout.dialog_pig_list, null);

        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);
        this.setContentView(root);
        mLeList = new ArrayList<>();
//        setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                scanLeDevice(false);
//                mLecallback = null;
//                setOnDismissListener(null);
//            }
//        });
        mUbtBluetoothManager = UbtBluetoothManager.getInstance();
//        mBluetoothadapter = BluetoothAdapter.getDefaultAdapter();

//        registerLeCallback();
        scanLeDevice(true);
        mLoadingView = findViewById(R.id.ubt_loading);
        mPigRycView = (RecyclerView) findViewById(R.id.ubt_pig_list_ryv);
        mPigRycView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        mPigAdapter = new PigListAdapter(mLeList);
        mPigRycView.setAdapter(mPigAdapter);
    }

    public void onDismiss() {
        scanLeDevice(false);
        mLecallback = null;
    }

    public void setBluetoothItemClickListener(OnPigListItemClickListener listener) {
        mPigAdapter.setItemClickListener(listener);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanResult(BleScanResultEvent event) {

        UbtBluetoothDevice ubtBleDevice = event.device;
        BluetoothDevice device = ubtBleDevice.getDevice();

        Log.d("gold_ble", "ble_name:" + device.getName() + "==addr:" + device.getAddress());
        int rawIndex = isHasDevice(device);
        UbtBluetoothDevice ubtBluetoothDevice = new UbtBluetoothDevice();
        ubtBluetoothDevice.setDevice(device);

        //近场连接
        float distance = getDistance(ubtBleDevice.getRssi());
        String name = device.getName();
        String tag = name.substring(0, name.indexOf("_") + 1) + name.substring(name.length() - 4, name.length());
        Log.e("pigList", tag + "_ble_distance:" + distance);
        if (distance < 2.0) {
            scanLeDevice(false);
            mPigAdapter.getItemClickListener().onClick(rawIndex, ubtBluetoothDevice);
            return;
        }
        if (rawIndex >= 0) {
            mLeList.remove(rawIndex);
            mLeList.add(rawIndex, ubtBluetoothDevice);
            mPigAdapter.updateList(mLeList);
            mPigAdapter.notifyItemChanged(rawIndex);
        } else {
            mLeList.add(ubtBluetoothDevice);
            mPigAdapter.updateList(mLeList);
            mPigAdapter.notifyItemInserted(mLeList.size());
        }
        mPigRycView.setVisibility(View.VISIBLE);
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    private void registerLeCallback() {
        mLecallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                Log.d("gold_ble", "ble_name:" + device.getName() + "==addr:" + device.getAddress());
                if (!TextUtils.isEmpty(device.getName()) && device.getName().startsWith(Constants.ROBOT_TAG)) {
                    int rawIndex = isHasDevice(device);
                    UbtBluetoothDevice ubtBluetoothDevice = new UbtBluetoothDevice();
                    ubtBluetoothDevice.setDevice(device);

                    //近场连接
                    float distance = getDistance(rssi);
                    String name = device.getName();
                    String tag = name.substring(0, name.indexOf("_") + 1) + name.substring(name.length() - 4, name.length());
                    Log.e("pigList", tag + "_ble_distance:" + distance);
                    if (distance < 2.0) {
                        scanLeDevice(false);
                        mPigAdapter.getItemClickListener().onClick(rawIndex, ubtBluetoothDevice);
                        return;
                    }
                    if (rawIndex >= 0) {
                        mLeList.remove(rawIndex);
                        mLeList.add(rawIndex, ubtBluetoothDevice);
                        mPigAdapter.updateList(mLeList);
                        mPigAdapter.notifyItemChanged(rawIndex);
                    } else {
                        mLeList.add(ubtBluetoothDevice);
                        mPigAdapter.updateList(mLeList);
                        mPigAdapter.notifyItemInserted(mLeList.size());
                    }
                    mPigRycView.setVisibility(View.VISIBLE);
                    if (mLoadingView != null) {
                        mLoadingView.setVisibility(View.GONE);
                    }

                }

            }
        };
        scanLeDevice(true);
    }

    /**
     * 根据rssi信号转换成距离
     * d=10^((ABS(RSSI)-A)/(10*n))、A 代表在距离一米时的信号强度(45 ~ 49), n 代表环境对信号的衰减系数(3.25 ~ 4.5)
     *
     * @param rssi
     * @return
     */
    private float getDistance(int rssi) {
        float A_Value = 49;
        float n_Value = 3.5f;
        int iRssi = Math.abs(rssi);
        float power = (iRssi - A_Value) / (10 * n_Value);
        return (float) Math.pow(10, power);
    }

    private int isHasDevice(BluetoothDevice device) {
        if (mLeList == null) {
            return -1;
        }
        final int devLen = mLeList.size();
        for (int index = 0; index < devLen; index++) {
            if (/*mLeList.get(index).getDevice().getAddress().equals(device.getAddress())
                    || */mLeList.get(index).getDevice().getName().equals(device.getName())) {
                return index;
            }
        }
        return -1;
    }

    /***
     * 开启关闭蓝牙扫描
     * @param enable
     */
    private void scanLeDevice(boolean enable) {
        if (enable) {
            EventBus.getDefault().register(PigListDialog.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScan = false;
                    mUbtBluetoothManager.stopScanBluetooth();
                    EventBus.getDefault().unregister(PigListDialog.this);
//                    mBluetoothadapter.stopLeScan(mLecallback);

                }
            }, SCANTIME);
            isScan = true;

            mUbtBluetoothManager.startScanBluetooth();
//            mBluetoothadapter.startLeScan(new UUID[]{UUID.fromString("10057e66-28bd-4c3e-a030-08b4982739bc")}, mLecallback);

        } else {
            isScan = false;

            mUbtBluetoothManager.stopScanBluetooth();
            EventBus.getDefault().unregister(PigListDialog.this);

//            mBluetoothadapter.stopLeScan(mLecallback);
        }
    }

    public int getBleCount() {
        if (mLeList != null) {
            return mLeList.size();
        } else {
            return 0;
        }
    }

    public ArrayList<UbtBluetoothDevice> getLeList() {
        return mLeList;
    }
}

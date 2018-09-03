package com.ubtechinc.goldenpig.pigmanager.widget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseDialog;

import java.util.ArrayList;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :小猪音响列表
 *@time          :2018/8/24 20:44
 *@change        :
 *@changetime    :2018/8/24 20:44
*/
public class PigListDialog extends BaseDialog {
    private BluetoothAdapter mBluetoothadapter=null;
    private BluetoothAdapter.LeScanCallback mLecallback;//扫描回调
    private boolean isScan=false;
    private final long SCANTIME = 1000*120;//设置扫描时间

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


    private void inits(){
        View root = View.inflate(getContext(), R.layout.dialog_pig_list, null);

        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);
        this.setContentView(root);
        mLeList=new ArrayList<>();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                scanLeDevice(false);
                mLecallback=null;
                setOnDismissListener(null);
            }
        });
        mBluetoothadapter=BluetoothAdapter.getDefaultAdapter();

        registerLeCallback();
        mLoadingView=findViewById(R.id.ubt_loading);
        mPigRycView=(RecyclerView)findViewById(R.id.ubt_pig_list_ryv);
        mPigRycView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPigAdapter=new PigListAdapter(mLeList);
        mPigRycView.setAdapter(mPigAdapter);
    }
    public void setBluetoothItemClickListener(OnPigListItemClickListener listener){
        mPigAdapter.setItemClickListener(listener);
    }
    private void registerLeCallback(){
        mLecallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                final boolean isHasDevice=isHasDevice(device);

                if (!isHasDevice && !TextUtils.isEmpty(device.getName())&& device.getName().startsWith("Pig_")){
                    UbtBluetoothDevice ubtBluetoothDevice=new UbtBluetoothDevice();
                    ubtBluetoothDevice.setDevice(device);
                    mLeList.add(ubtBluetoothDevice);
                    mPigAdapter.notifyItemInserted(mLeList.size());
                    mPigRycView.setVisibility(View.VISIBLE);
                    if (mLoadingView!=null){
                        mLoadingView.setVisibility(View.GONE);
                    }

                }

            }
        };
        scanLeDevice(true);
    }
    private boolean isHasDevice(BluetoothDevice device){
        if (mLeList==null){
            return false;
        }
        final int devLen=mLeList.size();
        for (int index = 0; index < devLen; index++) {
            if (mLeList.get(index).getDevice().getAddress().equals(device.getAddress())){
                return true;
            }
        }
        return  false;
    }
    /***
     * 开启关闭蓝牙扫描
     * @param enable
     */
    private void scanLeDevice(boolean enable){
        if (enable){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScan = false;
                    mBluetoothadapter.stopLeScan(mLecallback);

                }
            }, SCANTIME);
            isScan = true;
            mBluetoothadapter.startLeScan(mLecallback);

        }else {
            isScan = false;
            mBluetoothadapter.stopLeScan(mLecallback);
        }
    }

}

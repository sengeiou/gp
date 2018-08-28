package com.ubtechinc.goldenpig.pigmanager.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import com.ubtechinc.commlib.utils.ContextUtils;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :蓝牙管理器
 *@time          :2018/8/24 10:10
 *@change        :
 *@changetime    :2018/8/24 10:10
*/
public class BlueToothManager {
    /**蓝牙状态**/
    public final static byte BLUETOOTH_STATE_NONE=0;
    public final static byte BLUETOOTH_STATE_CLOSED=1;
    public final static byte BLUETOOTH_STATE_OPEN=2;

    public static byte getBluetoothState(){
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        if (adapter==null){
            return BLUETOOTH_STATE_NONE;
        }else if (adapter.isEnabled()){
            return BLUETOOTH_STATE_OPEN;
        }else {
            return BLUETOOTH_STATE_CLOSED;
        }
    }
    /***打开蓝牙设置界面**/
    public static void openBlueToothSetting(Activity context,int requestCode){

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            if (ContextUtils.isContextExisted(context)) {
                context.startActivityForResult(intent,requestCode);
            }
        } catch(ActivityNotFoundException ex){
            ex.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
    /***判断蓝牙扫描权限****/
    public static boolean checkLeScanPermission(Context context){
        if (ContextUtils.isContextExisted(context)){
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }
}

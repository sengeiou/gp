package com.ubtechinc.goldenpig.pigmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.commlib.utils.WifiUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;

import java.util.List;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :设置小猪音响WIFi及密码
 *@time          :2018/8/27 14:32
 *@change        :
 *@changetime    :2018/8/27 14:32
*/
public class SetPingNetWorkActivity extends BaseToolBarActivity implements View.OnClickListener{
    private Button mSendWifiInfoBtn;
    private EditText mWifiNamEdt,mWifiPwdEdt;

    private boolean mHasPermission;
    private String mCap;

    @Override
    protected int getConentView() {
        return R.layout.activity_set_pig_network;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermission();
        }
        mSendWifiInfoBtn=(Button)findViewById(R.id.ubt_btn_connect_wifi);
        mWifiNamEdt=(EditText)findViewById(R.id.ubt_edt_wifi_name);
        mWifiPwdEdt=(EditText)findViewById(R.id.ubt_edt_wifi_password);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String wifiName=mWifiNamEdt.getText().toString();
        String wifiPsd=mWifiPwdEdt.getText().toString();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_btn_connect_wifi:

                break;
            default:
                break;
        }
    } //权限请求码
    private static final int PERMISSION_REQUEST_CODE = 0;
    //两个危险权限需要动态申请
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };



    /**
     * 检查是否已经授予权限
     * @return
     */
    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        mHasPermission = checkPermission();
        if (!mHasPermission) {
            ActivityCompat.requestPermissions(this,
                    NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;   //判断用户是否同意获取权限
                    break;
                }
            }

            //如果同意权限
            if (hasAllPermission) {
                mHasPermission = true;
                if(WifiUtils.isOpenWifi(SetPingNetWorkActivity.this) && mHasPermission){  //如果wifi开关是开 并且 已经获取权限
                    scanWifiInfo();
                }else{
                    //Toast.makeText(MainActivity.this,"WIFI处于关闭状态或权限获取失败",Toast.LENGTH_SHORT).show();
                }

            } else {  //用户不同意权限
                mHasPermission = false;
                //Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanWifiInfo() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.startScan(); //启动扫描
        StringBuilder scanBuilder = new StringBuilder();
        List<ScanResult> scanResults = wifiManager.getScanResults();//搜索到的设备列表
        for (ScanResult scanResult : scanResults) {

            scanBuilder.append("\n设备名：" + scanResult.SSID+"\n 加密格式："+scanResult.capabilities
                    + "\n信号强度：" + wifiManager.calculateSignalLevel(scanResult.level, 1001) + "\n");
        }
        UbtLogger.e("scanWifiInfo", scanBuilder.toString());
    }
}

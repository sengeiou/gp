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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.commlib.utils.WifiUtils;
import com.ubtechinc.commlib.view.UbtPasswordEditText;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.view.UbtWifiListEditText;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

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
    private UbtWifiListEditText mWifiNamEdt;
    private UbtPasswordEditText mWifiPwdEdt;

    private boolean mHasPermission;
    private String mCap;
    private ICommandProduce commandProduce;
    @Override
    protected int getConentView() {
        return R.layout.activity_set_pig_network;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermission();
        }
        setToolBarTitle(getString(R.string.ubt_set_pig_net));
        commandProduce = new JsonCommandProduce();
        setTitleBack(true);
        mSendWifiInfoBtn=(Button)findViewById(R.id.ubt_btn_connect_wifi);
        mSendWifiInfoBtn.setOnClickListener(this);
        mWifiNamEdt=(UbtWifiListEditText)findViewById(R.id.ubt_edt_wifi_name);
        mWifiPwdEdt=(UbtPasswordEditText)findViewById(R.id.ubt_edt_wifi_password);
        mTvSkip=findViewById(R.id.ubt_tv_set_net_skip);
        mTvSkip.setVisibility(View.VISIBLE);
        mTvSkip.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_btn_connect_wifi:
                showNotify("小猪音响");
                checkWifiInfo();
                break;
            case R.id.ubt_tv_set_net_skip:
                ActivityRoute.toAnotherActivity(this, MainActivity.class,true);
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

                }else{
                    //Toast.makeText(MainActivity.this,"WIFI处于关闭状态或权限获取失败",Toast.LENGTH_SHORT).show();
                }

            } else {  //用户不同意权限
                mHasPermission = false;
                //Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**向本体发送wifi信息*/
    private void checkWifiInfo(){
        final String pwd=mWifiPwdEdt.getText().toString();
        if (TextUtils.isEmpty(pwd)){
            UBTBaseDialog dialog=new UBTBaseDialog(this);
            dialog.setRightButtonTxt(getString(R.string.ubt_enter));
            dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
            dialog.setTips(getString(R.string.ubt_wifi_no_pwd_tips));
            dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    sendWifiInfo();
                }
            });
            dialog.show();
        }else {
            sendWifiInfo();
        }

    }
    private void sendWifiInfo(){
        final String wifiName=mWifiNamEdt.getText();
        final String wifiPwd=mWifiPwdEdt.getText().toString();
        showLoadingDialog();
        String message = commandProduce.getWifiPasswdInfo(wifiName, wifiName, wifiPwd);
        UbtBluetoothManager.getInstance().sendMessageToBle(message);
    }
}

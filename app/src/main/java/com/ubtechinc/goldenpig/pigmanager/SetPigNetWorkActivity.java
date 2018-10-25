package com.ubtechinc.goldenpig.pigmanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.commlib.utils.WifiUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.view.UbtPasswordEditText;
import com.ubtechinc.goldenpig.comm.view.UbtWifiListEditText;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.bean.BundingListenerAbster;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.nets.utils.WifiControl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :设置小猪音箱WIFi及密码
 * @time :2018/8/27 14:32
 * @change :
 * @changetime :2018/8/27 14:32
 */
public class SetPigNetWorkActivity extends BaseToolBarActivity implements View.OnClickListener {
    private Button mSendWifiInfoBtn;
    private UbtWifiListEditText mWifiNamEdt;
    private UbtPasswordEditText mWifiPwdEdt;

    private boolean mHasPermission;
    private String mCap;
    private ICommandProduce commandProduce;
    private BungdingManager bungdingManager;

    private static final int TIME_OUT = 30;

    @Override
    protected int getConentView() {
        return R.layout.activity_set_pig_network;
    }

    @Override
    protected boolean isForbiddenSnapShot() {
        return true;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
        setToolBarTitle(getString(R.string.ubt_set_pig_net));
        commandProduce = new JsonCommandProduce();
        setTitleBack(true);
        mSendWifiInfoBtn = (Button) findViewById(R.id.ubt_btn_connect_wifi);
        mSendWifiInfoBtn.setOnClickListener(this);
        mWifiNamEdt = (UbtWifiListEditText) findViewById(R.id.ubt_edt_wifi_name);
        String phoneSsid = WifiControl.get(SetPigNetWorkActivity.this).getSSID();
        if (!TextUtils.isEmpty(phoneSsid)) {
            mWifiNamEdt.setText(phoneSsid);
        }
        mWifiPwdEdt = (UbtPasswordEditText) findViewById(R.id.ubt_edt_wifi_password);
        mTvSkip = findViewById(R.id.ubt_tv_set_net_skip);
        mTvSkip.setVisibility(View.VISIBLE);
        mTvSkip.setOnClickListener(this);
        bungdingManager = new BungdingManager(this);
        bungdingManager.setBangdingListener(mBandingListenerAbster);
        checkPigWifi();

        Event<Integer> event = new Event<>(EventBusUtil.CONTACT_PIC_SUCCESS);
        EventBusUtil.sendEvent(event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_connect_wifi:

                checkWifiInfo();
                break;
            case R.id.ubt_tv_set_net_skip:
                ActivityRoute.toAnotherActivity(this, MainActivity.class, true);
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
     *
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
                if (WifiUtils.isOpenWifi(SetPigNetWorkActivity.this) && mHasPermission) {  //如果wifi开关是开 并且 已经获取权限

                } else {
                    Toast.makeText(SetPigNetWorkActivity.this, "WIFI处于关闭状态或权限获取失败", Toast.LENGTH_SHORT).show();
                }

            } else {  //用户不同意权限
                mHasPermission = false;
                //Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 向本体发送wifi信息
     */
    private void checkWifiInfo() {
        String wifiName = mWifiNamEdt.getText();
        if (TextUtils.isEmpty(wifiName)) {
            ToastUtils.showShortToast(this, "请填写WIFI SSID");
            return;
        }
        final String pwd = mWifiPwdEdt.getPwd();
        if (TextUtils.isEmpty(pwd)) {
            UBTBaseDialog dialog = new UBTBaseDialog(this);
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
        } else {
            sendWifiInfo();
        }

    }

    private void checkPigWifi() {
        String message = commandProduce.getPigNetWorkState();
        UbtBluetoothManager.getInstance().sendMessageToBle(message);
    }

    private void sendWifiInfo() {
        mSendWifiInfoBtn.setText(R.string.ubt_connecting);
        mSendWifiInfoBtn.setAlpha(0.5f);
        final String wifiName = mWifiNamEdt.getText();
        final String wifiPwd = mWifiPwdEdt.getPwd();
        final String wifiCtype = mWifiNamEdt.getcType();
        if (TextUtils.isEmpty(wifiCtype)) {
            ToastUtils.showShortToast(this, "正在获取Wi-Fi加密方式，请稍后尝试");
            return;
        }
        LoadingDialog.getInstance(this).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mSendWifiInfoBtn.setText(R.string.ubt_connect);
                mSendWifiInfoBtn.setAlpha(1.0f);
            }
        });
        String message = commandProduce.getWifiPasswdInfo(wifiCtype, wifiName, wifiPwd);
        UbtBluetoothManager.getInstance().sendMessageToBle(message);
        LoadingDialog.getInstance(SetPigNetWorkActivity.this).setTimeout(TIME_OUT).setShowToast(true).show();
    }

    BundingListenerAbster mBandingListenerAbster = new BundingListenerAbster() {
        @Override
        public void onFaild(int errorCode) {
            super.onFaild(errorCode);
            if (errorCode != 2041) {
                dismissLoadDialog();
                mSendWifiInfoBtn.setText(R.string.ubt_connect);
                mSendWifiInfoBtn.setAlpha(1.0f);
                showNotify("连接失败");
                AuthLive.getInstance().getCurrentPig().setOnlineState(PigInfo.ROBOT_STATE_OFFLINE);
                UBTPGApplication.pig_net_status = false;
            }
        }

        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            super.onSuccess(response);
            dismissLoadDialog();
        }

        @Override
        public void connectSuccess() {
            super.connectSuccess();
            dismissLoadDialog();
        }

        @Override
        public void connWifiSuccess() {
            super.connWifiSuccess();
            UBTPGApplication.pig_net_status = true;
            mSendWifiInfoBtn.setText(R.string.ubt_connect);
            mSendWifiInfoBtn.setAlpha(1.0f);
            dismissLoadDialog();
            hideNotify();
            findViewById(R.id.ubt_layout_setnet).setVisibility(View.GONE);
            findViewById(R.id.ubt_img_success).setVisibility(View.VISIBLE);
            findViewById(R.id.ubt_tv_set_net_success).setVisibility(View.VISIBLE);
            mTvSkip.setEnabled(false);
            mTvSkip.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityRoute.toAnotherActivity(SetPigNetWorkActivity.this, MainActivity.class, true);
                }
            }, 2000);

        }

        @Override
        public void onPigConnected(String wifiState) {
            super.onPigConnected(wifiState);
//            dismissLoadDialog();
            //{"co":120,"wifi_info":"{\"l\":-29,\"s\":\"\\\"alpha-bigbox-5G\\\"\"}"}
            UbtLogger.i("onPigConnected", wifiState);
            String wifiName = "";
            if (!TextUtils.isEmpty(wifiState)) {
                try {

                    wifiState = wifiState.replace("\\", "");
                    wifiState = wifiState.replace("\"{", "{");
                    wifiState = wifiState.replace("}\"", "}");
                    wifiState = wifiState.replace("\"\"", "\"");
                    JSONObject jsonObject = new JSONObject(wifiState);
                    if (jsonObject.has("wifi_info")) {
                        jsonObject = jsonObject.getJSONObject("wifi_info");
                    }
                    if (jsonObject.has("s")) {
                        wifiName = jsonObject.getString("s");
                        if (!TextUtils.isEmpty(wifiName)) {
                            showNotify("音箱已连接“" + wifiName + "”无线网络");
                        }
                    } else {
                        ///连接到有道网络是提示
                        showNotify("音箱已连接移动网络");
                    }
                    AuthLive.getInstance().getCurrentPig().setOnlineState(PigInfo.ROBOT_STATE_ONLINE);
                    UBTPGApplication.pig_net_status = true;
                    /*if (jsonObject.getInt("co") == 120) {
                        JSONObject subJson = jsonObject.getJSONObject("wifi_info");
                        if (jsonObject.has("wifi_info")) {
                            final String wifiName = subJson.getString("s");
                            if (TextUtils.isEmpty(wifiName)) {
                                /// 解析小猪网络json反馈
                                showNotify("音箱已连接“" + wifiName + "”无线网络");
                            }
                        }
                    } else {
                        ///连接到有道网络是提示
                        showNotify("音箱已连接移动网络");
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //TODO 刷新wifi输入框默认ssid
            updateDefaultSsid(wifiName);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        updateDefaultSsid("");
    }

    private void updateDefaultSsid(String wifiName) {
//        String defaultSsid;
//        if (TextUtils.isEmpty(wifiName)) {
//            defaultSsid = WifiControl.get(SetPigNetWorkActivity.this).getSSID();
//        } else {
//            defaultSsid = wifiName;
//        }
        String defaultSsid = WifiControl.get(SetPigNetWorkActivity.this).getConnectInfo().getSSID();
        if (!TextUtils.isEmpty(defaultSsid)) {
            mWifiNamEdt.setText(defaultSsid);
        }
    }
}

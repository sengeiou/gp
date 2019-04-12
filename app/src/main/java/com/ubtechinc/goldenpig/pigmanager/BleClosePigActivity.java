package com.ubtechinc.goldenpig.pigmanager;


import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.bluetooth.event.BleScanResultEvent;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.bean.BundingListenerAbster;
import com.ubtechinc.goldenpig.pigmanager.widget.PigListDialog;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import pl.droidsonroids.gif.GifImageView;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :   靠近小猪--近场绑定
 * @time :2018/12/19 11:31
 * @change :
 * @changetime :2018/12/19 11:31
 */
public class BleClosePigActivity extends BaseToolBarActivity implements View.OnClickListener {

    private GifImageView ivBoxStartup;

    private View dtvManualSearchPig;

    private PigListDialog pigListDialog;

    private UbtBluetoothDevice mBluetoothDevice;

    private BungdingManager mBangdingManager;

    private Disposable scanDisposable;

//    private Disposable connDisposable;

    private UBTBaseDialog mGpsTipDialog;

    private UBTBaseDialog mNoDeviceDialog;

    private UBTBaseDialog mErrorDialog;

    private UBTSubTitleDialog mNoTelepathicDialog;

    private UBTSubTitleDialog mUnBindTipDialog;

    private static final int MSG_WATH_DISCONNECT_SUCCESS = 0x001;

    public static final int CONNECT_TIMEOUT = 30;

    private static final int MSG_CHECK_WIFI = 0x002;

    private String mPigWifiName;

    private int mPigMobileType = -1;

    private UbtBluetoothManager mUbtBluetoothManager;

    private boolean isAutoScan;

    private boolean isManualScan;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WATH_DISCONNECT_SUCCESS:
                    if (isAutoScan) {
                        LoadingDialog.getInstance(BleClosePigActivity.this).setTimeout(CONNECT_TIMEOUT)
                                .setShowToast(true).show();
                    }
                    UbtBluetoothManager.getInstance().connectBluetooth(mBluetoothDevice);
                    startScanBle(false);
                    break;
                case MSG_CHECK_WIFI:
                    toSetWifi();
                    break;

                default:
                    break;
            }
        }
    };

    private void toSetWifi() {
        dismissLoadDialog();

        if (!TextUtils.isEmpty(mPigWifiName)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("PigWifiName", mPigWifiName);
            ActivityRoute.toAnotherActivity(BleClosePigActivity.this, PigWifiInfoActivity.class, map, false);
        } else if (mPigMobileType != -1 && mPigMobileType != 0) {
            ActivityRoute.toAnotherActivity(BleClosePigActivity.this, PigWifiInfoActivity.class, false);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("comingSource", "closepig");
            ActivityRoute.toAnotherActivity(BleClosePigActivity.this, SetPigNetWorkActivity.class, map, false);
        }
    }

    @Override
    protected int getConentView() {
        return R.layout.activity_ble_close_pig;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle(getString(R.string.ubt_pig_bind));
        setTitleBack(true);
        initViews();
        mBangdingManager = new BungdingManager(this);
    }

    private void initViews() {
        ivBoxStartup = findViewById(R.id.iv_box_startup);
        ivBoxStartup.post(() -> ivBoxStartup.setImageResource(R.drawable.close_pig));

        dtvManualSearchPig = findViewById(R.id.dtv_manual_search_pig);
        dtvManualSearchPig.setOnClickListener(this);
        mUbtBluetoothManager = UbtBluetoothManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UbtBluetoothManager.isBleEnabled()) {
            isAutoScan = true;
            isManualScan = false;
            startScanBle(true);
        }
    }

    /**
     * 开始扫描ble，近场绑定
     *
     * @param enable
     */
    private void startScanBle(boolean enable) {
        if (enable) {
            UbtBluetoothManager.getInstance().closeConnectBle();
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            if (scanDisposable != null) {
                scanDisposable.dispose();
            }
            mUbtBluetoothManager.startScanBluetooth();
            scanDisposable = Observable.timer(15, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        //TODO 获取不到蓝牙设备后响应
                        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (isAutoScan) {
                            if (locManager == null || !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                showGpsTipDialog();
                            } else {
                                showNoTelepathicDialog();
                            }
                        } else if (isManualScan) {
                            if (pigListDialog == null || pigListDialog.getLeList() == null || pigListDialog.getLeList().isEmpty()) {
                                if (locManager == null || !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    showGpsTipDialog();
                                } else {
                                    LoadingDialog.dissMiss();
                                    showNoManualScanDialog();
                                }
                            }
                        }
                    });
        } else {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(BleClosePigActivity.this);
            }
            isAutoScan = false;
            isManualScan = false;
            mUbtBluetoothManager.stopScanBluetooth();
            if (scanDisposable != null) {
                scanDisposable.dispose();
            }
        }
    }

    /**
     * 判断位置信息是否开启
     * @param context
     * @return
     */
    private static boolean isLocationOpen(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider || isNetWorkProvider;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingDialog.getInstance(BleClosePigActivity.this).dismiss();
        startScanBle(false);
        if (scanDisposable != null) {
            scanDisposable.dispose();
            scanDisposable = null;
        }
//        if (connDisposable != null) {
//            connDisposable.dispose();
//            connDisposable = null;
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanResult(BleScanResultEvent event) {
        UbtBluetoothDevice ubtBleDevice = event.device;
        BluetoothDevice device = ubtBleDevice.getDevice();
        Log.d("gold_ble", "ble_name:" + device.getName()
                + "==addr:" + device.getAddress() + "isAutoScan:" + isAutoScan + "isManualScan:" + isManualScan);
        if (isAutoScan) {
            //TODO 自动近场
            float distance = getDistance(ubtBleDevice.getRssi());
            Log.d("gold_ble", "distance:" + distance);
            if (distance < 2.0) {
                connectBleDevice(ubtBleDevice);
            }
        } else if (isManualScan) {
            //TODO 手动
            if (pigListDialog != null) {
                LoadingDialog.getInstance(BleClosePigActivity.this).dismiss();
                pigListDialog.updateData(ubtBleDevice);
                pigListDialog.show();
            }
        }
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

    BundingListenerAbster mBandingListenerAbster = new BundingListenerAbster() {
        @Override
        public void onFaild(int errorCode, String message) {
            super.onFaild(errorCode, message);

            switch (errorCode) {
                case 2041:
                    if (pigListDialog != null && pigListDialog.isShowing()) {
                        message = getResources().getString(R.string.ubt_one_user_one_pig);
//                        ToastUtils.showShortToast(BleClosePigActivity.this, R.string.ubt_one_user_one_pig);
                    }
                    break;
                case 2040:
//                    ToastUtils.showShortToast(BleClosePigActivity.this, message);
                    break;
                default:
                    message = Constants.getErrorMsg(errorCode);
//                    ToastUtils.showShortToast(BleClosePigActivity.this, Constants.getErrorMsg(errorCode));
                    break;
            }
            if (pigListDialog != null) {
                pigListDialog.dismiss();
            }
            dismissLoadDialog();
            showErrorDialog(message);
        }

        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            super.onSuccess(response);

        }

        @Override
        public void connectSuccess() {
            super.connectSuccess();

        }

        @Override
        public void connectFailed() {
            super.connectFailed();
            dismissLoadDialog();
            if (pigListDialog != null) {
                pigListDialog.dismiss();
            }
            showErrorDialog("连接失败");
//            ToastUtils.showShortToast(BleClosePigActivity.this, R.string.failed_retry);
        }

        @Override
        public void connWifiSuccess() {
            super.connWifiSuccess();
        }

        @Override
        public void onMaster() {
            super.onMaster();
            onBindCallback();
        }

        @Override
        public void bindByOthers(CheckBindRobotModule.User user) {
            super.bindByOthers(user);
            onBindCallback();
        }

        @Override
        public void onUnBind() {
            super.onUnBind();
            if (pigListDialog != null) {
                pigListDialog.dismiss();
            }
            showErrorDialog("绑定失败");
//            ToastUtils.showShortToast(BleClosePigActivity.this, "用户绑定失败");
        }

        @Override
        public void onStopBind(boolean isConflict) {
            if (isConflict) {
                onMaster();
            } else {
                showOnBindTipDialog();
            }
        }

        @Override
        public void onPigConnected(String wifiState) {
            UbtLogger.i("onPigConnected", wifiState);
            if (!TextUtils.isEmpty(wifiState)) {
                try {
                    wifiState = wifiState.replace("\\", "");
                    wifiState = wifiState.replace("\"{", "{");
                    wifiState = wifiState.replace("}\"", "}");
                    wifiState = wifiState.replace("\"\"", "\"");
                    JSONObject wifiStateJson = new JSONObject(wifiState);

                    JSONObject wifi_info = wifiStateJson.optJSONObject("wifi_info");
                    if (wifi_info != null) {
                        mPigWifiName = wifi_info.optString("s");
                    }

                    JSONObject mobile_info = wifiStateJson.optJSONObject("mobile_info");
                    if (mobile_info != null) {
                        mPigMobileType = mobile_info.optInt("m");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mHandler.removeMessages(MSG_CHECK_WIFI);
            toSetWifi();
        }
    };

    /**
     * 错误弹框
     * @param message
     */
    private void showErrorDialog(String message) {
        if (mErrorDialog == null) {
            mErrorDialog = new UBTBaseDialog(this);
            mErrorDialog.setTips(message);
            mErrorDialog.setLeftBtnShow(false);
            mErrorDialog.setRightButtonTxt("我知道了");
            mErrorDialog.setRightBtnColor(ContextCompat.getColor(this, R.color.ubt_tab_btn_txt_checked_color));
            mErrorDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    isAutoScan = true;
                    isManualScan = false;
                    startScanBle(true);
                }
            });
        }
        if (!isDestroyed() && !isFinishing() && !mErrorDialog.isShowing()) {
            mErrorDialog.show();
        }
    }

    private void onBindCallback() {
        if (pigListDialog != null) {
            pigListDialog.dismiss();
        }
        UbtToastUtils.showCustomToast(this, getString(R.string.ubt_bind_success));
        checkPigWifi();
    }

    private void checkPigWifi() {
        ICommandProduce commandProduce = new JsonCommandProduce();
        String message = commandProduce.getPigNetWorkState();
        UbtBluetoothManager.getInstance().sendMessageToBle(message);
        mHandler.sendEmptyMessageDelayed(MSG_CHECK_WIFI, 5000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dtv_manual_search_pig:
                if (UbtBluetoothManager.isBleEnabled()) {
                    startSearchPig();
                }
                break;
            default:
                break;
        }
    }

    private void startSearchPig() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            AndPermission.with(this)
                    .requestCode(0x1102)
                    .permission(Permission.LOCATION)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            initPigListDialog();
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            showPermissionDialog(Permission.LOCATION);
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();
        } else {
            initPigListDialog();
        }
    }

    private void initPigListDialog() {
        UbtBluetoothManager.getInstance().closeConnectBle();
        LoadingDialog.getInstance(BleClosePigActivity.this).setTimeout(CONNECT_TIMEOUT)
                .setShowToast(true).show();
        pigListDialog = new PigListDialog(this);
        pigListDialog.setBluetoothItemClickListener((pos, device) -> connectBleDevice(device));
        pigListDialog.setOnDismissListener(dialog -> {
            isAutoScan = true;
            isManualScan = false;
        });
        isAutoScan = false;
        isManualScan = true;
        startScanBle(true);
    }

    private void showGpsTipDialog() {
        if (mGpsTipDialog == null) {
            mGpsTipDialog = new UBTBaseDialog(this);
            mGpsTipDialog.setTips("请在手机“设置”中确认GPS定位服务已开启");
            mGpsTipDialog.setLeftBtnShow(false);
            mGpsTipDialog.setRightButtonTxt("我知道了");
            mGpsTipDialog.setRightBtnColor(ContextCompat.getColor(this, R.color.ubt_tab_btn_txt_checked_color));
            mGpsTipDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
//                    openGPS(BleClosePigActivity.this);
                    isAutoScan = true;
                    isManualScan = false;
                    startScanBle(true);
                }

            });
        }
        if (!isDestroyed() && !isFinishing()) {
            mGpsTipDialog.show();
        }
    }

    private void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void connectBleDevice(final UbtBluetoothDevice device) {
        if (device != null) {
            mBluetoothDevice = device;
            mBangdingManager.setBangdingListener(mBandingListenerAbster);
            new Thread(() -> mHandler.sendEmptyMessage(MSG_WATH_DISCONNECT_SUCCESS)).start();
//            connDisposable = Observable.timer(15, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(aLong -> {
//                        //TODO 手动连接15秒超时处理
//                        if (isManualScan) {
//                            if (pigListDialog != null) {
//                                pigListDialog.dismiss();
//                                showErrorDialog("连接失败");
//                            }
//                        }
//                    });
        }
    }

    /**
     * 显示自动搜索无响应dialog
     */
    private void showNoTelepathicDialog() {
        if (mNoTelepathicDialog == null) {
            mNoTelepathicDialog = new UBTSubTitleDialog(this);
            mNoTelepathicDialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
            mNoTelepathicDialog.setTips(getString(R.string.ubt_notelepathic_title));
            mNoTelepathicDialog.setOnlyOneButton();
            mNoTelepathicDialog.setRightButtonTxt(getString(R.string.i_know_text));
            mNoTelepathicDialog.setSubTips(getString(R.string.ubt_notelepathic_tip));
            mNoTelepathicDialog.setSubTipColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tips_txt_color, null));
            mNoTelepathicDialog.setSubTipGravity(Gravity.LEFT);
            mNoTelepathicDialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    isAutoScan = true;
                    isManualScan = false;
                    startScanBle(true);
                }
            });
        }
        if (!isDestroyed() && !isFinishing() && !mNoTelepathicDialog.isShowing()) {
            startScanBle(false);
            mNoTelepathicDialog.show();
        }
    }


    /**
     * 显示解绑对话框
     */
    private void showNoManualScanDialog() {
        if (mNoDeviceDialog == null) {
            mNoDeviceDialog = new UBTBaseDialog(this);
            mNoDeviceDialog.setTips("没找到八戒");
            mNoDeviceDialog.setLeftButtonTxt("关闭");
            mNoDeviceDialog.setRightButtonTxt("重新搜索");
            mNoDeviceDialog.setRightBtnColor(ContextCompat.getColor(this, R.color.ubt_tab_btn_txt_checked_color));
            mNoDeviceDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

                @Override
                public void onLeftButtonClick(View view) {
                    isAutoScan = true;
                    isManualScan = false;
                    startScanBle(true);
                }

                @Override
                public void onRightButtonClick(View view) {
                    startSearchPig();
                }

            });
        }
        if (!isDestroyed() && !isFinishing() && !mNoDeviceDialog.isShowing()) {
            startScanBle(false);
            mNoDeviceDialog.show();
        }
    }

    /**
     * 显示解绑对话框
     */
    private void showOnBindTipDialog() {
        dismissLoadDialog();
        if (pigListDialog != null) {
            pigListDialog.dismiss();
        }
        if (mUnBindTipDialog == null) {
            mUnBindTipDialog = new UBTSubTitleDialog(this);
            mUnBindTipDialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
            mUnBindTipDialog.setTips(getString(R.string.unbind_pig_dialog_tip));
            mUnBindTipDialog.setOnlyOneButton();
            mUnBindTipDialog.setRightButtonTxt(getString(R.string.i_know_text));
            mUnBindTipDialog.setSubTips(getString(R.string.unbind_pig_dialog_sub_tip));
            mUnBindTipDialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
                @Override
                public void onLeftButtonClick(View view) {

                }

                @Override
                public void onRightButtonClick(View view) {
                    isAutoScan = true;
                    isManualScan = false;
                    startScanBle(true);
                }
            });
        }
        if (!mUnBindTipDialog.isShowing()) {
            mUnBindTipDialog.show();
        }
    }

}

package com.ubtechinc.goldenpig.pigmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.DrawableTextView;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.pigmanager.bluetooth.BlueToothManager;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;
/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :蓝牙配网权限校验页
 *@time          :2018/12/20 13:49
 *@change        :
 *@changetime    :2018/12/20 13:49
*/
public class BleConfigReadyActivity extends BaseToolBarActivity implements View.OnClickListener {

    private GifImageView ivBoxStartup;

    private DrawableTextView tvBleOpen;

    private DrawableTextView tvBleLocation;

    private UBTBaseDialog mEnterDialog;

    private final int BLUETOOTH_REQUESTCODE = 0x100; //权限申请后的返回码

    private BroadcastReceiver mReceive = new BluetoothStateBroadcastReceive();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBack(true);
        hiddleTitle();
        registerBluetoothReceiver();
    }

    @Override
    protected int getConentView() {
        return R.layout.activity_ble_ready;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initViews();
    }

    private void initViews() {
        ivBoxStartup = findViewById(R.id.iv_box_startup);
        ivBoxStartup.post(() -> {
            ivBoxStartup.setImageResource(R.drawable.ble_open);
        });

        tvBleOpen = findViewById(R.id.tv_ble_open);
        tvBleOpen.setOnClickListener(this);

        tvBleLocation = findViewById(R.id.tv_ble_location);
        tvBleLocation.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBluetoothReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBleView();
        updateLocationView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ble_open:
                openBle();
                break;
            case R.id.tv_ble_location:
                reqLocationPermission();
                break;
            default:
                break;
        }
    }

    private void handlePageAction() {
        if (!tvBleOpen.isEnabled() && !tvBleLocation.isEnabled()) {
            ActivityRoute.toAnotherActivity(this, SetNetWorkEnterActivity.class, true);
        }
    }

    private void updateBleView() {
        if (checkBle()) {
            tvBleOpen.setEnabled(false);
            tvBleOpen.setDrawable(DrawableTextView.LEFT, ContextCompat.getDrawable(this, R.drawable.ic_done_only));
        } else {
            //TODO ble isnot open
            tvBleOpen.setEnabled(true);
            tvBleOpen.setDrawable(DrawableTextView.LEFT, null);
        }
        handlePageAction();
    }

    private void updateLocationView() {
        if (checkLocation()) {
            tvBleLocation.setEnabled(false);
            tvBleLocation.setDrawable(DrawableTextView.LEFT, ContextCompat.getDrawable(this, R.drawable.ic_done_only));
        } else {
            //TODO location permission is not granted
            tvBleLocation.setEnabled(true);
            tvBleLocation.setDrawable(DrawableTextView.LEFT, null);
        }
        handlePageAction();
    }

    private void openBle() {
        mEnterDialog = new UBTBaseDialog(this);
        mEnterDialog.setTips(getString(R.string.ubt_want_open_bluetooth));
        mEnterDialog.setRightButtonTxt(getString(R.string.ubt_allowed));
        mEnterDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                BlueToothManager.openBlueToothSetting(BleConfigReadyActivity.this, BLUETOOTH_REQUESTCODE);
            }

        });
        mEnterDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_REQUESTCODE) {
            updateBleView();
        }
    }



    private boolean checkBle() {
        boolean isOpen = false;
        final byte state = BlueToothManager.getBluetoothState();
        switch (state) {
            case BlueToothManager.BLUETOOTH_STATE_OPEN:
                //TODO 蓝牙已开启
                isOpen = true;
                break;

            case BlueToothManager.BLUETOOTH_STATE_CLOSED:
                //TODO 蓝牙已关闭
                isOpen = false;
                break;

            case BlueToothManager.BLUETOOTH_STATE_NONE:
                //TODO 蓝牙模块不存在
                isOpen = false;
                ToastUtils.showShortToast(this, getString(R.string.ubt_bluetooth_none));
                break;
            default:
                break;
        }
        return isOpen;
    }

    private boolean checkLocation() {
        return AndPermission.hasPermission(this, Permission.LOCATION);
    }

    private void reqLocationPermission() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            AndPermission.with(this)
                    .requestCode(0x1102)
                    .permission(Permission.LOCATION)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            updateLocationView();
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            showPermissionDialog(Permission.LOCATION);
                            updateLocationView();
                        }
                    })
                    .rationale((requestCode, rationale) -> rationale.resume())
                    .start();
        } else {
            updateLocationView();
        }
    }

    private void registerBluetoothReceiver(){
        if(mReceive == null){
            mReceive = new BluetoothStateBroadcastReceive();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        registerReceiver(mReceive, intentFilter);
    }

    private void unregisterBluetoothReceiver(){
        if(mReceive != null){
            unregisterReceiver(mReceive);
            mReceive = null;
        }
    }

    class BluetoothStateBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
//                    Toast.makeText(context , "蓝牙设备:" + device.getName() + "已链接", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                    Toast.makeText(context , "蓝牙设备:" + device.getName() + "已断开", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState){
                        case BluetoothAdapter.STATE_OFF:
                            updateBleView();
//                            Toast.makeText(context , "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_ON:
//                            Toast.makeText(context , "蓝牙已开启"  , Toast.LENGTH_SHORT).show();
                            updateBleView();
                            break;
                    }
                    break;
            }
        }
    }

}

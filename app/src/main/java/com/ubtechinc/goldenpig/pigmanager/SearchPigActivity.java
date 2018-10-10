package com.ubtechinc.goldenpig.pigmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.bean.BundingListenerAbster;
import com.ubtechinc.goldenpig.pigmanager.bluetooth.BlueToothManager;
import com.ubtechinc.goldenpig.pigmanager.widget.OnPigListItemClickListener;
import com.ubtechinc.goldenpig.pigmanager.widget.PigListDialog;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import pl.droidsonroids.gif.GifImageView;


/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :搜索音箱界面
 * @time :2018/8/23 18:52
 * @change :
 * @changetime :2018/8/23 18:52
 */
public class SearchPigActivity extends BaseToolBarActivity implements View.OnClickListener {
    private GifImageView mGifImg;
    private Button mSearchBtn; ///开始搜索音箱
    private TextView mTipsTv;
    private final int BLUETOOTH_REQUESTCODE = 100; //权限申请后的返回码

    private boolean isSearched; //是否进行再次搜索
    private UBTBaseDialog mEnterDialog;

    private PermissionLocationRequest mLocationRequest;

    private static final int MSG_WATH_DISCONNECT_SUCCESS = 0x001;

    private UbtBluetoothDevice mBluetoothDevice;
    private BungdingManager mBangdingManager;
    private String TAG="SearchPigActivity";
    private PigListDialog pigListDialog;
    private CountDownTimer mTimer;
    private boolean mHasPermission;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getConentView() {
        return R.layout.activity_search_pig;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle(getString(R.string.ubt_pig_bind));

        setTitleBack(true);
        initViews();
        mBangdingManager = new BungdingManager(this);
    }

    private void initViews() {
        mGifImg = (GifImageView) findViewById(R.id.ubt_img_set_net_logo);

        mGifImg.setImageResource(R.drawable.pig_mute);

        mTipsTv = (TextView) findViewById(R.id.ubt_tv_set_net_tips);
        mTipsTv.setText(R.string.ubt_press_mute_btn_tips);

        mSearchBtn = (Button) findViewById(R.id.ubt_btn_start_set_net);
        mSearchBtn.setText(R.string.ubt_search_pig);
        mSearchBtn.setOnClickListener(this);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermission();
        }
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
    @Override
    protected void onResume() {
        super.onResume();
        closeEnterDialog();
        if (isSearched){
            mSearchBtn.setText("重新搜索");
        }else {
            mSearchBtn.setText("搜索音箱");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==BLUETOOTH_REQUESTCODE){
            checkBlueTooth();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeEnterDialog();
        cancelTimer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_start_set_net:
                checkBlueTooth();
                break;
            default:
                break;
        }
    }
    private void cancelTimer(){
        if (mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
    }
    private void checkBlueTooth() {
        final byte state = BlueToothManager.getBluetoothState();
        switch (state) {
            case BlueToothManager.BLUETOOTH_STATE_OPEN:
                startSearchPig();
                break;

            case BlueToothManager.BLUETOOTH_STATE_CLOSED:
                openBlueTooth();
                break;

            case BlueToothManager.BLUETOOTH_STATE_NONE:
                ToastUtils.showShortToast(this, getString(R.string.ubt_bluetooth_none));
                break;
            default:
                break;
        }
    }
    private void startTimer(){
        if (mTimer == null) {
            mTimer = new CountDownTimer((long) (60000), 60000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                     if (pigListDialog!=null&&pigListDialog.isShowing()){
                         if (pigListDialog.getBleCount()<1){
                             showNotify("未搜到音箱，请确认已按照引导视频正确操作");
                         }else if (isClicked){
                             ToastUtils.showLongToast(SearchPigActivity.this,R.string.ubt_bunding_ping_timeout);
                         }
                         isClicked=false;
                         UbtBluetoothManager.getInstance().closeConnectBle();
                         pigListDialog.dismiss();

                     }
                     mTimer=null;
                }
            };
            mTimer.start();
        }
    }
    private void openBlueTooth() {
        mEnterDialog = new UBTBaseDialog(this);
        mEnterDialog.setTips(getString(R.string.ubt_want_open_bluetooth));
        mEnterDialog.setRightButtonTxt(getString(R.string.ubt_allowed));
        mEnterDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {

            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

                BlueToothManager.openBlueToothSetting(SearchPigActivity.this,BLUETOOTH_REQUESTCODE);
            }

        });
        mEnterDialog.show();
    }

    private void closeEnterDialog() {
        if (mEnterDialog != null) {
            if (mEnterDialog.isShowing()) {
                mEnterDialog.dismiss();
            }
            mEnterDialog = null;
        }
    }

    private void startSearchPig() {
        if (Build.VERSION_CODES.M<=Build.VERSION.SDK_INT) {
            if (AndPermission.hasPermission(SearchPigActivity.this, Permission.LOCATION)) {
                showPigListDialog();
            } else {
                if (null == mLocationRequest) {
                    mLocationRequest = new PermissionLocationRequest(SearchPigActivity.this);
                }
                mLocationRequest.request(new PermissionLocationRequest.PermissionLocationCallback() {
                    @Override
                    public void onSuccessful() {
                        showPigListDialog();
                    }

                    @Override
                    public void onFailure() {


                    }

                    @Override
                    public void onRationSetting() {


                    }
                });
            }
        }else {
            showPigListDialog();
        }
    }
    private boolean isClicked;
    private void showPigListDialog(){
        pigListDialog = new PigListDialog(this);
        pigListDialog.setBluetoothItemClickListener(new OnPigListItemClickListener() {
            @Override
            public void onClick(int pos, UbtBluetoothDevice device) {
                isClicked=true;
                connectBleDevice(device);
                startTimer();

            }
        });
        pigListDialog.show();
    }
    private void connectBleDevice(final UbtBluetoothDevice device) {
        if (device != null) {
            mBluetoothDevice = device;
            mBangdingManager.setBangdingListener(mBandingListenerAbster);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    mHandler.sendEmptyMessage(MSG_WATH_DISCONNECT_SUCCESS);
                }
            }).start();
        }
    }

    private void toSetWifi(){
        closeEnterDialog();
        cancelTimer();

        ActivityRoute.toAnotherActivity(SearchPigActivity.this,SetPigNetWorkActivity.class,false);
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WATH_DISCONNECT_SUCCESS:
                    UbtBluetoothManager.getInstance().closeConnectBle();
                    UbtBluetoothManager.getInstance().connectBluetooth(mBluetoothDevice);
                    break;
                 
                default:
                    break;
            }
        }
    };
    BundingListenerAbster mBandingListenerAbster = new BundingListenerAbster(){
        @Override
        public void onFaild(int errorCode) {
            super.onFaild(errorCode);

            switch (errorCode){
                case 2041:
                    if (pigListDialog!=null&&pigListDialog.isShowing()) {

                        ToastUtils.showShortToast(SearchPigActivity.this, R.string.ubt_one_user_one_pig);
                    }
                    break;
                default:
                    ToastUtils.showShortToast(SearchPigActivity.this, Constants.getErrorMsg(errorCode));
                    break;
            }
            pigListDialog.dismiss();
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
        public void connWifiSuccess() {
            super.connWifiSuccess();
        }

        @Override
        public void onMaster() {
            super.onMaster();
            if (pigListDialog!=null) {
                pigListDialog.dismiss();
            }
            if (mTimer!=null){
                mTimer.cancel();
            }
            toSetWifi();
        }

        @Override
        public void onUnBind() {
            super.onUnBind();
            //toSetWifi();
            //ToastUtils.showShortToast(SearchPigActivity.this,"用户未绑定");
        }
    };

}

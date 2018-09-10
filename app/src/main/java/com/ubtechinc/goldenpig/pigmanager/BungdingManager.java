package com.ubtechinc.goldenpig.pigmanager;


import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.JsonUtils;

import com.ubtechinc.bluetooth.BleConnectAbstract;
import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.bean.UbtScanResult;
import com.ubtechinc.goldenpig.pigmanager.model.RobotAllAccountViewModel;
import com.ubtechinc.goldenpig.pigmanager.observeable.MyPigsLive;
import com.ubtechinc.goldenpig.pigmanager.observeable.RobotBindStateLive;
import com.ubtechinc.goldenpig.pigmanager.register.RegisterPigRepository;
import com.ubtechinc.nets.utils.JsonUtil;
import com.ubtechinc.tvlloginlib.TVSManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ubtechinc.bluetooth.Constants.CODE_1;
import static com.ubtechinc.bluetooth.Constants.PIG_REPLY_NET_STATE;
import static com.ubtechinc.bluetooth.Constants.ROBOT_BANGDING_SUCCESS;
import static com.ubtechinc.bluetooth.Constants.ROBOT_CONNECT_SUCCESS;
import static com.ubtechinc.bluetooth.Constants.ROBOT_REPLY_WIFI_IS_OK_TRANS;
import static com.ubtechinc.bluetooth.Constants.WIFI_LIST_RESLUT_TRANS;
import static com.ubtechinc.goldenpig.pigmanager.BungdingManager.BindState.HaventBind;
import static com.ubtechinc.goldenpig.pigmanager.BungdingManager.BindState.MySelf;
import static com.ubtechinc.goldenpig.pigmanager.BungdingManager.BindState.Networkerror;
import static com.ubtechinc.goldenpig.pigmanager.BungdingManager.BindState.Others;


/**
 * @author：wululin
 * @date：2017/10/30 10:43
 * @modifier：ubt
 * @modify_date：2017/10/30 10:43
 * [A brief description]
 * version
 */

public class BungdingManager {
    public enum BindState{
        MySelf, Others, HaventBind, Networkerror
    }
    private static final String TAG = BungdingManager.class.getSimpleName();
    private BanddingListener mBanddingListener;
    private RegisterPigRepository mRobotRepository;
    private String mSerialId;
    private GetWifiListListener mGetWifiListListener;
    private BaseActivity mContext;
    private ICommandProduce commandProduce;
    private String clientIdRecord;
    private RegisterRobotModule.Response response;
    private String  mToken;
    public BungdingManager(BaseActivity context) {
        UbtBluetoothManager.getInstance().setBleConnectListener(mBleConnectAbstract);
        mRobotRepository = new RegisterPigRepository();
        commandProduce = new JsonCommandProduce();
        this.mContext = context;
    }
    public BungdingManager(BaseActivity context,String token) {
        UbtBluetoothManager.getInstance().setBleConnectListener(mBleConnectAbstract);
        mRobotRepository = new RegisterPigRepository();
        commandProduce = new JsonCommandProduce();
        this.mContext = context;
        this.mToken=token;
    }
    public void setBangdingListener(BanddingListener listener) {
        if (listener != null) {
            UbtBluetoothManager.getInstance().setBleConnectListener(mBleConnectAbstract);
        }
        mBanddingListener = listener;
    }

    public void setGetWifiListListener(GetWifiListListener getWifiListListener){
        this.mGetWifiListListener = getWifiListListener;
    }

    private boolean mRobotWifiIsOk;
    private String mPid;
    private String mSid;
    private BluetoothDevice mCurrentDevices;

    public void setCommandProduce(ICommandProduce commandProduce) {
        this.commandProduce = commandProduce;
    }

    BleConnectAbstract mBleConnectAbstract = new BleConnectAbstract(){
        @Override
        public void receiverDataFromRobot(String data) {

            try{
                JSONObject rePlyJson = new JSONObject(data);
                if (rePlyJson.has(Constants.DATA_COMMAND)) {
                    int command = rePlyJson.getInt(Constants.DATA_COMMAND);
                    if(command == Constants.ROBOT_CONNENT_WIFI_SUCCESS){
                        Log.i(TAG,"isChangeWifi========" + UbtBluetoothManager.getInstance().isChangeWifi());
                        if(UbtBluetoothManager.getInstance().isChangeWifi()){
                            if(mBanddingListener != null){
                                mBanddingListener.connWifiSuccess();
                            }

                            if(mBanddingListener != null){
                                RegisterRobotModule.Response response = new  RegisterRobotModule.Response();
                                response.setSuccess(true);
                                mBanddingListener.onSuccess(response);
                            }

                        }else {
                            /*final String productId = rePlyJson.getString(Constants.PRODUCTID);
                            mSerialId = rePlyJson.getString(Constants.SERISAL_NUMBER);
                            getClientId(productId,mSerialId);*/
                            if(mBanddingListener != null){
                                mBanddingListener.connWifiSuccess();
                            }
                        }

                    }else if(command == Constants.ROBOT_BLE_NETWORK_FAIL){
                        int errorCode = rePlyJson.getInt(Constants.ERROR_CODE);
                        Log.d(TAG,"errorCode=====" + errorCode);
                        ///UbtBluetoothManager.getInstance().closeConnectBle();
                        if (mBanddingListener != null) {
                            mBanddingListener.onFaild(errorCode);
                        }
                    }else if(command == ROBOT_BANGDING_SUCCESS) {
                        //UbtBluetoothManager.getInstance().closeConnectBle();
                        //EventBus.getDefault().post(new BindingRobotSuccessEvent());
                        if(mBanddingListener != null){
                            mBanddingListener.onSuccess(response);
                        }
                    }else if(command == WIFI_LIST_RESLUT_TRANS){
                        String appListStr = (String) rePlyJson.get(Constants.WIIF_LIST_COMMAND);
                        JSONArray jsonArray = new JSONArray(appListStr);
                        List<UbtScanResult> scanResultList=null;
                        if(jsonArray.length()!=0){
                            scanResultList  = JsonUtils.stringToObjectList(appListStr, UbtScanResult.class);
                        }
                        if(mGetWifiListListener!=null){
                            mGetWifiListListener.onGetWifiList(scanResultList);
                        }
                    }else if(command == ROBOT_CONNECT_SUCCESS){
                        int code = rePlyJson.getInt(Constants.CODE);
                       /* final String productId = rePlyJson.getString(Constants.PRODUCTID);*/

                        getClientId(BuildConfig.APP_ID,mSerialId);
                        //String serailId = mCurrentDevices.getName().replace(Constants.ROBOT_TAG,"");
                       // checkRobotBindState(serailId,mToken,"wx0238743de057a634");
                        /*if(code == Constants.CODE_0){
                            sendWifiIsOkMsgToRobot();
                        }else {
                            UbtBluetoothManager.getInstance().closeConnectBle();
                            if(mBanddingListener != null){
                                mBanddingListener.devicesConnectByOther();
                            }
                        }*/
                    }else if(command == ROBOT_REPLY_WIFI_IS_OK_TRANS){
                        Log.i(TAG,"isChangeWifi========" + UbtBluetoothManager.getInstance().isChangeWifi());
                        if(UbtBluetoothManager.getInstance().isChangeWifi()){
                            if(mBanddingListener!=null){
                                mBanddingListener.robotNotWifi();
                            }
                        }else {
                            int  code = rePlyJson.getInt(Constants.CODE);
                            mRobotWifiIsOk = code == CODE_1;
                            if(mRobotWifiIsOk){
                                mPid = rePlyJson.getString(Constants.PRODUCTID);
                                mSid = rePlyJson.getString(Constants.SERISAL_NUMBER);
                            }
                            String serailId = mCurrentDevices.getName().replace(Constants.ROBOT_TAG,"");
                            checkRobotBindState(serailId,mToken,BuildConfig.APP_ID_WX);
                        }

                    }else if (command==PIG_REPLY_NET_STATE){

                        if (mBanddingListener!=null){
                            mBanddingListener.onPigConnected(data);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void sendDataFailed(String result) {
            //UbtBluetoothManager.getInstance().closeConnectBle();
            if (mBanddingListener != null) {
                // 发送失败
                mBanddingListener.onFaild(Constants.BLUETOOTH_SEND_FIAL);
            }
        }

        @Override
        public void connectSuccess(BluetoothDevice device) {
            mCurrentDevices = device;
            mSerialId = device.getName().replace(Constants.ROBOT_TAG,"");
            sendBleConnectSuccessMsgToRobot();
            if(mBanddingListener != null){
                mBanddingListener.connectSuccess();
            }
        }

        @Override
        public void connectFailed() {
            //FIXME --logic.peng 连接失败会一直重连, 不要关闭蓝牙
            if(mBanddingListener != null){
                mBanddingListener.connectFailed();
            }
        }
    };


    RegisterPigRepository.RegisterRobotListener mResponseListener  = new RegisterPigRepository.RegisterRobotListener() {
        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            final String token= CookieInterceptor.get().getToken();
            checkRobotBindState(mSerialId,token,BuildConfig.APP_ID);
            /*if(response.isSuccess()){
                BungdingManager.this.response = response;
                if(clientIdRecord != null) {
                    Log.i(TAG, " sendClientIdToRobot onSuccess -- response : ");
                    sendClientIdToRobot(clientIdRecord);
                }
            }else {
                UbtBluetoothManager.getInstance().closeConnectBle();
                if(response.getResultCode() == 1004){ //序列号不存在
                    if(mBanddingListener != null){
                        mBanddingListener.onFaild(Constants.ON_SERAIL_ERROR_CODE);
                    }
                } else if(response.getResultCode() == 1005) {
                   // MyPigsLive.getInstance().getRobotUserId();
                    if(mBanddingListener != null){
                        mBanddingListener.onFaild(Constants.ALREADY_BADING);
                    }
                }
            }*/

        }

        @Override
        public void onError(String error) {
           // UbtBluetoothManager.getInstance().closeConnectBle();
            int erorCode=-1;
            try {
                if (error!=null) {
                    JSONObject jsonObject = new JSONObject(error);
                    erorCode = jsonObject.getInt("code");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (erorCode==2041){
                final String token=CookieInterceptor.get().getToken();
                checkRobotBindState(mSerialId,token,BuildConfig.APP_ID);
            }
            if(mBanddingListener != null){
                mBanddingListener.onFaild(erorCode);
            }
        }
    };

    /**
     * 获取clientId
     */
    private void getClientId(final String productId, final String dsn){
        final String userId=AuthLive.getInstance().getUserId();
        final String token= CookieInterceptor.get().getToken();

       TVSManager.getInstance(mContext, BuildConfig.APP_ID_WX,BuildConfig.APP_ID_QQ).tvsAuth(BuildConfig.PRODUCT_ID, dsn, new TVSManager.TVSAuthListener() {
            @Override
            public void onSuccess(String clientId) {
                Log.i(TAG,"onSuccess======="+clientId);
                clientIdRecord = clientId;
                // 先绑定机器人绑定成功再发送clientId
                mRobotRepository.registerRobot(token, userId,dsn,productId, mResponseListener);
            }

            @Override
            public void onError(int code) {
                Log.i(TAG,"onError=======");
                UbtBluetoothManager.getInstance().closeConnectBle();
                if(mBanddingListener != null){
                    mBanddingListener.onFaild(Constants.GET_CLIENT_ID_ERROR_CODE);
                }
            }
        });

    }

    /**
     * 发送clientId 给机器人
     */
    private void sendClientIdToRobot(String clientId){
        Log.i(TAG, " sendClientIdToRobot clientId : " + clientId);
//        String clientTrans = Utils.pactkClientIdCommandToRobot(clientId);
        String clientTrans = commandProduce.getClientId(clientId);
        if(!TextUtils.isEmpty(clientTrans)){
            UbtBluetoothManager.getInstance().sendMessageToBle(clientTrans);
        }
    }


    /**
     * 发送WiFi是否连接成功指令
     */
    private void sendWifiIsOkMsgToRobot(){
//        String message = Utils.pactkCommandToRobot(Constants.ROBOT_WIFI_IS_OK_TRANS);
        String message = commandProduce.getWifiState();
        if(!TextUtils.isEmpty(message)){
            UbtBluetoothManager.getInstance().sendMessageToBle(message);
        }
    }

    /**
     * 发送蓝牙连接成功指令
     */
    private void sendBleConnectSuccessMsgToRobot(){
        String message = commandProduce.getBleSuc();
        if(!TextUtils.isEmpty(message)){
            UbtBluetoothManager.getInstance().sendMessageToBle(message);
        }
    }

    private void checkRobotBindState(String serialId,String token,String appId) {
        RobotAllAccountViewModel mRobotAllAccountViewModel = new RobotAllAccountViewModel();
        mRobotAllAccountViewModel.checkRobotBindState(serialId,token,appId).observe(mContext, new Observer<RobotBindStateLive>() {
            @Override
            public void onChanged(@Nullable RobotBindStateLive robotBindStateLive) {

                switch (robotBindStateLive.getCurBindState()){
                    case Others:
                       // UbtBluetoothManager.getInstance().closeConnectBle();
                        List<CheckBindRobotModule.User> owerUsers = robotBindStateLive.getRobotOwners();
                        CheckBindRobotModule.User user = null;
                        for(CheckBindRobotModule.User user1: owerUsers){
                            if(user1.getRoleType() == 0){
                                user = user1;
                            }
                        }
                        if(mBanddingListener != null){
                            mBanddingListener.bindByOthers(user);
                        }
                        break;
                    case MySelf:
                      sendClientIdToRobot(clientIdRecord);
                        if (mBanddingListener!=null) {
                            mBanddingListener.onMaster();
                        }
                        break;
                    case HaventBind:
                        /*if(mRobotWifiIsOk){
                            getClientId(mPid,mSid);
                        }else {
                            if(mBanddingListener!=null){
                                mBanddingListener.robotNotWifi();//配网
                            }
                        }*/
                        if (mBanddingListener!=null){
                            mBanddingListener.onUnBind();
                        }
                        break;
                    case Networkerror:
                        UbtBluetoothManager.getInstance().closeConnectBle();
                        if(mBanddingListener != null){
                            mBanddingListener.onFaild(Constants.REGISTER_ROBOT_ERROR_CODE);
                        }
                        break;
                }
            }
        });
    }
    public interface BanddingListener{
        void connWifiSuccess();
        void onSuccess(RegisterRobotModule.Response response);
        void onFaild(int errorCode);//需要关闭蓝牙
        void connectSuccess();
        void devicesConnectByOther();
        void bindByOthers(CheckBindRobotModule.User user);
        void robotNotWifi();
        void connectFailed();//不用关闭蓝牙
        void hasWifi(String wifi); //小猪已经连接了的Wifi时，会掉名称
        void onMaster(); ///用户是管理员
        void onUnBind();
        void onPigConnected(String wifiName);
    }

    public interface GetWifiListListener{
        void onGetWifiList(List<UbtScanResult> scanResultList);
    }


}

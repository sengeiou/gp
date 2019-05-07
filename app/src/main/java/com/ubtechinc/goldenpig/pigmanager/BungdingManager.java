package com.ubtechinc.goldenpig.pigmanager;


import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.robot.dmsdk.TVSWrapBridge;
import com.ubt.robot.dmsdk.TVSWrapConstant;
import com.ubt.robot.dmsdk.TVSWrapType;
import com.ubt.robot.dmsdk.model.TVSWrapAccountInfo;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtechinc.bluetooth.BleConnectAbstract;
import com.ubtechinc.bluetooth.Constants;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.net.CheckBindRobotModule;
import com.ubtechinc.goldenpig.net.RegisterRobotModule;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.bean.UbtScanResult;
import com.ubtechinc.goldenpig.pigmanager.model.RobotAllAccountViewModel;
import com.ubtechinc.goldenpig.pigmanager.observeable.RobotBindStateLive;
import com.ubtechinc.goldenpig.pigmanager.register.RegisterPigRepository;
import com.ubtechinc.goldenpig.utils.SCADAHelper;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.ubtechinc.bluetooth.Constants.CODE_1;
import static com.ubtechinc.bluetooth.Constants.PIG_REPLY_NET_STATE;
import static com.ubtechinc.bluetooth.Constants.ROBOT_BANGDING_SUCCESS;
import static com.ubtechinc.bluetooth.Constants.ROBOT_CONNECT_SUCCESS;
import static com.ubtechinc.bluetooth.Constants.ROBOT_REPLY_WIFI_IS_OK_TRANS;
import static com.ubtechinc.bluetooth.Constants.WIFI_LIST_RESLUT_TRANS;


/**
 * @author：wululin
 * @date：2017/10/30 10:43
 * @modifier：ubt
 * @modify_date：2017/10/30 10:43
 * [A brief description]
 * version
 */

public class BungdingManager {
    public enum BindState {
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
    private String mToken;
    private UbtTIMManager timManager;

    public BungdingManager(BaseActivity context) {
        UbtBluetoothManager.getInstance().setBleConnectListener(mBleConnectAbstract);
        mRobotRepository = new RegisterPigRepository();
        commandProduce = new JsonCommandProduce();
        timManager = UbtTIMManager.getInstance();
        this.mContext = context;
    }

    public BungdingManager(BaseActivity context, String token) {
        UbtBluetoothManager.getInstance().setBleConnectListener(mBleConnectAbstract);
        mRobotRepository = new RegisterPigRepository();
        commandProduce = new JsonCommandProduce();
        this.mContext = context;
        this.mToken = token;
    }

    public void setBangdingListener(BanddingListener listener) {
        if (listener != null) {
            UbtBluetoothManager.getInstance().setBleConnectListener(mBleConnectAbstract);
        }
        mBanddingListener = listener;
    }

    public void setGetWifiListListener(GetWifiListListener getWifiListListener) {
        this.mGetWifiListListener = getWifiListListener;
    }

    private boolean mRobotWifiIsOk;
    private String mPid;
    private String mSid;
    private BluetoothDevice mCurrentDevices;

    public void setCommandProduce(ICommandProduce commandProduce) {
        this.commandProduce = commandProduce;
    }

    BleConnectAbstract mBleConnectAbstract = new BleConnectAbstract() {
        @Override
        public void receiverDataFromRobot(String data) {

            try {
                JSONObject rePlyJson = new JSONObject(data);
                Log.i(TAG, "mBleConnectAbstract==rePlyJson====" + rePlyJson.toString());
                if (rePlyJson.has(Constants.DATA_COMMAND)) {
                    int command = rePlyJson.getInt(Constants.DATA_COMMAND);
                    if (command == Constants.ROBOT_CONNENT_WIFI_SUCCESS) {
                        Log.i(TAG, "isChangeWifi========" + UbtBluetoothManager.getInstance().isChangeWifi());
                        if (UbtBluetoothManager.getInstance().isChangeWifi()) {
                            if (mBanddingListener != null) {
                                mBanddingListener.connWifiSuccess();
                            }

                            if (mBanddingListener != null) {
                                RegisterRobotModule.Response response = new RegisterRobotModule.Response();
                                response.setSuccess(true);
                                mBanddingListener.onSuccess(response);
                            }

                        } else {
                            /*final String productId = rePlyJson.getString(Constants.PRODUCTID);
                            mSerialId = rePlyJson.getString(Constants.SERISAL_NUMBER);
                            getClientId(productId,mSerialId);*/
                            if (mBanddingListener != null) {
                                mBanddingListener.connWifiSuccess();
                            }
                        }

                    } else if (command == Constants.ROBOT_BLE_NETWORK_FAIL) {
                        int errorCode = rePlyJson.getInt(Constants.ERROR_CODE);
                        Log.d(TAG, "errorCode=====" + errorCode);
                        ///UbtBluetoothManager.getInstance().closeConnectBle();
                        if (mBanddingListener != null) {
                            mBanddingListener.onFaild(errorCode, "");
                        }
                    } else if (command == ROBOT_BANGDING_SUCCESS) {
                        //UbtBluetoothManager.getInstance().closeConnectBle();
                        //EventBus.getDefault().post(new BindingRobotSuccessEvent());
                        if (mBanddingListener != null) {
                            mBanddingListener.onSuccess(response);
                        }
                    } else if (command == WIFI_LIST_RESLUT_TRANS) {
                        String appListStr = (String) rePlyJson.get(Constants.WIIF_LIST_COMMAND);
                        JSONArray jsonArray = new JSONArray(appListStr);
//                        UbtLogger.d(TAG, "WIFI_LIST_RESLUT_TRANS:" + jsonArray.length());
                        List<UbtScanResult> scanResultList = null;
                        if (jsonArray.length() != 0) {
                            scanResultList = JsonUtils.stringToObjectList(appListStr, UbtScanResult.class);
                        }
                        UbtLogger.d(TAG, "WIFI_LIST_RESLUT_TRANS:" + scanResultList.size());
                        if (mGetWifiListListener != null) {
                            mGetWifiListListener.onGetWifiList(scanResultList);
                        }
                    } else if (command == ROBOT_CONNECT_SUCCESS) {
                        int code = rePlyJson.getInt(Constants.CODE);
                        /* final String productId = rePlyJson.getString(Constants.PRODUCTID);*/

                        getClientId(BuildConfig.PRODUCT_ID, mSerialId);
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
                    } else if (command == ROBOT_REPLY_WIFI_IS_OK_TRANS) {
                        Log.i(TAG, "isChangeWifi========" + UbtBluetoothManager.getInstance().isChangeWifi());
                        if (UbtBluetoothManager.getInstance().isChangeWifi()) {
                            if (mBanddingListener != null) {
                                mBanddingListener.robotNotWifi();
                            }
                        } else {
                            int code = rePlyJson.getInt(Constants.CODE);
                            mRobotWifiIsOk = code == CODE_1;
                            if (mRobotWifiIsOk) {
                                mPid = rePlyJson.getString(Constants.PRODUCTID);
                                mSid = rePlyJson.getString(Constants.SERISAL_NUMBER);
                            }
                            String serailId = mCurrentDevices.getName().replace(Constants.ROBOT_TAG, "");
                            checkRobotBindState(serailId, mToken, BuildConfig.APP_ID);
                        }

                    } else if (command == PIG_REPLY_NET_STATE) {

                        if (mBanddingListener != null) {
                            mBanddingListener.onPigConnected(data);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sendDataFailed(String result) {
            //UbtBluetoothManager.getInstance().closeConnectBle();
            if (mBanddingListener != null) {
                // 发送失败
//                mBanddingListener.onFaild(Constants.BLUETOOTH_SEND_FIAL, "");
            }
        }

        @Override
        public void connectSuccess(BluetoothDevice device) {
            mCurrentDevices = device;
            mSerialId = device.getName().replace(Constants.ROBOT_TAG, "");
            sendBleConnectSuccessMsgToRobot();
            if (mBanddingListener != null) {
                mBanddingListener.connectSuccess();
            }
        }

        @Override
        public void connectFailed() {
            //FIXME --logic.peng 连接失败会一直重连, 不要关闭蓝牙
            if (mBanddingListener != null) {
                mBanddingListener.connectFailed();
            }
        }
    };


    RegisterPigRepository.RegisterRobotListener mResponseListener = new RegisterPigRepository.RegisterRobotListener() {
        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            final String token = CookieInterceptor.get().getToken();
            checkRobotBindState(mSerialId, token, BuildConfig.APP_ID);
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
            int erorCode = -1;
            String message = "";
            try {
                if (error != null) {
                    JSONObject jsonObject = new JSONObject(error);
                    erorCode = jsonObject.getInt("code");
                    message = jsonObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (erorCode == 2041) {
                final String token = CookieInterceptor.get().getToken();
                checkRobotBindState(mSerialId, token, BuildConfig.APP_ID);
            }
            if (mBanddingListener != null) {
                mBanddingListener.onFaild(erorCode, message);
            }
        }
    };

    /**
     * 获取clientId
     */
    private void getClientId(final String productId, final String dsn) {
        final String userId = AuthLive.getInstance().getUserId();
        final String token = CookieInterceptor.get().getToken();

        TVSWrapBridge.tvsTokenVerify(new TVSWrapBridge.TVSWrapCallback() {
            @Override
            public void onError(int errCode) {
                UbtToastUtils.showCustomToast(mContext, "刷票失败");
                if (mBanddingListener != null) {
                    mBanddingListener.onFaild(Constants.GET_CLIENT_ID_ERROR_CODE, "");
                }
            }

            @Override
            public void onSuccess(Object result) {
                TVSWrapAccountInfo tvsWrapAccountInfo = TVSWrapBridge.getTVSAccountInfo(productId, dsn);
                String clientId = tvsWrapAccountInfo.getClientID();
                clientIdRecord = clientId;
                //TODO 校验和当前绑定的是否是同一个
                PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                if (pigInfo != null && !TextUtils.isEmpty(pigInfo.getRobotName())) {
                    String robotDsn = pigInfo.getRobotName();
                    if (mBanddingListener != null) {
                        if (robotDsn.equals(dsn)) {
                            //同一个猪
                            mBanddingListener.onStopBind(true);
                            if (pigInfo.isAdmin) {
                                sendClientIdToRobot(clientIdRecord);
                            }
                        } else {
                            //不同猪
                            mBanddingListener.onStopBind(false);
                        }
                    }
                } else {
                    mRobotRepository.registerRobot(token, userId, dsn, BuildConfig.APP_ID, BuildConfig.product,
                            mResponseListener);
                }
            }
        });
    }

    /**
     * 发送clientId 给机器人
     */
    private void sendClientIdToRobot(String clientId) {
        Log.i(TAG, " sendClientIdToRobot clientId : " + clientId);
        final String userId = AuthLive.getInstance().getUserId();
        String clientTrans = commandProduce.getClientId(clientId, userId);
        if (!TextUtils.isEmpty(clientTrans)) {
            UbtBluetoothManager.getInstance().sendMessageToBle(clientTrans);
        }
        //TODO TVS绑定dsn注册音乐会员
        TVSWrapBridge.tvsBindDevice(TVSWrapType.TVS, TVSWrapConstant.PRODUCT_ID, mSerialId, new TVSWrapBridge.TVSWrapCallback() {
            @Override
            public void onError(int errCode) {
//                com.ubtech.utilcode.utils.ToastUtils.showShortToast("注册音乐会员失败，错误码：" + errCode);
                Log.i(TAG, "注册音乐会员失败，错误码:" + errCode);
            }

            @Override
            public void onSuccess(Object result) {
//                com.ubtech.utilcode.utils.ToastUtils.showShortToast("注册音乐会员成功");
                Log.i(TAG, "注册音乐会员成功");
            }
        });
    }


    /**
     * 发送WiFi是否连接成功指令
     */
    private void sendWifiIsOkMsgToRobot() {
//        String message = Utils.pactkCommandToRobot(Constants.ROBOT_WIFI_IS_OK_TRANS);
        String message = commandProduce.getWifiState();
        if (!TextUtils.isEmpty(message)) {
            UbtBluetoothManager.getInstance().sendMessageToBle(message);
        }
    }

    /**
     * 发送蓝牙连接成功指令
     */
    private void sendBleConnectSuccessMsgToRobot() {
        String message = commandProduce.getBleSuc();
        if (!TextUtils.isEmpty(message)) {
            UbtBluetoothManager.getInstance().sendMessageToBle(message);
        }
    }

    private void checkRobotBindState(String serialId, String token, String appId) {
        RobotAllAccountViewModel mRobotAllAccountViewModel = new RobotAllAccountViewModel();
        mRobotAllAccountViewModel.checkRobotBindState(serialId, token, appId).observe(mContext, new
                Observer<RobotBindStateLive>() {
                    @Override
                    public void onChanged(@Nullable RobotBindStateLive robotBindStateLive) {

                        switch (robotBindStateLive.getCurBindState()) {
                            case Others: //TODO 普通成员
                                // UbtBluetoothManager.getInstance().closeConnectBle();
                                List<CheckBindRobotModule.User> owerUsers = robotBindStateLive.getRobotOwners();
                                CheckBindRobotModule.User user = null;
                                for (CheckBindRobotModule.User user1 : owerUsers) {
                                    if (user1.getRoleType() == 0) {
                                        user = user1;
                                    }
                                }
                                if (mBanddingListener != null) {
                                    mBanddingListener.bindByOthers(user);
                                }
                                break;
                            case MySelf: //TODO 如果IM未登录，进行IM登录
                                doTIMLogin();
                                sendClientIdToRobot(clientIdRecord);
                                PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                                if (pigInfo == null) {
                                    pigInfo = new PigInfo();
                                }
                                pigInfo.setRobotName(mSerialId);
                                SCADAHelper.recordEvent(SCADAHelper.EVENET_APP_ROBOT_BIND);
                                if (mBanddingListener != null) {
                                    mBanddingListener.onMaster();
                                }
                                break;
                            case HaventBind: //TODO 不在成员列表
                                if (mBanddingListener != null) {
                                    mBanddingListener.onUnBind();
                                }
                                break;
                            case Networkerror:
                                //UbtBluetoothManager.getInstance().closeConnectBle();
                                if (mBanddingListener != null) {
                                    mBanddingListener.onFaild(Constants.REGISTER_ROBOT_ERROR_CODE, "");
                                }
                                break;
                                default:
                        }
                    }
                });
    }

    private void doTIMLogin() {
        if (timManager == null) {
            timManager = UbtTIMManager.getInstance();
        }
        timManager.loginTIM(AuthLive.getInstance().getUserId(), mSerialId, com.ubt.imlibv2.BuildConfig.IM_Channel);
    }

    public interface BanddingListener {
        void connWifiSuccess();

        void onSuccess(RegisterRobotModule.Response response);

        void onFaild(int errorCode, String message);//需要关闭蓝牙

        void connectSuccess();

        void devicesConnectByOther();

        void bindByOthers(CheckBindRobotModule.User user);

        void robotNotWifi();

        void connectFailed();//不用关闭蓝牙

        void hasWifi(String wifi); //八戒已经连接了的Wifi时，会掉名称

        void onMaster(); ///用户是管理员

        void onUnBind();

        void onPigConnected(String wifiName);

        /**
         * 阻止绑定
         *
         * @param isConflict 是否同一个
         */
        void onStopBind(boolean isConflict);
    }

    public interface GetWifiListListener {
        void onGetWifiList(List<UbtScanResult> scanResultList);
    }


}

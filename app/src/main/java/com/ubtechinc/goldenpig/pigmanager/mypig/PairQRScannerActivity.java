package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.text.TextUtils;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.qrcodelib.QRScannerActivity;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.pigmanager.register.GetPairPigQRHttpProxy;
import com.ubtechinc.goldenpig.pigmanager.register.PairPigHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author ubt
 */
public class PairQRScannerActivity extends QRScannerActivity {
    @Override
    protected String getQrTitle() {
        return getString(R.string.ubt_scann_qr);
    }

    @Override
    protected String getQrSubTitle() {
        return getString(R.string.ubt_scann_qr_tips);
    }

    @Override
    protected void doSendSign(String msg) {
        new PairPigHttpProxy().doPair(CookieInterceptor.get().getToken(),
                BuildConfig.APP_ID, msg, new PairPigHttpProxy.PairPigCallback() {
                    @Override
                    public void onError(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(error)) {
                                    setErrorTips(error);
                                } else {
                                    setErrorTips(getString(R.string.ubt_pair_pig_fialure));
                                }
                            }
                        });

                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(R.string.ubt_pair_pig_success);

                                //TODO 小猪配对或解除配对后需要通过IM通知小猪本体功能
                                getPigPair();
                            }
                        });
                    }
                });
    }

    private void getPigPair() {
        new GetPairPigQRHttpProxy().getPairPigQR(this, CookieInterceptor.get().getToken(), BuildConfig
                .APP_ID, new GetPairPigQRHttpProxy.GetPairPigQRCallBack() {
            @Override
            public void onError(String error) {

                //TODO 获取配对数据失败
                setErrorTips("获取配对数据失败");
            }

            @Override
            public void onSuccess(String response) {

                //TODO 刷新配对信息
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject != null) {
                            JSONObject pairData = new JSONObject(jsonObject.optString("pairData"));
                            if (pairData != null) {
                                int userId = pairData.optInt("userId");
                                String serialNumber = pairData.optString("serialNumber");
                                int pairUserId = pairData.optInt("pairUserId");
                                String pairSerialNumber = pairData.optString("pairSerialNumber");
                                sendPairInfo(userId, serialNumber, pairUserId, pairSerialNumber);
                            }
                        }
                    } catch (JSONException e) {
                        UBTLog.e("pig", e.getMessage());
                    }
                }
            }
        });
    }

    private void sendPairInfo(int userId, String serialNumber, int pairUserId, String pairSerialNumber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("pairSerialNumber", pairSerialNumber);
        ActivityRoute.toAnotherActivity(PairQRScannerActivity.this, PairPigActivity.class, map, false);

        //TODO 给自己的猪发
        TIMConversation selfConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C, serialNumber);
        TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(1, pairSerialNumber));
        UbtTIMManager.getInstance().sendTIM(selfMessage, selfConversation);

        //TODO 给配对的猪发
        TIMConversation pairPigConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C, pairSerialNumber);
        TIMMessage pairPigMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(1, serialNumber));
        UbtTIMManager.getInstance().sendTIM(pairPigMessage, pairPigConversation);


        //TODO 给配对的用户发
        TIMConversation pairUserConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C, String.valueOf(pairUserId));
        TIMMessage pairUserMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.IM_ACCOUNT_PAIR);
        UbtTIMManager.getInstance().sendTIM(pairUserMessage, pairUserConversation);
    }
}

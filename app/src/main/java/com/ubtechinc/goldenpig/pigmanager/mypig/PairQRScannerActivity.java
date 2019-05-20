package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubt.qrcodelib.QRScannerActivity;
import com.ubtech.utilcode.utils.ActivityTool;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.entity.PairPig;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.register.PairPigHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.PAIR_PIG_UPDATE;

/**
 * @author ubt
 */
public class PairQRScannerActivity extends QRScannerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtil.register(this);
    }

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
                        runOnUiThread(() -> {
                            if (!TextUtils.isEmpty(error)) {
                                setErrorTips(error);
                            } else {
                                setErrorTips(getString(R.string.ubt_pair_pig_fialure));
                            }
                            restartScan();
                        });
                    }

                    @Override
                    public void onSuccess() {
                        UbtLogger.d("PairQR", "doSendSign onSuccess");
                        runOnUiThread(() -> {
                            ToastUtils.showShortToast(R.string.ubt_pair_pig_success);

                            ActivityRoute.toAnotherActivity(PairQRScannerActivity.this, PairPigActivity.class, true);
                            ActivityTool.finishActivity(QRCodeActivity.class);
                            //TODO 触发更新配对信息
                            EventBusUtil.sendEvent(new Event(EventBusUtil.DO_UPDATE_PAIR_PIG));
                        });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case PAIR_PIG_UPDATE:
                imSyncRelationShip();
                break;
        }
    }

    /**
     * 配对八戒或解除配对后需要通过IM通知八戒本体功能
     */
    private void imSyncRelationShip() {
        UbtLogger.d("imSyncRelationShip", "imSyncRelationShip");
        PairPig pairPig = AuthLive.getInstance().getPairPig();
        if (pairPig != null) {
            String serialNumber = pairPig.getSerialNumber();
            int pairUserId = pairPig.getPairUserId();
            String pairSerialNumber = pairPig.getPairSerialNumber();

//            ActivityRoute.toAnotherActivity(PairQRScannerActivity.this, PairPigActivity.class, true);
//            ActivityTool.finishActivity(QRCodeActivity.class);

            //TODO 给自己的猪发
            TIMConversation selfConversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, serialNumber);
            TIMMessage selfMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
            UbtTIMManager.getInstance().sendTIM(selfMessage, selfConversation);

            //TODO 给配对的猪发
            TIMConversation pairPigConversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, pairSerialNumber);
            TIMMessage pairPigMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
            UbtTIMManager.getInstance().sendTIM(pairPigMessage, pairPigConversation);


            //TODO 给配对的用户发
            TIMConversation pairUserConversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C, String.valueOf(pairUserId));
            TIMMessage pairUserMessage = ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.syncPairInfo(2));
            UbtTIMManager.getInstance().sendTIM(pairUserMessage, pairUserConversation);
        } else {
            setErrorTips("获取配对数据失败");
        }
    }

}

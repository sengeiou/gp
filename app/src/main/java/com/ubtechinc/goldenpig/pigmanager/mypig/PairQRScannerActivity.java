package com.ubtechinc.goldenpig.pigmanager.mypig;

import com.ubt.qrcodelib.QRScannerActivity;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.pigmanager.register.PairPigHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;

public class PairQRScannerActivity extends QRScannerActivity {
    @Override
    protected String getQrTitle() {
        return getString(R.string.ubt_scann_qr);
    }

    @Override
    protected String getQrSubTitle() {
        return getString( R.string.ubt_scann_qr_tips);
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
                                setErrorTips(getString(R.string.ubt_pair_pig_fialure));
                            }
                        });

                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(R.string.ubt_pair_pig_success);
                                ActivityRoute.toAnotherActivity(PairQRScannerActivity.this,PairPigActivity.class,false);
                            }
                        });
                    }
                });
    }
}

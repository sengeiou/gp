package com.ubtechinc.goldenpig.personal;

import android.text.TextUtils;

import com.ubt.qrcodelib.QRScannerActivity;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.pigmanager.register.AddMemberHttpProxy;

public class MemberQRScannerActivity extends QRScannerActivity {
    @Override
    protected String getQrTitle() {
        return getString(R.string.ubt_scann_qr_join_group);
    }

    @Override
    protected String getQrSubTitle() {
        return getString( R.string.ubt_scann_qr_tips);
    }

    @Override
    protected void doSendSign(String msg) {
        new AddMemberHttpProxy().addMember(CookieInterceptor.get().getToken(),
                BuildConfig.APP_ID, msg, new AddMemberHttpProxy.AddMemberCallback() {
                    @Override
                    public void onError(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(error)) {
                                    setErrorTips(getString(R.string.ubt_add_member_failure));
                                } else {
                                    setErrorTips(error);
                                }
                            }
                        });

                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(R.string.ubt_add_member_success);
                                finish();
                            }
                        });

                    }
                });
    }
}

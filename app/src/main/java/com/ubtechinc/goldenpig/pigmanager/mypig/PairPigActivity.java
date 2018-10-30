package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.pigmanager.register.UnpairHttpProxy;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :小猪配对界面
 * @time :2018/9/28 19:18
 * @change :
 * @changetime :2018/9/28 19:18
 */
public class PairPigActivity extends BaseToolBarActivity implements View.OnClickListener {

    private View unPairBtn;

    private TextView memberNameTv;

    private String pairUserId;

    private String serialNumber;

    private String pairSerialNumber;

    @Override
    protected int getConentView() {
        return R.layout.activity_pair_pig;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_pair_pig);
        initViews();
        initData();
    }

    private void initViews() {
        unPairBtn = findViewById(R.id.ubt_btn_unpair);
        unPairBtn.setOnClickListener(this);
        memberNameTv = findViewById(R.id.ubt_tv_member_name);
    }

    private void showUnpairDialog() {
        UBTBaseDialog ubtBaseDialog = new UBTBaseDialog(this);
        ubtBaseDialog.setTips(getString(R.string.ubt_confirm_unpair));
        ubtBaseDialog.setRightButtonTxt(getString(R.string.ubt_enter));
        ubtBaseDialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        ubtBaseDialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        ubtBaseDialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                unPair();
            }
        });
        ubtBaseDialog.show();
    }

    /**
     * 初始化与小猪配对的用户列表
     */
    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            pairSerialNumber = intent.getStringExtra("pairSerialNumber");
            pairUserId = intent.getStringExtra("pairUserId");
            serialNumber = intent.getStringExtra("serialNumber");
            memberNameTv.setText(pairSerialNumber);
        }
    }

    private void unPair() {
        UnpairHttpProxy httpProxy = new UnpairHttpProxy();
        httpProxy.doUnpair(CookieInterceptor.get().getToken(),
                BuildConfig.APP_ID, new UnpairHttpProxy.UnpairCallBack() {
                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShortToast(R.string.ubt_ubpair_filure);
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onUnPairSuccess();
                            }
                        });
                    }
                });
    }

    private void onUnPairSuccess() {
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

        ToastUtils.showShortToast(R.string.ubt_ubpair_success);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ubt_btn_unpair) {
            showUnpairDialog();
        }
    }
}

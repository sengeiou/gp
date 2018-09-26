package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.content.Intent;
import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :二维码扫描界面，兼顾小猪配对和成员组加入
 *@time          :2018/9/22 16:10
 *@change        :
 *@changetime    :2018/9/22 16:10
*/
public class QRScanActivity extends BaseToolBarActivity {
    private  boolean isPair; //是否小猪配对使用的功能
    @Override
    protected int getConentView() {
        return R.layout.activity_qr_scan;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        changContent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changContent(intent);
    }

    private void changContent(Intent intent){
        if (intent!=null){
            Bundle extras =intent.getExtras();
            if (null!=extras&&extras.containsKey("isPair")){
                isPair=extras.getBoolean("isPair",false);
            }
        }
    }
}

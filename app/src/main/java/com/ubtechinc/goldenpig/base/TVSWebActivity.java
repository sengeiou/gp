package com.ubtechinc.goldenpig.base;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :TVS web页面
 * @time :2019/4/25 18:09
 * @change :
 * @changetime :2019/4/25 18:09
 */
public class TVSWebActivity extends BaseToolBarActivity {

    public static final String TVS_WEB_URL = "TVS_WEB_URL";

    private FragmentTransaction ft;

    private TVSWebFragment tvsWebFragment;

    @Override
    protected int getConentView() {
        return R.layout.activity_fragment_container;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.main_smarthome);

        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        tvsWebFragment = TVSWebFragment.newInstance();
        ft.replace(R.id.fragment_content, tvsWebFragment).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!tvsWebFragment.webGoBack()) {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onReceive(Event event, int code) {
        if (code == EventBusUtil.NETWORK_STATE_CHANGED) {
            tvsWebFragment.onStateRefresh();
        }
    }

}

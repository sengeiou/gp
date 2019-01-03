package com.ubtechinc.goldenpig.personal;

import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :蜂窝移动网络
 * @time :2019/1/3 15:37
 * @change :
 * @changetime :2019/1/3 15:37
 */
public class BeeHiveMobileActivity extends BaseToolBarActivity {


    @Override
    protected int getConentView() {
        return R.layout.activity_beehive_open;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("蜂窝移动网络");
        setTitleBack(true);
    }

}

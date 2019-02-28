package com.ubtechinc.goldenpig.personal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :NOSIM页面
 * @time :2019/1/3 15:37
 * @change :
 * @changetime :2019/1/3 15:37
 */
public class NoSimActivity extends BaseToolBarActivity {

    public static final String KEY_TOOL_BAR_TITLE = "actionbarTitle";

    @Override
    protected int getConentView() {
        return R.layout.activity_no_sim;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String title = "蜂窝移动网络";
        Intent intent = getIntent();
        if (intent != null) {
            String sourceTitle = intent.getStringExtra(KEY_TOOL_BAR_TITLE);
            if (!TextUtils.isEmpty(sourceTitle)) {
                title = sourceTitle;
            }
        }
        setToolBarTitle(title);
        setTitleBack(true);
    }

}

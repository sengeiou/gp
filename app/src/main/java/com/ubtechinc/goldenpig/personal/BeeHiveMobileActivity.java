package com.ubtechinc.goldenpig.personal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

    public static final String KEY_BEE_HIVE_OPEN = "beeHiveOpen";

    private boolean isBeeHiveOpen;
    @Override
    protected int getConentView() {
        return R.layout.activity_beehive_open;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("蜂窝移动网络");
        setTitleBack(true);

        Intent intent = getIntent();
        if (intent != null) {
            isBeeHiveOpen = intent.getBooleanExtra(KEY_BEE_HIVE_OPEN, false);
        }

        ImageView ivMobileIcon = findViewById(R.id.iv_mobile_icon);
        TextView tvMobileValue = findViewById(R.id.tv_mobile_value);
        if (isBeeHiveOpen) {
            ivMobileIcon.setImageResource(R.drawable.ic_mobile_data);
            tvMobileValue.setText(R.string.ubt_is_open);
        } else {
            ivMobileIcon.setImageResource(R.drawable.ic_mobile_data_close);
            tvMobileValue.setText(R.string.ubt_not_open);
        }
    }

}

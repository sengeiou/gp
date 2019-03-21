package com.ubtechinc.goldenpig.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseWebActivity;
import com.ubtechinc.goldenpig.utils.StatusBarWrapUtil;


/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :
 *@time          :2019/3/12 14:13
 *@change        :
 *@changetime    :2019/3/12 14:13
*/
public class CommonWebActivity extends BaseWebActivity {

    /** web地址 */
    public static final String KEY_URL = "KEY_URL";

    /** 是否沉浸状态栏 */
    public static final String KEY_IMMERSE_STATUSBAR = "KEY_IMMERSE_STATUSBAR";

    /** 是否需要actionbar */
    public static final String KEY_NEED_ACTIONBAR = "KEY_NEED_ACTIONBAR";

    /** 深色状态栏图标 */
    public static final String KEY_LIGHT_STATUS_BAR = "KEY_LIGHT_STATUS_BAR";

    private String url;

    private boolean isImmerseStatusbar;

    private boolean isNeedactionbar;

    private boolean isLightStatusbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isImmerseStatusbar) {
            StatusBarWrapUtil.translucentStatusBar(this, true);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
                    .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if (isNeedactionbar) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    protected String getURL() {
        return url;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.ubt_skills_detail;
    }

    @Override
    protected void onGoNextWeb() {
        //TODO 进入web下一级
    }

    @Override
    protected void processWeb() {
        super.processWeb();
        Intent intent = getIntent();
        url = intent.getStringExtra(KEY_URL);
        isImmerseStatusbar = intent.getBooleanExtra(KEY_IMMERSE_STATUSBAR, true);
        isNeedactionbar = intent.getBooleanExtra(KEY_NEED_ACTIONBAR, false);
        isLightStatusbar = intent.getBooleanExtra(KEY_LIGHT_STATUS_BAR, true);
    }

    @Override
    protected void onGoBackWeb() {
        //TODO web回退
    }

    @Override
    protected boolean needActionBar() {
        return isNeedactionbar;
    }

}

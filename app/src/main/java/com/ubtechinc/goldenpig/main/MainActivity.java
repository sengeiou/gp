package com.ubtechinc.goldenpig.main;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.fragment.HouseFragment;
import com.ubtechinc.goldenpig.main.fragment.MainFragmentAdpater;
import com.ubtechinc.goldenpig.main.fragment.PersonalFragment;
import com.ubtechinc.goldenpig.main.fragment.PigFragment;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;


import java.util.ArrayList;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :主页Activity
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<Fragment> fragments;
    private MainFragmentAdpater adapter;
    private ViewPager fragmentPage;
    private RadioButton pigRbtn;
    private RadioButton houseRbtn;
    private RadioButton personRbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        inits();
    }

    private void inits() {
        /*if (AuthLive.getInstance().getCurrentPigList()==null||AuthLive.getInstance().getCurrentPigList().size()<0) {
            GetPigListRepository repository = new GetPigListRepository();
            repository.getUserPigs(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, "", new GetPigListRepository.OnGetPigListLitener() {
                @Override
                public void onError(ThrowableWrapper e) {
                    ActivityRoute.toAnotherActivity(MainActivity.this, SetNetWorkEnterActivity.class,false);
                }

                @Override
                public void onException(Exception e) {
                    ActivityRoute.toAnotherActivity(MainActivity.this, SetNetWorkEnterActivity.class,false);
                }

                @Override
                public void onSuccess(String response) {
                    PigUtils.getPigList(response, AuthLive.getInstance().getUserId(), AuthLive.getInstance().getCurrentPigList());*/
//                    if (AuthLive.getInstance().getCurrentPigList()==null||AuthLive.getInstance().getCurrentPigList().size()<1){
//                        ActivityRoute.toAnotherActivity(MainActivity.this, SetNetWorkEnterActivity.class,false);
//                    }
               /* }
            });
        }*/
        personRbtn = (RadioButton) findViewById(R.id.ubt_rbt_me);
        personRbtn.setOnClickListener(this);

        pigRbtn = (RadioButton) findViewById(R.id.ubt_rbt_pig);
        pigRbtn.setOnClickListener(this);

        houseRbtn = (RadioButton) findViewById(R.id.ubt_rbt_house);
        houseRbtn.setOnClickListener(this);

        fragmentPage = (ViewPager) findViewById(R.id.ubt_pg_main_pager);

        fragments = new ArrayList<>();
        fragments.add(new PigFragment());
        fragments.add(new HouseFragment());
        fragments.add(new PersonalFragment());
        adapter = new MainFragmentAdpater(getSupportFragmentManager(), fragments);
        fragmentPage.setAdapter(adapter);
        fragmentPage.addOnPageChangeListener(new MainViewPagerChangeListener());
        fragmentPage.setCurrentItem(0);
    }

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT); //导航栏颜色也可以正常设置 //
                // window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus; // attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_rbt_pig:
                fragmentPage.setCurrentItem(0);
                break;
            case R.id.ubt_rbt_house:
                fragmentPage.setCurrentItem(1);
                break;
            case R.id.ubt_rbt_me:
                fragmentPage.setCurrentItem(2);
                break;
        }
    }

    private class MainViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    pigRbtn.setChecked(true);
                    break;
                case 1:
                    houseRbtn.setChecked(true);
                    break;
                case 2:
                    personRbtn.setChecked(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

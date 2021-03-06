package com.ubtechinc.goldenpig.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.bottomnavigation.BottomNavigationBar;
import com.bottomnavigation.BottomNavigationItem;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.commlib.utils.StatusBarUtil;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.Constant;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.fragment.PersonalNewFragment;
import com.ubtechinc.goldenpig.main.fragment.PigNewFragment;
import com.ubtechinc.goldenpig.main.fragment.SkillFragment;
import com.ubtechinc.goldenpig.main.fragment.SmartHomeFragment;
import com.ubtechinc.goldenpig.model.JsonCallback;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionModel;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.utils.StatusBarWrapUtil;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :主页Activity
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class MainActivity extends BaseActivity {

    Handler mHander = new Handler();

    public static boolean isNoSim;

    public static boolean isBeeHiveOpen;

    public String pigPhoneNumber;

    private PigNewFragment mHomeFragment;

    private SkillFragment mSkillFragment;

    private SmartHomeFragment mSmartHomeFragment;

    private PersonalNewFragment mMineFragment;

    private FragmentManager mFragmentManager;

    private long mExitTime;

    public static final String HOME_TAG = "home";
    public static final String SMARTHOOME_TAG = "smarthome";
    public static final String SKILL_TAG = "skill";
    public static final String MINE_TAG = "mine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarWrapUtil.translucentStatusBar(this, true);
        StatusBarUtil.setStatusBarTextColor(this, false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
                .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initViews();
        checkInitInterlocution();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    private void initViews() {
        initNavBar();
        preInitFragment();
        initFragment(0);
    }

    private void initNavBar() {
        BottomNavigationBar mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_selected, R.string.ubt_tab_little_pig).setInactiveIconResource(R.drawable.ic_home_normal))
                .addItem(new BottomNavigationItem(R.drawable.ic_smart_home, R.string.ubt_tab_smarthome).setInactiveIconResource(R.drawable.ic_smart_home_gray))
                .addItem(new BottomNavigationItem(R.drawable.ic_skil_selected, R.string.ubt_tab_skill).setInactiveIconResource(R.drawable.ic_skil_normal))
                .addItem(new BottomNavigationItem(R.drawable.ic_me_selected, R.string.ubt_tab_person).setInactiveIconResource(R.drawable.ic_me_normal))
                .setFirstSelectedPosition(0)
                .initialise();
        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                initFragment(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private void preInitFragment() {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mHomeFragment == null) {
            mHomeFragment = BaseFragment.newInstance(PigNewFragment.class);
            fragmentTransaction.add(R.id.main_content, mHomeFragment, HOME_TAG);
        }
        if (mSkillFragment == null) {
            mSkillFragment = BaseFragment.newInstance(SkillFragment.class);
            fragmentTransaction.add(R.id.main_content, mSkillFragment, SKILL_TAG);
        }
        if (mMineFragment == null) {
            mMineFragment = BaseFragment.newInstance(PersonalNewFragment.class);
            fragmentTransaction.add(R.id.main_content, mMineFragment, MINE_TAG);
        }
        if (mSmartHomeFragment == null) {
            mSmartHomeFragment = BaseFragment.newInstance(SmartHomeFragment.class);
            fragmentTransaction.add(R.id.main_content, mSmartHomeFragment, SMARTHOOME_TAG);
        }
        fragmentTransaction.commit();
    }

    private void showOrHideFragment(FragmentTransaction fragmentTransaction, Fragment fragment, boolean isVisibleToUser) {
        if (fragmentTransaction != null && fragment != null) {
            if (isVisibleToUser) {
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.hide(fragment);
            }
            fragment.setUserVisibleHint(isVisibleToUser);
        }
    }

    private void initFragment(int i) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (i) {
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = BaseFragment.newInstance(PigNewFragment.class);
                    fragmentTransaction.add(R.id.main_content, mHomeFragment, HOME_TAG);
                } else {
                    showOrHideFragment(fragmentTransaction, mHomeFragment, true);
                }
                break;
            case 1:
//                LoginProxy proxy = TVSManager.getInstance(this, com.ubtechinc.goldenpig.BuildConfig.APP_ID_WX, com.ubtechinc.goldenpig.BuildConfig.APP_ID_QQ).getProxy();
//                String url = "https://ddsdk.html5.qq.com/smartHome";
//              proxy.tvsRequestUrl(url, null, null, null);
                //ActivityRoute.toAnotherActivity((Activity) this, SmartHomeWebActivity.class, false);
                if (mSmartHomeFragment == null) {
                    mSmartHomeFragment = BaseFragment.newInstance(SmartHomeFragment.class);
                    fragmentTransaction.add(R.id.main_content, mSmartHomeFragment, SMARTHOOME_TAG);
                } else {
                    showOrHideFragment(fragmentTransaction, mSmartHomeFragment, true);
                }
                break;
            case 2:
                if (mSkillFragment == null) {
                    mSkillFragment = BaseFragment.newInstance(SkillFragment.class);
                    fragmentTransaction.add(R.id.main_content, mSkillFragment, SKILL_TAG);
                } else {
                    showOrHideFragment(fragmentTransaction, mSkillFragment, true);
                }
                break;
            case 3:
                if (mMineFragment == null) {
                    mMineFragment = BaseFragment.newInstance(PersonalNewFragment.class);
                    fragmentTransaction.add(R.id.main_content, mMineFragment, MINE_TAG);
                } else {
                    showOrHideFragment(fragmentTransaction, mMineFragment, true);
                }
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        showOrHideFragment(fragmentTransaction, mHomeFragment, false);
        showOrHideFragment(fragmentTransaction, mSkillFragment, false);
        showOrHideFragment(fragmentTransaction, mMineFragment, false);
        showOrHideFragment(fragmentTransaction, mSmartHomeFragment, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        disConnectBle();
    }

    private void disConnectBle() {
        getWindow().getDecorView().postDelayed(() -> UbtBluetoothManager.getInstance().closeConnectBle(), 5000);
        PigInfo myPig = AuthLive.getInstance().getCurrentPig();
        if (myPig != null && myPig.isAdmin) {
            UbtTIMManager.getInstance().queryNativeInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //TODO 实现只在冷启动时显示启动页，即点击返回键与点击HOME键退出效果一致
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((SystemClock.elapsedRealtime() - mExitTime) > 2000) {
                ToastUtils.showShortToast(this, getString(R.string.click_again_exit));
                mExitTime = SystemClock.elapsedRealtime();
            } else {
//                System.exit(0);
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void checkInitInterlocution() {
        if (!SPUtils.get().getBoolean(Constant.SP_ADDED_INIT_INTERLOCUTION, false)) {
            InterlocutionModel requestModel = new InterlocutionModel();
            requestModel.addInterlocutionRequest("谁是我的宝宝", "当然是八戒宝宝我啦。", new
                    JsonCallback<String>(String.class) {
                        @Override
                        public void onSuccess(String reponse) {
                            mHander.post(new Runnable() {
                                @Override
                                public void run() {
                                    SPUtils.get().put(Constant.SP_ADDED_INIT_INTERLOCUTION, true);
                                }
                            });
                        }

                        @Override
                        public void onError(String str) {
                            if (str.contains("问句重复")) {
                                SPUtils.get().put(Constant.SP_ADDED_INIT_INTERLOCUTION, true);
                            }
                        }
                    });
        }
    }

}

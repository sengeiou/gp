package com.ubtechinc.goldenpig.base;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ubtech.utilcode.utils.BarUtils.getStatusBarHeight;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :基础Fragment
 * @time :2018/8/17 17:58
 * @change :
 * @changTime :2018/8/17 17:58
 */
public abstract class BaseFragment extends Fragment {
    private ViewGroup mView;
    protected View mStatusBarView;
    private Unbinder unbinder;
    protected View mTipsView;
    protected TextView mTipsClickView;
    protected TextView mTipsTv;

    public BaseFragment() {
        super();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }*/
        unbinder = ButterKnife.bind(this, view);
        mTipsView = view.findViewById(R.id.ubt_layout_tips);
        mTipsClickView = view.findViewById(R.id.ubt_bind_tv);
        mTipsTv = view.findViewById(R.id.ubt_tv_main_tips);

    }

    protected void showTips() {
        if (mTipsView != null) {
            final PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo == null) {
                mTipsView.setVisibility(View.VISIBLE);
                onNoPig();
                mTipsTv.setText(R.string.ubt_unbund_pig);
                if (mTipsClickView != null) {
                    mTipsClickView.setText(R.string.ubt_click_for_bind);
                }
                if (mTipsClickView != null) {
                    mTipsClickView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tipsClickForBind();
                        }
                    });
                }
            } else {
                mTipsView.setVisibility(View.GONE);
                onHasPig();
            }
        }
    }

    /**
     * 获取用户是否绑定小猪或小猪是否有联网
     */
    protected int getUsetPigState() {

        return 0;
    }

    protected void tipsClickForBind() {
        if (AuthLive.getInstance().getCurrentPig() == null) {
            HashMap<String, Boolean> params = new HashMap<>();
            params.put("back", false);
            params.put("skip", true);
            ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, params, false);
        }
    }

    protected void tipsClickForNet() {
        HashMap<String, Boolean> params = new HashMap<>();
        params.put("back", false);
        params.put("skip", true);
        ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, params, false);
    }

    /**
     * @return :
     * @throws :
     * @auther :hqt
     * @description :设置状态栏背景色
     * @parma :
     */
    protected void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            //如果为全透明模式，取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置状态栏为透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            //设置window的状态栏不可见
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            //如果为半透明模式，添加设置Window半透明的Flag
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    protected void addStatusBar() {
        if (mStatusBarView == null) {
            mStatusBarView = new View(getContext());
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int statusBarHeight = getStatusBarHeight(getActivity());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth, statusBarHeight);
            mStatusBarView.setLayoutParams(params);
            mStatusBarView.requestLayout();
            if (mView != null) {
                mView.addView(mStatusBarView, 0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        //checkPigWifi();
        MobclickAgent.onPageStart(this.getClass().getSimpleName()); //统计页面("MainScreen"为页面名称，可自定义)
        showTips();
    }

    private void checkPigWifi() {
        String message = new JsonCommandProduce().getPigNetWorkState();
        UbtBluetoothManager.getInstance().sendMessageToBle(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected abstract void onNoPig();

    protected abstract void onNoSetNet();

    protected abstract void onHasPig();

    protected abstract void onSetedNet();
}

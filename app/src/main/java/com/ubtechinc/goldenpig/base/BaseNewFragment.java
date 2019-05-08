package com.ubtechinc.goldenpig.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.view.StateView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hht on 2018/2/24 0024.
 */

public abstract class BaseNewFragment extends Fragment {
    protected boolean isVisible = false;//当前Fragment是否可见
    protected boolean isInitView = false;//是否与View建立起映射关系
    protected boolean isFirstLoad = true;//是否是第一次加载数据
    Unbinder unbinder;
    private View convertView;
//    private SparseArray<View> mViews;

    /**
     * @return fragment布局
     */
    protected abstract int getLayoutId();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void lazyLoadData();

    public View getConvertView() {
        return convertView;
    }

    private void loadFristData() {
        if (isInitView && isVisible && isFirstLoad) {
            isFirstLoad = false;
            lazyLoadData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        convertView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, convertView);
        initView();
        isInitView = true;
        loadFristData();
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        return convertView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            isVisible = true;
            loadFristData();
        } else {
            isVisible = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event != null) {
            onReceiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyMessageEvent(Event event) {
        if (event != null) {
            onReceiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void onReceiveEvent(Event event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void onReceiveStickyEvent(Event event) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName()); //统计页面("MainScreen"为页面名称，可自定义)
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    protected StateView mStateView;

    public void initStateView(Boolean hasActionbar) {
        mStateView = StateView.inject(getActivity(), hasActionbar);
    }

    public void initStateView(View view, Boolean hasActionbar) {
        mStateView = StateView.inject(view, hasActionbar);
    }
}

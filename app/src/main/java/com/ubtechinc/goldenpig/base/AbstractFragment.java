package com.ubtechinc.goldenpig.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tqzhang.stateview.core.LoadManager;
import com.tqzhang.stateview.stateview.BaseStateControl;
import com.ubtechinc.goldenpig.stateview.LoadingState;

/**
 *@auther        :zzj
 *@email         :zhijun.zhou@ubtrobot.com
 *@Description:  :统一基类Fragment
 *@time          :2019/4/26 10:51
 *@change        :
 *@changetime    :2019/4/26 10:51
*/
public abstract class AbstractFragment extends Fragment {

    private View rootView;

    protected FragmentActivity activity;

    protected LoadManager loadManager;

    protected boolean mIsFirstVisible = true;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), null, false);
        loadManager = new LoadManager.Builder()
                .setViewParams(rootView)
                .setListener(new BaseStateControl.OnRefreshListener() {
                    @Override
                    public void onRefresh(View view) {
                        onStateRefresh();
                    }
                })
                .build();
        initViews(savedInstanceState);
        return loadManager.getLoadLayout();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean isVis = isHidden() || getUserVisibleHint();
        if (isVis && mIsFirstVisible) {
            lazyLoad();
            mIsFirstVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.activity = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    /**
     * 当界面可见时的操作
     */
    protected void onVisible() {
        if (mIsFirstVisible && isResumed()) {
            lazyLoad();
            mIsFirstVisible = false;
        }
    }

    /**
     * 当界面不可见时的操作
     */
    protected void onInVisible() {

    }

    /**
     * 数据懒加载
     */
    protected void lazyLoad() {

    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化views
     *
     * @param savedInstanceState
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 状态刷新
     */
    protected void onStateRefresh() {
        //TODO
        loadManager.showStateView(LoadingState.class);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int id) {
        return (T) rootView.findViewById(id);
    }

}

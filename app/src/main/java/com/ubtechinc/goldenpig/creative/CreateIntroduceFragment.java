package com.ubtechinc.goldenpig.creative;


import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseNewFragment;

public class CreateIntroduceFragment extends BaseNewFragment {
    private static final String TAG = CreateIntroduceFragment.class.getSimpleName();
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fra_create_introduce;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void lazyLoadData() {

    }
}

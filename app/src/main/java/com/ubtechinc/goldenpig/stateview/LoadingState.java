package com.ubtechinc.goldenpig.stateview;

import com.tqzhang.stateview.stateview.BaseStateControl;
import com.ubtechinc.goldenpig.R;

/**
 * @Description: 加载中状态
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/4 19:51
 */
public class LoadingState extends BaseStateControl {

    @Override
    protected int onCreateView() {
        return R.layout.common_loading_view;
    }
}

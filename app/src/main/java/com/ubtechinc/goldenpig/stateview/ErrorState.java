package com.ubtechinc.goldenpig.stateview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tqzhang.stateview.stateview.BaseStateControl;
import com.ubtechinc.goldenpig.R;

/**
 * @Description: 加载错误状态
 * @Author: zhijunzhou
 * @CreateDate: 2019/4/4 19:53
 */
public class ErrorState extends BaseStateControl {

    @Override
    protected int onCreateView() {
        return R.layout.common_error_view;
    }

    @Override
    protected void onViewCreate(Context context, View view) {
        TextView errorDesc = view.findViewById(R.id.tv_error_desc);
//        ImageView errorIcon = view.findViewById(R.id.iv_error_icon);
        if (view.getTag() != null) {
            if (view.getTag().equals("1")) {
                errorDesc.setText("网络不给力～_~");
//                errorIcon.setImageResource(R.mipmap.empty_network);
            } else if (view.getTag().equals("2")) {
                errorDesc.setText("服务器异常");
//                errorIcon.setImageResource(R.mipmap.empty_server);
            }

        }
    }

}

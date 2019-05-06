package com.ubtechinc.goldenpig.children;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.robot.dmsdk.TVSWrapBridge;
import com.ubt.robot.dmsdk.TVSWrapConstant;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;

import org.json.JSONException;
import org.json.JSONObject;

public class ChildrenActivity extends BaseToolBarActivity {

    private static final String TAG = ChildrenActivity.class.getSimpleName();

    ImageView iv_protect;
    TextView tv_child_mode_state;
    RelativeLayout rl_child_off;
    RelativeLayout rl_child_on;


    @Override
    protected int getConentView() {
        return R.layout.activity_children;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("儿童");
        setTitleBack(true);
        initView();
        if (getChildMode()) {
            openChildState();
        } else {
            closeChildState();
        }
    }

    private void initView() {
        iv_protect = (ImageView) findViewById(R.id.iv_protect);
        tv_child_mode_state = (TextView) findViewById(R.id.tv_child_mode_state);
        rl_child_off = (RelativeLayout) findViewById(R.id.rl_child_off);
        rl_child_on = (RelativeLayout) findViewById(R.id.rl_child_on);

        rl_child_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open child mode
                setChildMode(1);

            }
        });

        rl_child_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //off child mode
                setChildMode(0);

            }
        });
    }

    private void openChildState() {
        iv_protect.setVisibility(View.VISIBLE);
        rl_child_on.setVisibility(View.VISIBLE);
        rl_child_off.setVisibility(View.INVISIBLE);
        tv_child_mode_state.setText(R.string.child_mode_open);
        tv_child_mode_state.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));

    }

    private void closeChildState() {
        iv_protect.setVisibility(View.INVISIBLE);
        rl_child_off.setVisibility(View.VISIBLE);
        rl_child_on.setVisibility(View.INVISIBLE);
        tv_child_mode_state.setText(R.string.child_mode_close);
        tv_child_mode_state.setTextColor(getResources().getColor(R.color.txt_child_close));
    }


    private void setChildMode(int mode) {
        TVSWrapBridge.tvsSetChildMode(TVSWrapBridge.setChildModeBlobInfo(mode), TVSWrapConstant.PRODUCT_ID,
                AuthLive.getInstance().getCurrentPig().getRobotName(), new TVSWrapBridge.TVSWrapCallback<String>() {
                    @Override
                    public void onError(int errCode) {
                        UbtLogger.e(TAG, "onError errCode:" + errCode);
                        ToastUtils.showShortToast("儿童模式切换失败");
                    }

                    @Override
                    public void onSuccess(String result) {
                        UbtLogger.d(TAG, "onSuccess result:" + result);
                        if (mode == 1) {
                            openChildState();
                        } else {
                            closeChildState();
                        }
                    }
                });
    }


    private boolean isChildMode = false;

    private boolean getChildMode() {
        TVSWrapBridge.tvsGetChildMode(TVSWrapBridge.getChildModeBlobInfo(), TVSWrapConstant.PRODUCT_ID,
                AuthLive.getInstance().getCurrentPig().getRobotName(), new TVSWrapBridge.TVSWrapCallback<String>() {
                    @Override
                    public void onError(int errCode) {
                        UbtLogger.e(TAG, "onError errCode:" + errCode);
                    }

                    @Override
                    public void onSuccess(String result) {
                        UbtLogger.d(TAG, "onSuccess result:" + result);
                        try {
                            JSONObject obj = new JSONObject(result);
                            JSONObject childModeInfo = obj.getJSONObject("childModeInfo");
                            isChildMode = childModeInfo.optBoolean("isChildMode");
                            UbtLogger.d(TAG, "childModeInfo:" + isChildMode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

}

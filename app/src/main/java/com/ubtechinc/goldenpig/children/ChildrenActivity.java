package com.ubtechinc.goldenpig.children;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.ai.tvs.comm.CommOpInfo;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.comm.widget.LoadingDialog;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.personal.alarm.AddAlarmActivity;
import com.ubtechinc.goldenpig.utils.PigUtils;
import com.ubtechinc.goldenpig.utils.TvsUtil;
import com.ubtechinc.tvlloginlib.TVSManager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.SET_ALARM_SUCCESS;

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
        if(getChildMode()){
            openChildState();
        }else{
            closeChildState();
        }
    }

    private void initView(){
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

    private void openChildState(){
        iv_protect.setVisibility(View.VISIBLE);
        rl_child_on.setVisibility(View.VISIBLE);
        rl_child_off.setVisibility(View.INVISIBLE);
        tv_child_mode_state.setText(R.string.child_mode_open);
        tv_child_mode_state.setTextColor(getResources().getColor(R.color.ubt_tab_btn_txt_checked_color));

    }

    private void closeChildState(){
        iv_protect.setVisibility(View.INVISIBLE);
        rl_child_off.setVisibility(View.VISIBLE);
        rl_child_on.setVisibility(View.INVISIBLE);
        tv_child_mode_state.setText(R.string.child_mode_close);
        tv_child_mode_state.setTextColor(getResources().getColor(R.color.txt_child_close));
    }


    private void setChildMode(int mode){
        ELoginPlatform platform;
        if (CookieInterceptor.get().getThridLogin().getLoginType().toLowerCase().equals("wx")) {
            platform = ELoginPlatform.WX;
        } else {
            platform = ELoginPlatform.QQOpen;
        }

        TVSManager tvsManager = TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ);
        tvsManager.init(this);
        tvsManager.requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils
                .setChildMode(mode), new TVSManager.TVSAlarmListener() {
            @Override
            public void onSuccess(CommOpInfo msg) {
                UbtLogger.d(TAG, "onSuccess msg:" + msg.toString());
                if(TextUtils.isEmpty(msg.errMsg)){
                    if(mode == 1){
                        openChildState();
                    }else{
                        closeChildState();
                    }
                }else{
                    ToastUtils.showShortToast("儿童模式切换失败");
                }

            }

            @Override
            public void onError(String str) {
                UbtLogger.e(TAG, "onError str:" + str);
                ToastUtils.showShortToast("儿童模式切换失败");
            }
        });

    }


    private boolean isChildMode = false;
    private boolean getChildMode(){
        ELoginPlatform platform = TvsUtil.currentPlatform();
        TVSManager tvsManager = TVSManager.getInstance(this, BuildConfig.APP_ID_WX, BuildConfig.APP_ID_QQ);
        tvsManager.init(this);
        tvsManager.requestTskmUniAccess(platform, PigUtils.getAlarmDeviceMManager(), PigUtils
                .getChildMode(), new TVSManager.TVSAlarmListener() {
            @Override
            public void onSuccess(CommOpInfo msg) {
                UbtLogger.d(TAG, "onSuccess msg:" + msg.toString());
                String str = msg.errMsg;
                try {
                    JSONObject obj = new JSONObject(str);
                    JSONObject childModeInfo = obj.getJSONObject("childModeInfo");
                    isChildMode = childModeInfo.optBoolean("isChildMode");
                    UbtLogger.d(TAG, "childModeInfo:" + isChildMode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String str) {
                UbtLogger.e(TAG, "onError str:" + str);
                ToastUtils.showShortToast("获取儿童模式状态数据异常");
            }
        });

        return isChildMode;

    }




}

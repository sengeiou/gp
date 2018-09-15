package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :我的小猪页面
 *@time          :2018/9/15 12:48
 *@change        :
 *@changetime    :2018/9/15 12:48
*/
public class MyPigActivity extends BaseToolBarActivity {
    @BindView(R.id.ubt_btn_dev_update)
    Button mDevUpateBtn;        //升级按钮
    @BindView(R.id.ubt_btn_unbind)
    Button mUnBindBtn;          //解除绑定按钮
    @BindView(R.id.ubt_img_pig_state)
    ImageView mPigStateImg;    // 小猪在线状态图标


    @BindView(R.id.ubt_tv_version)
    TextView mPigVersionTv;
    @BindView(R.id.ubt_tv_searialno)
    TextView mSearialNoTv;
    private PigInfo mPig;
    @Override
    protected int getConentView() {
        return R.layout.activity_my_pig;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitleBack(true);
        setToolBarTitle(R.string.my_pig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPig= AuthLive.getInstance().getCurrentPig();
        showPigNo();
    }

    @OnClick({R.id.ubt_btn_dev_update,R.id.ubt_btn_unbind})
    public void onClik(View view){
        switch (view.getId()){
            case R.id.ubt_btn_dev_update:
                toDeviceUpdate();
                break;
            case R.id.ubt_btn_unbind:
                showComfireDialog();
                break;
        }
    }

    /*显示确认对转权限话框*/
    private void showComfireDialog(){
        UBTBaseDialog dialog=new UBTBaseDialog(this);
        dialog.setTips(getString(R.string.ubt_unbing_tips));
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(),R.color.ubt_tab_btn_txt_checked_color,null));
        dialog.show();
    }
    private void showPigNo(){
        if (mPig!=null){
            mSearialNoTv.setText(String.format(getString(R.string.ubt_pig_serialno),mPig.getRobotName()));
            if (mPig.isAdmin&&mPig.isMaster()){
                mPigVersionTv.setVisibility(View.VISIBLE);
                mPigVersionTv.setText(String.format(getString(R.string.ubt_pig_version_format), ContextUtils.getVerName(this)));
            }else {
                mPigVersionTv.setVisibility(View.GONE);
            }
        }

    }
    private void toDeviceUpdate(){
        if (mPig!=null &&mPig.isAdmin&&mPig.isMaster()){
            ActivityRoute.toAnotherActivity(this,DeviceUpdateActivity.class,false);
        }
    }

}

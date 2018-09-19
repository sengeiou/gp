package com.ubtechinc.goldenpig.pigmanager.hotspot;

import android.os.Binder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.comm.widget.UbtEditDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :设置WIFI热点
 *@time          :2018/9/18 11:10
 *@change        :
 *@changetime    :2018/9/18 11:10
*/
public class SetHotSpotActivity extends BaseToolBarActivity {
    @BindView(R.id.ubt_btn_hotspot_name)
    UbtSubTxtButton mHotspotNameBtn;
    @BindView(R.id.ubt_btn_hotspot_pwd)
    UbtSubTxtButton mHotspotPwdBtn;

    private UbtEditDialog dialog;
    @Override
    protected int getConentView() {
        return R.layout.activity_set_wifi_hotspot;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_device_manger);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.ubt_btn_hotspot_pwd,R.id.ubt_btn_hotspot_name})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.ubt_btn_hotspot_name:
                showUpdatNameDialog();
                break;
            case R.id.ubt_btn_hotspot_pwd:
                showUpdatePwdDialog();
                break;
        }
    }

    /*显示修改热点名称的对话框*/
    private void showUpdatNameDialog(){
        initDialog();
        dialog.setTipsTxt(mHotspotNameBtn.getText().toString());
        dialog.setRawTxt(mHotspotNameBtn.getRightText());
        dialog.setOnEnterClickListener(new UbtEditDialog.OnEnterClickListener() {
            @Override
            public void onEnterClick(View view, String newStr) {
                if (mHotspotNameBtn!=null) {
                    mHotspotNameBtn.setRightText(newStr);
                }
            }
        });
        dialog.show();
    }
    /*
    * 显示修改热点密码对话框
    * */
    private void showUpdatePwdDialog(){
        initDialog();
        dialog.setTipsTxt(mHotspotPwdBtn.getText().toString());
        dialog.setRawTxt(mHotspotPwdBtn.getRightText());
        dialog.setOnEnterClickListener(new UbtEditDialog.OnEnterClickListener() {
            @Override
            public void onEnterClick(View view, String newStr) {
                if (mHotspotPwdBtn!=null){
                    mHotspotPwdBtn.setRightText(newStr);
                }

            }
        });
        dialog.show();
    }
    private void initDialog(){
        dialog=new UbtEditDialog(this);

    }
}

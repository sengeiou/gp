package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.ai.tvs.zxing.util.ZXingUtil;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.pigmanager.register.GetAddMemberQRHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :添加成员界面
 * @time :2018/9/21 17:39
 * @change :
 * @changetime :2018/9/21 17:39
 */
public class QRCodeActivity extends BaseToolBarActivity implements View.OnClickListener {
    private ImageView mQRImg; //二维码
    private int mQRSize;
    private long mQRClickTime;
    private boolean isPair; //用于区分两种小猪配对和添加成员功能，显示不同文字或导航栏按钮
    @Override
    protected int getConentView() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        initViews();
        initContent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent!=null){
            initContent(intent);
        }
    }

    private void initViews() {
        mQRImg = findViewById(R.id.ubt_img_qrcode);
        mQRImg.setOnClickListener(this);

        mQRSize = getResources().getDimensionPixelOffset(R.dimen.dp_250);
    }
    private void initContent(Intent intent){
        if (intent!=null){
            Bundle extras =intent.getExtras();
            if (null!=extras&&extras.containsKey("isPair")){
                isPair=extras.getBoolean("isPair",false);
            }
        }
        if (isPair){
            setToolBarTitle(R.string.ubt_pair_pig);
            ((TextView)findViewById(R.id.ubt_tv_qrcode_sub_title)).setText(R.string.ubt_pair_pig);
            ((TextView)findViewById(R.id.ubt_tv_qrcode_desc)).setText(R.string.ubt_pair_pig_desc);
            mToolbarRightBtn=findViewById(R.id.ubt_imgbtn_add);
            mToolbarRightBtn.setImageResource(R.drawable.ic_add); ///暂时使用这个图标，目前还没有图标
            mToolbarRightBtn.setVisibility(View.VISIBLE);
            mToolbarRightBtn.setOnClickListener(this);
        }else {
            setToolBarTitle(R.string.ubt_add_member);
            ((TextView)findViewById(R.id.ubt_tv_qrcode_sub_title)).setText(R.string.ubt_add_member);
            ((TextView)findViewById(R.id.ubt_tv_qrcode_desc)).setText(R.string.ubt_addmember_desc);
            findViewById(R.id.ubt_imgbtn_add).setVisibility(View.GONE);
            createAddMemberQR();
        }
    }
    private void showQRErrorToast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showLongToast("生成二维码失败，请点击重试");
            }
        });
    }
    private void createAddMemberQR() {
        GetAddMemberQRHttpProxy httpProxy = new GetAddMemberQRHttpProxy();
        httpProxy.getMemberQR(CookieInterceptor.get().getToken(), BuildConfig.APP_ID, new GetAddMemberQRHttpProxy.GetMemberQRCallBack() {
            @Override
            public void onError(String error) {

                showQRErrorToast();
            }

            @Override
            public void onSuccess(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("sign")) {
                            if (mQRImg != null) {
                                mQRImg.setImageBitmap(ZXingUtil.createQRImage(jsonObject.getString("sign"), mQRSize, mQRSize));
                            }
                        } else {
                            showQRErrorToast();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showQRErrorToast();
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_img_qrcode:
                if (mQRClickTime == 0 || System.currentTimeMillis() - mQRClickTime > 5000) {
                    mQRClickTime = System.currentTimeMillis();
                    createAddMemberQR();
                } else {
                    ToastUtils.showLongToast("生成二维码过于频繁，请稍后重试");
                }

                break;
            case R.id.ubt_imgbtn_add:
                ActivityRoute.toAnotherActivity(this,QRScanActivity.class,false);
                break;
        }

    }
}

package com.ubtechinc.goldenpig.me;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ubtechinc.commlib.utils.ToastUtils;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.login.repository.UBTAuthRepository;
import com.ubtechinc.goldenpig.route.ActivityRoute;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   : 个人信息页
 *@time          :2018/9/10 11:03
 *@change        :
 *@changetime    :2018/9/10 11:03
*/
public class UserInfoActivity extends BaseToolBarActivity implements View.OnClickListener,UBTAuthRepository.UBTAuthCallBack{
    private ImageView mPhotoImg; //头像
    private UbtSubTxtButton mUserNameBtn; //显示用户昵称
    private UserInfo mUser;
    private View mLogoutBtn;
    private  UBTAuthRepository ubtAuthRepository ;
    @Override
    protected int getConentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarBackground(0xF5F8FB);
        setToolBarTitle(getString(R.string.ubt_user_info));
        setTitleBack(true);
        ubtAuthRepository= new UBTAuthRepository();
        iniViews();
    }

    private void  iniViews(){
        mPhotoImg=(ImageView)findViewById(R.id.ubt_img_photo);
        mUserNameBtn=(UbtSubTxtButton)findViewById(R.id.ubt_btn_user_name);

        mLogoutBtn=findViewById(R.id.ubt_btn_logout);
        mLogoutBtn.setOnClickListener(this);

        AuthLive authLive=AuthLive.getInstance();
        mUser=authLive.getCurrentUser();
        if (mUser!=null){
            if (!TextUtils.isEmpty(mUser.getNickName())){
                mUserNameBtn.setRightText(mUser.getNickName());
            }
            if (!TextUtils.isEmpty(mUser.getUserImage())){
                Glide.with(this).load(mUser.getUserImage()).centerCrop().transform(new GlideCircleTransform(this)).into(mPhotoImg);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_btn_logout:

                ubtAuthRepository.logout(this);
                break;
        }
    }

    @Override
    public void onSuccess(UserInfo userInfo) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onLogout() {
        new LoginModel().logoutTVS();
        AuthLive.getInstance().logout();
        ActivityRoute.toAnotherActivity(this, LoginActivity.class,true);
    }

    @Override
    public void onLogoutError() {
        ToastUtils.showShortToast(this,"退出登录失败");
    }
}

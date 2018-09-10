package com.ubtechinc.goldenpig.me;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.login.observable.AuthLive;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   : 个人信息页
 *@time          :2018/9/10 11:03
 *@change        :
 *@changetime    :2018/9/10 11:03
*/
public class UserInfoActivity extends BaseToolBarActivity {
    private ImageView mPhotoImg; //头像
    private UbtSubTxtButton mUserNameBtn; //显示用户昵称
    private UserInfo mUser;
    @Override
    protected int getConentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarBackground(0xF5F8FB);
        setToolBarTitle(getString(R.string.ubt_user_info));
        setTitleBack(true);
        iniViews();
    }

    private void  iniViews(){
        mPhotoImg=(ImageView)findViewById(R.id.ubt_img_photo);
        mUserNameBtn=(UbtSubTxtButton)findViewById(R.id.ubt_btn_user_name);
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
}

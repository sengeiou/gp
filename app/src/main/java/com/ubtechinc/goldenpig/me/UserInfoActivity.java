package com.ubtechinc.goldenpig.me;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.comm.widget.UBTBaseDialog;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.LoginModel;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.login.repository.UBTAuthRepository;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.TvsUtil;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description : 个人信息页
 * @time :2018/9/10 11:03
 * @change :
 * @changetime :2018/9/10 11:03
 */
public class UserInfoActivity extends BaseToolBarActivity implements View.OnClickListener, UBTAuthRepository.UBTAuthCallBack {

    private ImageView mPhotoImg;

    private TextView mUserNameTv;

    private TextView mUserAccountTv;

    private View mLogoutBtn;

    private UBTAuthRepository ubtAuthRepository;

    @Override
    protected int getConentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarBackground(0xF5F8FB);
        setToolBarTitle(getString(R.string.ubt_user_info));
        setTitleBack(true);
        ubtAuthRepository = new UBTAuthRepository();
        iniViews();
    }

    private void iniViews() {
        mPhotoImg = findViewById(R.id.ubt_img_photo);
        mUserNameTv = findViewById(R.id.ubt_user_name);
        mUserAccountTv = findViewById(R.id.ubt_user_account);

        mLogoutBtn = findViewById(R.id.ubt_btn_logout);
        mLogoutBtn.setOnClickListener(this);

        fillAccountView();
    }

    private void fillAccountView() {
        UserInfo currentUser = AuthLive.getInstance().getCurrentUser();
        if (currentUser != null) {
            mUserAccountTv.setText(TvsUtil.currentPlatformValue());
            String nickName = SharedPreferencesUtils.getString(this, "tvs_nickName", "");
            String headImgUrl = SharedPreferencesUtils.getString(this, "tvs_headImgUrl", "");
            if (TextUtils.isEmpty(nickName)) {
                nickName = currentUser.getNickName();
            }
            if (TextUtils.isEmpty(headImgUrl)) {
                headImgUrl = currentUser.getUserImage();
            }

            Glide.with(this)
                    .load(headImgUrl)
                    .asBitmap()
                    .centerCrop()
                    .transform(new GlideCircleTransform(this))
                    .placeholder(R.drawable.ic_sign_in)
                    .into(mPhotoImg);

            mUserNameTv.setText(StringUtils.utf8ToString(nickName));
        } else {
            ActivityManager.getInstance().popAllActivityExcept(LoginActivity.class.getName());
            ActivityRoute.toAnotherActivity(this, LoginActivity.class, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_logout:
                showLogoutDialog();

                break;
            default:
        }
    }

    private void showLogoutDialog() {
        UBTBaseDialog dialog = new UBTBaseDialog(this);
        dialog.setTips(getString(R.string.logout_tip));
        dialog.setLeftButtonTxt(getString(R.string.ubt_cancel));
        dialog.setRightButtonTxt(getString(R.string.ubt_enter));
        dialog.setRightBtnColor(ResourcesCompat.getColor(getResources(), R.color.ubt_tab_btn_txt_checked_color, null));
        dialog.setOnUbtDialogClickLinsenter(new UBTBaseDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                ubtAuthRepository.logout(UserInfoActivity.this);
            }
        });
        dialog.show();
    }

    private void doLogout() {
        new LoginModel().logoutTVS();
        AuthLive.getInstance().logout();
        ActivityManager.getInstance().popAllActivity();
        ActivityRoute.toAnotherActivity(this, LoginActivity.class, true);
    }


    @Override
    public void onSuccess(UserInfo userInfo) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onLogout() {
        doLogout();
    }

    @Override
    public void onLogoutError() {
        doLogout();
    }
}

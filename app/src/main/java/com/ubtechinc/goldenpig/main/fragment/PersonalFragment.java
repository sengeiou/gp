package com.ubtechinc.goldenpig.main.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.commlib.view.CircleImageView;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.about.UbtAboutActivtiy;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.feedback.FeedBackActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.me.UserInfoActivity;
import com.ubtechinc.goldenpig.personal.DeviceManageActivity;
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.hotspot.SetHotSpotActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :  个人信息页Fragment
 * @time :2018/8/17 17:58
 * @change :
 * @changTime :2018/8/17 17:58
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {


    private View mToUserInfo;
    private View mSetNetBtn;   //绑定配网按钮
    @BindView(R.id.ubt_btn_person_hotspot)
    View mToHospotBtn;
    private View mFeedBackBtn; //反馈帮助
    private UbtSubTxtButton mAboutBtn; //关于页按钮
    private ImageView mPohtoImg;
    private TextView mNikenameTv;

    private TextView mTitle;
    private View mCyanBg;  /// 蓝色渐进色背景板
    public PersonalFragment() {
        super();
    }

    @Override
    protected void onNoPig() {
        if (mCyanBg!=null){
            mCyanBg.setVisibility(View.GONE);
        }
        if (mTitle!=null){
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(),R.color.ubt_tips_txt_color,null));
        }
    }

    @Override
    protected void onNoSetNet() {
        if (mCyanBg!=null){
            mCyanBg.setVisibility(View.GONE);
        }
        if (mTitle!=null){
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(),R.color.ubt_tips_txt_color,null));
        }
    }

    @Override
    protected void onHasPig() {
        if (mCyanBg!=null){
            mCyanBg.setVisibility(View.VISIBLE);
        }
        if (mTitle!=null){
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(),R.color.ubt_white,null));
        }
    }

    @Override
    protected void onSetedNet() {
        if (mCyanBg!=null){
            mCyanBg.setVisibility(View.VISIBLE);
        }
        if (mTitle!=null){
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(),R.color.ubt_white,null));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inits();
    }


    private void inits() {
        if (AuthLive.getInstance().getCurrentUser() != null) {
            mPohtoImg = (ImageView) getActivity().findViewById(R.id.ubt_img_me_photo);
            Glide.with(getActivity())
                    .load(AuthLive.getInstance().getCurrentUser().getUserImage())
                    .asBitmap()
                    .centerCrop()
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mPohtoImg);

            mNikenameTv = (TextView) getActivity().findViewById(R.id.ubt_tv_me_nikename);
            mNikenameTv.setText(AuthLive.getInstance().getCurrentUser().getNickName());
        }
        mTitle=getActivity().findViewById(R.id.ubt_me_fragment_title);
        mCyanBg=getActivity().findViewById(R.id.ubt_me_normal_bg);
        mToUserInfo = getActivity().findViewById(R.id.ubt_btn_go_login);
        mToUserInfo.setOnClickListener(this);

        mSetNetBtn = getActivity().findViewById(R.id.ubt_btn_person_set_wifi);
        mSetNetBtn.setOnClickListener(this);

        mFeedBackBtn = getActivity().findViewById(R.id.ubt_btn_person_feedback);
        mFeedBackBtn.setOnClickListener(this);

        mAboutBtn = (UbtSubTxtButton) getActivity().findViewById(R.id.ubt_btn_person_about);
        mAboutBtn.setOnClickListener(this);

        getActivity().findViewById(R.id.ubt_btn_person_clock).setOnClickListener(this);

        try {
            String versionName = String.format(getString(R.string.ubt_version_format),
                    ContextUtils.getVerName(getContext()));
            mAboutBtn.setRightText(versionName);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        getActivity().findViewById(R.id.ubt_btn_device_manager).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityRoute.toAnotherActivity(getActivity(), DeviceManageActivity.class, false);
            }
        });
    }

    @Override
    @OnClick(R.id.ubt_btn_person_hotspot)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_btn_go_login:
                ActivityRoute.toAnotherActivity(getActivity(), UserInfoActivity.class, false);
                break;
            case R.id.ubt_btn_person_set_wifi:
                HashMap<String, Boolean> params = new HashMap<>();
                params.put("back", true);
                params.put("skip", false);
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class,
                        params,
                        false);
                break;
            case R.id.ubt_btn_person_hotspot:
                if (AuthLive.getInstance().getCurrentPig()!=null) {
                    ActivityRoute.toAnotherActivity(getActivity(), SetHotSpotActivity.class,
                            false);
                }
                break;
            case R.id.ubt_btn_person_feedback:
                ActivityRoute.toAnotherActivity(getActivity(), FeedBackActivity.class, false);
                break;
            case R.id.ubt_btn_person_about:
                ActivityRoute.toAnotherActivity(getActivity(), UbtAboutActivtiy.class, false);
                break;
            case R.id.ubt_btn_person_clock:
                ActivityRoute.toAnotherActivity(getActivity(), AlarmListActivity.class, false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

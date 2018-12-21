package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.about.UbtAboutActivtiy;
import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.feedback.FeedBackActivity;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.QQMusicWebActivity;
import com.ubtechinc.goldenpig.me.UserInfoActivity;
import com.ubtechinc.goldenpig.personal.DeviceManageActivity;
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionActivity;
import com.ubtechinc.goldenpig.personal.remind.RemindActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.hotspot.SetHotSpotActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.tvlloginlib.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :  个人信息页Fragment
 * @time :2018/8/17 17:58
 * @change :
 * @changTime :2018/8/17 17:58
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {


    private View mSetNetBtn;

    private View mFeedBackBtn; //反馈帮助
    private UbtSubTxtButton mAboutBtn; //关于页按钮
    private ImageView mPohtoImg;
    private TextView mNikenameTv;

    private TextView mTitle;
    private View mCyanBg;  /// 蓝色渐进色背景板

    @BindView(R.id.ubt_btn_person_hotspot)
    Button mToHospotBtn;
    @BindView(R.id.ubt_btn_device_manager)
    Button mDevMangerBtn;
    @BindView(R.id.ubt_btn_person_answer)
    Button mAnswerBtn;
    @BindView(R.id.ubt_btn_person_clock)
    Button mClockBtn;
    @BindView(R.id.ubt_btn_person_remind)
    Button mRemindBtn;
    @BindView(R.id.ubt_btn_person_qq)
    Button mQQMusicBtn;

    public PersonalFragment() {
        super();
    }

    @Override
    protected void onNoPig() {
        if (mCyanBg != null) {
            mCyanBg.setVisibility(View.GONE);
        }
        if (mTitle != null) {
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color
                    .ubt_tips_txt_color, null));
        }
    }

    @Override
    protected void onNoSetNet() {
        if (mCyanBg != null) {
            mCyanBg.setVisibility(View.GONE);
        }
        if (mTitle != null) {
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color
                    .ubt_tips_txt_color, null));
        }
    }

    @Override
    protected void onHasPig() {
        if (mCyanBg != null) {
            mCyanBg.setVisibility(View.VISIBLE);
        }
        if (mTitle != null) {
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ubt_white, null));
        }
    }

    @Override
    protected void onSetedNet() {
        if (mCyanBg != null) {
            mCyanBg.setVisibility(View.VISIBLE);
        }
        if (mTitle != null) {
            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ubt_white, null));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        EventBusUtil.register(this);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inits();
    }

    private void inits() {
        mPohtoImg = getActivity().findViewById(R.id.ubt_img_me_photo);
        mNikenameTv = getActivity().findViewById(R.id.ubt_tv_me_nikename);

        mTitle = getActivity().findViewById(R.id.ubt_me_fragment_title);
        mCyanBg = getActivity().findViewById(R.id.ubt_me_normal_bg);

        mSetNetBtn = getActivity().findViewById(R.id.ubt_btn_person_set_wifi);
        mSetNetBtn.setOnClickListener(this);

        mFeedBackBtn = getActivity().findViewById(R.id.ubt_btn_person_feedback);
        mFeedBackBtn.setOnClickListener(this);

        mAboutBtn = getActivity().findViewById(R.id.ubt_btn_person_about);
        mAboutBtn.setOnClickListener(this);

        getActivity().findViewById(R.id.ubt_btn_person_clock).setOnClickListener(this);
        getActivity().findViewById(R.id.ubt_btn_person_answer).setOnClickListener(this);

        try {
            String versionName = String.format(getString(R.string.ubt_version_format),
                    ContextUtils.getVerName(getContext()));
            mAboutBtn.setRightText(versionName);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        getActivity().findViewById(R.id.ubt_btn_device_manager).setOnClickListener(view -> ActivityRoute.toAnotherActivity(getActivity(), DeviceManageActivity.class, false));
        changeItemAlpha();

        fillAccountView();
    }

    private void fillAccountView() {
        UserInfo currentUser = AuthLive.getInstance().getCurrentUser();
        if (currentUser != null) {
            String nickName = SharedPreferencesUtils.getString(getActivity(), "tvs_nickName", "");
            String headImgUrl = SharedPreferencesUtils.getString(getActivity(), "tvs_headImgUrl", "");
            if (TextUtils.isEmpty(nickName)) {
                nickName = currentUser.getNickName();
            }
            if (TextUtils.isEmpty(headImgUrl)) {
                headImgUrl = currentUser.getUserImage();
            }

            Glide.with(getActivity())
                    .load(headImgUrl)
                    .asBitmap()
                    .centerCrop()
                    .transform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.drawable.ic_sign_in)
                    .into(mPohtoImg);

            mNikenameTv.setText(StringUtils.utf8ToString(nickName));
        } else {
            ActivityManager.getInstance().popAllActivityExcept(LoginActivity.class.getName());
            ActivityRoute.toAnotherActivity(getActivity(), LoginActivity.class, true);
        }
    }

    private void changeItemAlpha() {
        try {
            float alpha = 1f;
            boolean isEnable = true;
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            if (pigInfo != null && pigInfo.isAdmin) {
                alpha = 1f;
                isEnable = true;
            } else {
                alpha = 0.5f;
                isEnable = false;
            }

            mToHospotBtn.setAlpha(alpha);
            mToHospotBtn.setEnabled(isEnable);
            mAnswerBtn.setAlpha(alpha);
            mAnswerBtn.setEnabled(isEnable);//isEnable
            mClockBtn.setAlpha(alpha);
            mClockBtn.setEnabled(isEnable);
            mRemindBtn.setAlpha(alpha);
            mRemindBtn.setEnabled(isEnable);
        } catch (Exception e) {

        }
    }

    @Override
    @OnClick({R.id.rl_login_info, R.id.ubt_btn_person_hotspot, R.id.ubt_btn_person_remind, R.id.ubt_btn_person_qq})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_login_info:
                ActivityRoute.toAnotherActivity(getActivity(), UserInfoActivity.class, false);
                break;
            case R.id.ubt_btn_person_set_wifi:
                HashMap<String, Boolean> params = new HashMap<>();
                params.put("back", false);
                params.put("skip", true);
                toBleConfigActivity(params, false);
                break;
            case R.id.ubt_btn_person_hotspot:
                if (AuthLive.getInstance().getCurrentPig() != null) {
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
            case R.id.ubt_btn_person_answer:
                ActivityRoute.toAnotherActivity(getActivity(), InterlocutionActivity.class, false);
                break;
            case R.id.ubt_btn_person_remind:
                ActivityRoute.toAnotherActivity(getActivity(), RemindActivity.class, false);
                break;
            case R.id.ubt_btn_person_qq:
                ActivityRoute.toAnotherActivity(getActivity(), QQMusicWebActivity.class, false);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event == null) return;
        int code = event.getCode();
        switch (code) {
            case USER_PIG_UPDATE:
                changeItemAlpha();
                break;
        }
    }
}

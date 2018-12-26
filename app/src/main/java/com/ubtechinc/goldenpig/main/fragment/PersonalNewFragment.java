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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.about.UbtAboutActivtiy;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.feedback.FeedBackActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.QQMusicWebActivity;
import com.ubtechinc.goldenpig.me.UserInfoActivity;
import com.ubtechinc.goldenpig.personal.DeviceManageActivity;
import com.ubtechinc.goldenpig.personal.PigManageDetailActivity;
import com.ubtechinc.goldenpig.personal.alarm.AlarmListActivity;
import com.ubtechinc.goldenpig.personal.interlocution.InterlocutionActivity;
import com.ubtechinc.goldenpig.personal.remind.RemindActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.hotspot.SetHotSpotActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

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
public class PersonalNewFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.ubt_img_me_photo)
    ImageView mPohtoImg;
    @BindView(R.id.ubt_tv_me_nikename)
    TextView mNikenameTv;
    @BindView(R.id.tv_manager)
    TextView tv_manager;
    @BindView(R.id.rl_pig_state)
    RelativeLayout rl_pig_state;
    @BindView(R.id.tv_pig)
    TextView tv_pig;
    @BindView(R.id.tv_pig_state)
    TextView tv_pig_state;
    @BindView(R.id.ubt_tv_pig_name)
    TextView ubt_tv_pig_name;
    @BindView(R.id.ll_version)
    LinearLayout ll_version;
    @BindView(R.id.ll_bind)
    LinearLayout ll_bind;
    @BindView(R.id.ll_function)
    LinearLayout ll_function;
    @BindView(R.id.ubt_btn_person_qq)
    Button mQQMusicBtn;
    @BindView(R.id.ubt_btn_person_feedback)
    View mFeedBackBtn; //反馈帮助
    @BindView(R.id.ubt_btn_person_about)
    UbtSubTxtButton mAboutBtn; //关于页按钮

    //    private View mToUserInfo;
//    private View mSetNetBtn;   //绑定配网按钮
//    private TextView mTitle;
//    private View mCyanBg;  /// 蓝色渐进色背景板
//    @BindView(R.id.ubt_btn_person_hotspot)
//    Button mToHospotBtn;
//    @BindView(R.id.ubt_btn_device_manager)
//    Button mDevMangerBtn;
//    @BindView(R.id.ubt_btn_person_answer)
//    Button mAnswerBtn;
//    @BindView(R.id.ubt_btn_person_clock)
//    Button mClockBtn;
//    @BindView(R.id.ubt_btn_person_remind)
//    Button mRemindBtn;
    public PersonalNewFragment() {
        super();
    }

    @Override
    protected void onNoPig() {
        tv_pig_state.setVisibility(View.GONE);
        tv_manager.setVisibility(View.INVISIBLE);
        ll_bind.setVisibility(View.VISIBLE);
        ll_function.setVisibility(View.GONE);
        ll_version.setVisibility(View.GONE);
        ubt_tv_pig_name.setVisibility(View.GONE);
//        if (mCyanBg != null) {
//            mCyanBg.setVisibility(View.GONE);
//        }
//        if (mTitle != null) {
//            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color
//                    .ubt_tips_txt_color, null));
//        }
    }

    @Override
    protected void onNoSetNet() {
        tv_pig_state.setVisibility(View.VISIBLE);
        tv_pig_state.setText("(离线)");
        tv_manager.setVisibility(View.INVISIBLE);
        ll_bind.setVisibility(View.VISIBLE);
        ll_function.setVisibility(View.GONE);
        ll_version.setVisibility(View.GONE);
        ubt_tv_pig_name.setVisibility(View.VISIBLE);
//        if (mCyanBg != null) {
//            mCyanBg.setVisibility(View.GONE);
//        }
//        if (mTitle != null) {
//            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color
//                    .ubt_tips_txt_color, null));
//        }
    }

    @Override
    protected void onHasPig() {

//        if (mCyanBg != null) {
//            mCyanBg.setVisibility(View.VISIBLE);
//        }
//        if (mTitle != null) {
//            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ubt_white, null));
//        }
    }

    @Override
    protected void onSetedNet() {
//        if (mCyanBg != null) {
//            mCyanBg.setVisibility(View.VISIBLE);
//        }
//        if (mTitle != null) {
//            mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ubt_white, null));
//        }
    }

    @Override
    protected void hasNewVersion() {
        ll_version.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_new, container, false);
        EventBusUtil.register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inits();
    }

    private void inits() {
        int height = (int) ((ScreenUtils.getScreenWidth() - getResources().getDimension(R.dimen.dp_30)) * 187 / 345);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rl_pig_state.getLayoutParams();
        params.height = height;
        rl_pig_state.setLayoutParams(params);
        Glide.with(getActivity())
                .load(AuthLive.getInstance().getCurrentUser().getUserImage())
                .asBitmap()
                .centerCrop()
                .transform(new GlideCircleTransform(getActivity()))
                .placeholder(R.drawable.ic_sign_in)
                .into(mPohtoImg);
        mNikenameTv.setText(StringUtils.utf8ToString(AuthLive.getInstance().getCurrentUser().getNickName()));
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null) {
            if (pigInfo.isAdmin) {
                tv_manager.setVisibility(View.VISIBLE);
            } else {
                tv_manager.setVisibility(View.INVISIBLE);
            }
            tv_pig_state.setVisibility(View.VISIBLE);
            String name = pigInfo.getRobotName();
            if (!TextUtils.isEmpty(name) && name.length() >= 4) {
                name = name.substring(name.length() - 4, name.length());
            }
            ubt_tv_pig_name.setText(getString(R.string.ubt_bajie, name));
            ubt_tv_pig_name.setVisibility(View.VISIBLE);
            ll_bind.setVisibility(View.GONE);
            ll_function.setVisibility(View.VISIBLE);
        } else {
            ubt_tv_pig_name.setVisibility(View.GONE);
            ll_bind.setVisibility(View.VISIBLE);
            ll_function.setVisibility(View.GONE);
        }
        mFeedBackBtn.setOnClickListener(this);
        mAboutBtn.setOnClickListener(this);
        try {
            String versionName = String.format(getString(R.string.ubt_version_format),
                    ContextUtils.getVerName(getContext()));
            mAboutBtn.setRightText(versionName);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
//        getActivity().findViewById(R.id.ubt_btn_device_manager).setOnClickListener(new View
//                .OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityRoute.toAnotherActivity(getActivity(), DeviceManageActivity.class, false);
//            }
//        });
    }

    @Override
    @OnClick({R.id.rl_login_info, /*R.id.ubt_btn_person_hotspot, R.id.ubt_btn_person_remind,*/ R.id
            .ubt_btn_person_qq, R.id.rl_pig_state})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_login_info:
                ActivityRoute.toAnotherActivity(getActivity(), UserInfoActivity.class, false);
                break;
            case R.id.ubt_btn_person_set_wifi:
                HashMap<String, Boolean> params = new HashMap<>();
                params.put("back", false);
                params.put("skip", true);
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class,
                        params,
                        false);
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
            case R.id.rl_pig_state:
                ActivityRoute.toAnotherActivity(getActivity(), PigManageDetailActivity.class, false);
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
                inits();
                break;
        }
    }
}

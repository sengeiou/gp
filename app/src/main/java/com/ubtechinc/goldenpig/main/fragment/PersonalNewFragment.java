package com.ubtechinc.goldenpig.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.imlibv2.bean.ContactsProtoBuilder;
import com.ubt.imlibv2.bean.UbtTIMManager;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.about.UbtAboutActivtiy;
import com.ubtechinc.goldenpig.app.ActivityManager;
import com.ubtechinc.goldenpig.app.UBTPGApplication;
import com.ubtechinc.goldenpig.base.BaseActivity;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.entity.UserInfo;
import com.ubtechinc.goldenpig.comm.img.GlideCircleTransform;
import com.ubtechinc.goldenpig.comm.widget.UBTSubTitleDialog;
import com.ubtechinc.goldenpig.creative.CreateActivity;
import com.ubtechinc.goldenpig.eventbus.EventBusUtil;
import com.ubtechinc.goldenpig.eventbus.modle.Event;
import com.ubtechinc.goldenpig.login.LoginActivity;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.main.CheckMessageHttpProxy;
import com.ubtechinc.goldenpig.main.CommonWebActivity;
import com.ubtechinc.goldenpig.main.MainActivity;
import com.ubtechinc.goldenpig.main.UbtWebHelper;
import com.ubtechinc.goldenpig.me.UserInfoActivity;
import com.ubtechinc.goldenpig.message.MessageActivity;
import com.ubtechinc.goldenpig.personal.BeeHiveMobileActivity;
import com.ubtechinc.goldenpig.personal.ContinuousVoiceActivity;
import com.ubtechinc.goldenpig.personal.NoSimActivity;
import com.ubtechinc.goldenpig.personal.PigManageDetailActivity;
import com.ubtechinc.goldenpig.personal.RobotOfflineActivity;
import com.ubtechinc.goldenpig.personal.SwitchWifiActivity;
import com.ubtechinc.goldenpig.pigmanager.BleConfigReadyActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.hotspot.SetHotSpotActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.utils.CheckUtil;
import com.ubtechinc.goldenpig.utils.SharedPreferencesUtils;
import com.ubtechinc.goldenpig.utils.UbtToastUtils;
import com.ubtrobot.upgrade.VersionInformation;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NEED_SHOW_POINT;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.NETWORK_STATE_CHANGED;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.READ_SYSTEM_MSG;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_ROBOT_ONLINE_STATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.RECEIVE_ROBOT_VERSION_STATE;
import static com.ubtechinc.goldenpig.eventbus.EventBusUtil.USER_PIG_UPDATE;
import static com.ubtechinc.goldenpig.main.CommonWebActivity.KEY_URL;
import static com.ubtechinc.goldenpig.personal.AboutBleBJActivity.KEY_PIGINFO_VERSION;
import static com.ubtechinc.goldenpig.personal.BeeHiveMobileActivity.KEY_BEE_HIVE_OPEN;
import static com.ubtechinc.goldenpig.personal.NoSimActivity.KEY_TOOL_BAR_TITLE;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :  个人信息页Fragment
 * @time :2018/8/17 17:58
 * @change :
 * @changTime :2018/8/17 17:58
 */
public class PersonalNewFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "PersonalNewFragment";

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
    @BindView(R.id.iv_online_state)
    ImageView iv_online_state;
    @BindView(R.id.iv_go_to)
    ImageView iv_goto;
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
    @BindView(R.id.rl_cyctem_msg)
    RelativeLayout rlMsg;
    @BindView(R.id.iv_red_point)
    ImageView ivRedPoint;
    @BindView(R.id.ubt_btn_person_creative)
    View mCreativeSpaceBtn;
    @BindView(R.id.ubt_btn_manual)
    View mManualBtn;

    public PersonalNewFragment() {
        super();
    }

    @Override
    protected void onNoPig() {
        tv_manager.setVisibility(View.INVISIBLE);
        ll_bind.setVisibility(View.VISIBLE);
        ll_function.setVisibility(View.GONE);
        ll_version.setVisibility(View.GONE);
        ubt_tv_pig_name.setVisibility(View.GONE);
        updateRobotUI(null);
        ll_version.setVisibility(View.GONE);
    }

    @Override
    protected void onNoSetNet() {
        tv_manager.setVisibility(View.INVISIBLE);
        ll_bind.setVisibility(View.VISIBLE);
        ll_function.setVisibility(View.GONE);
        ll_version.setVisibility(View.GONE);
        ubt_tv_pig_name.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onHasPig() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getPigVersionState();
        }
    }

    private void getPigVersionState() {
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin && UBTPGApplication.isNetAvailable && UBTPGApplication.isRobotOnline) {
            UbtTIMManager.getInstance().sendTIM(ContactsProtoBuilder.createTIMMsg(ContactsProtoBuilder.getPigVersionState()));
        }
    }

    @Override
    protected void onSetedNet() {

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
        checkSystemMSG(); //向后台查询是否有未读消息
        return view;
    }

    public void checkSystemMSG() {

        UbtLogger.d(TAG, "checkSystemMSG");
        new CheckMessageHttpProxy().getData(new CheckMessageHttpProxy.CheckMessageCallback() {
            @Override
            public void onError(String error) {
                UbtLogger.d(TAG, "onError:" + error);
            }

            @Override
            public void onSuccess(boolean show) {
                UbtLogger.d(TAG, "onSuccess:" + show);
                Event event = new Event<>(EventBusUtil.NEED_SHOW_POINT);
                event.setData(show);
                EventBusUtil.sendEvent(event);
            }
        });



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
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        updateRobotUI(null);

        if (pigInfo != null) {
            mQQMusicBtn.setAlpha(1.0f);
            mQQMusicBtn.setEnabled(true);
            mCreativeSpaceBtn.setAlpha(1.0f);
            mCreativeSpaceBtn.setEnabled(true);
            tv_manager.setVisibility(View.VISIBLE);
            if (pigInfo.isAdmin) {
                tv_manager.setText("管理员");
            } else {
                tv_manager.setText("普通成员");
                ll_version.setVisibility(View.GONE);
            }
            String name = pigInfo.getRobotName();
            if (!TextUtils.isEmpty(name) && name.length() >= 4) {
                name = name.substring(name.length() - 4, name.length());
            }
            ubt_tv_pig_name.setText(getString(R.string.ubt_bajie, name));
            ubt_tv_pig_name.setVisibility(View.VISIBLE);
            ll_bind.setVisibility(View.GONE);

            ll_function.setVisibility(View.VISIBLE);
        } else {
            mQQMusicBtn.setAlpha(0.5f);
            mQQMusicBtn.setEnabled(false);
            mCreativeSpaceBtn.setAlpha(0.5f);
            mCreativeSpaceBtn.setEnabled(false);
            ubt_tv_pig_name.setVisibility(View.GONE);
            ll_bind.setVisibility(View.VISIBLE);
            ll_function.setVisibility(View.GONE);
            tv_manager.setVisibility(View.GONE);
            ll_version.setVisibility(View.GONE);
        }
        mFeedBackBtn.setOnClickListener(this);
        mAboutBtn.setOnClickListener(this);
        mCreativeSpaceBtn.setOnClickListener(this);
        try {
            String versionName = String.format(getString(R.string.ubt_version_format),
                    ContextUtils.getVerName(getContext()));
            mAboutBtn.setRightText(versionName);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        fillAccountView();
    }

    private void fillAccountView() {
        UserInfo currentUser = AuthLive.getInstance().getCurrentUser();
        if (currentUser != null) {
            String nickName = SharedPreferencesUtils.getString(getActivity(), "tvs_nickName", "");
            String headImgUrl = SharedPreferencesUtils.getString(getActivity(), "tvs_headImgUrl", "");
            Log.d(TAG, "PersonalFrag|fillAccountView|tvs_nickName=" + nickName + " ubt_nickName=" + currentUser.getNickName());
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

    @Override
    @OnClick({R.id.rl_login_info, R.id.ubt_btn_person_qq, R.id.rl_pig_state, R.id.ll_bind, R.id.ll_wifi, R.id.ll_4g,
            R.id.ll_hot_pwd, R.id.ll_duihua, R.id.rl_cyctem_msg, R.id.ubt_btn_manual})
    public void onClick(View v) {
        if (!CheckUtil.checkPhoneNetState(getActivity())) {
            return;
        }
        boolean isNoSim = MainActivity.isNoSim;
        boolean isBeeHiveOpen = MainActivity.isBeeHiveOpen;
        switch (v.getId()) {
            case R.id.rl_login_info:
                ActivityRoute.toAnotherActivity(getActivity(), UserInfoActivity.class, false);
                break;
            case R.id.ubt_btn_person_feedback:
                ActivityRoute.toAnotherActivity(getActivity(), CommonWebActivity.class, UbtWebHelper.getFeedBackWebviewData(getActivity()), false);
                break;
            case R.id.ubt_btn_person_about:
                ActivityRoute.toAnotherActivity(getActivity(), UbtAboutActivtiy.class, false);
                break;
            case R.id.ubt_btn_person_qq:
                ActivityRoute.toAnotherActivity(getActivity(), CommonWebActivity.class, UbtWebHelper.getQQMusicWebviewData(getActivity()), false);
                break;
            case R.id.rl_pig_state:
                HashMap<String, Boolean> hashMap = new HashMap<>();
                hashMap.put("robotNewVersion", ll_version.getVisibility() == View.VISIBLE);
                ActivityRoute.toAnotherActivity(getActivity(), PigManageDetailActivity.class, hashMap, false);
                break;
            case R.id.ll_bind:
                ActivityRoute.toAnotherActivity(getActivity(), BleConfigReadyActivity.class, false);
                break;
            case R.id.ll_wifi:
                PigInfo myPig = AuthLive.getInstance().getCurrentPig();
                if (myPig.isAdmin) {
                    if (UBTPGApplication.isRobotOnline) {
                        ActivityRoute.toAnotherActivity(getActivity(), SwitchWifiActivity.class, false);
                    } else {
                        ActivityRoute.toAnotherActivity(getActivity(), RobotOfflineActivity.class, false);
                    }
                } else {
                    ActivityRoute.toAnotherActivity(getActivity(), BleConfigReadyActivity.class, false);
                }
                break;
            case R.id.ll_4g:
                if (isNoSim) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(KEY_TOOL_BAR_TITLE, getResources().getString(R.string.ubt_mobile_bee_hive));
                    enterFunction(NoSimActivity.class, map);
                } else {
                    HashMap<String, Boolean> map = new HashMap<>();
                    map.put(KEY_BEE_HIVE_OPEN, isBeeHiveOpen);
                    enterFunction(BeeHiveMobileActivity.class, map);
                }
                break;
            case R.id.ll_hot_pwd:
                if (isNoSim) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(KEY_TOOL_BAR_TITLE, getResources().getString(R.string.ubt_person_hotspot));
                    enterFunction(NoSimActivity.class, map);
                } else if (isBeeHiveOpen) {
                    enterFunction(SetHotSpotActivity.class, null);
                } else {
                    UbtToastUtils.showCustomToast(getActivity(), getString(R.string.open_beehive_mobile));
                }
                break;
            case R.id.ll_duihua:
                enterFunction(ContinuousVoiceActivity.class, null);
                break;
            case R.id.rl_cyctem_msg:
                ActivityRoute.toAnotherActivity(getActivity(), MessageActivity.class, false);
                break;
            case R.id.ubt_btn_person_creative:
                ActivityRoute.toAnotherActivity(getActivity(),CreateActivity.class, false);
                break;
            case R.id.ubt_btn_manual:
                String url = BuildConfig.H5_URL + "/small/smallPigStrategy.html";
                Intent it = new Intent(getActivity(), CommonWebActivity.class);
                it.putExtra(KEY_URL, url);
                startActivity(it);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        Log.e("Personal", "isd:" + isDetached());
        if (event == null || isDetached() || isRemoving() || !isAdded()) {
            return;
        }
        int code = event.getCode();
        switch (code) {
            case USER_PIG_UPDATE:
                inits();
                break;
            case NETWORK_STATE_CHANGED:
                updateRobotUI(null);
                break;
            case RECEIVE_ROBOT_ONLINE_STATE:
                updateRobotUI(null);
                break;
            case RECEIVE_ROBOT_VERSION_STATE:
                updateRobotUI((VersionInformation.UpgradeInfo) event.getData());
                break;
            case READ_SYSTEM_MSG:
                ivRedPoint.setVisibility(View.GONE);
                break;
            case NEED_SHOW_POINT:
                if((boolean)(event.getData())){
                    ivRedPoint.setVisibility(View.VISIBLE);
                }else{
                    ivRedPoint.setVisibility(View.GONE);
                }
                break;


        }
    }

    /**
     * 更新机器人面板ui
     */
    private void updateRobotUI(VersionInformation.UpgradeInfo info) {
        if (UBTPGApplication.isNetAvailable) {
            PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
            boolean hasPig = pigInfo != null;
            boolean isAdmin = hasPig && pigInfo.isAdmin;
            boolean isOnline = isAdmin && UBTPGApplication.isRobotOnline;
            String robotName = hasPig ? pigInfo.getRobotName() : "";
            iv_goto.setVisibility(hasPig ? View.VISIBLE : View.GONE);
            tv_pig_state.setText(isOnline ? "(在线)" : "(离线)");
            tv_pig_state.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            iv_online_state.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

            if (!isOnline) {
                ll_version.setVisibility(View.GONE);
            }
            iv_online_state.setImageResource(isOnline ? R.drawable.ic_line : R.drawable.ic_off_line);

            if (info != null) {
                int status = info.getStatus();
                ll_version.setVisibility(status == 1 ? View.VISIBLE : View.GONE);
                String currentVersion = info.getCurrentVersion();
                SPUtils.get().put(KEY_PIGINFO_VERSION + robotName, currentVersion);
            } else {
                //TODO 如果设备切到在线状态且未显示版本更新
                if (isOnline && (ll_version.getVisibility() != View.VISIBLE)) {
                    getPigVersionState();
                }
            }
        } else {
            tv_pig_state.setVisibility(View.GONE);
            ll_version.setVisibility(View.GONE);
            iv_online_state.setVisibility(View.GONE);
        }
    }

    private void enterFunction(Class clazz, HashMap<String, ? extends Object> hashMap) {
        PigInfo myPig = AuthLive.getInstance().getCurrentPig();
        if (myPig == null) {
            showBindTipDialog();
        } else if (myPig.isAdmin) {
            if (checkOnlineState()) {
                ActivityRoute.toAnotherActivity(getActivity(), clazz, hashMap, false);
            }
        } else {
            ToastUtils.showShortToast(R.string.only_admin_operate);
        }
    }

    private boolean checkOnlineState() {
        if (UBTPGApplication.isRobotOnline) {
            return true;
        } else {
            UbtToastUtils.showCustomToast(getActivity(), getString(R.string.ubt_robot_offline));
            return false;
        }
    }

    private void showBindTipDialog() {
        UBTSubTitleDialog dialog = new UBTSubTitleDialog(getActivity());
        dialog.setRightBtnColor(ContextCompat.getColor(getActivity(), R.color.ubt_tab_btn_txt_checked_color));
        dialog.setTips("请完成绑定与配网");
        dialog.setSubTips("完成后即可使用各项技能");
        dialog.setLeftButtonTxt("取消");
        dialog.setRightButtonTxt("确认");
        dialog.setOnUbtDialogClickLinsenter(new UBTSubTitleDialog.OnUbtDialogClickLinsenter() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                //TODO goto ble bind config
                if (getActivity() instanceof BaseActivity) {
                    ((BaseActivity) getActivity()).toBleConfigActivity(false);
                }
            }
        });
        dialog.show();
    }
}

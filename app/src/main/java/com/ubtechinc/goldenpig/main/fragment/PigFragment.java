package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.commlib.log.UBTLog;
import com.ubtechinc.goldenpig.BuildConfig;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.comm.net.CookieInterceptor;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.personal.MemberQRScannerActivity;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.pigmanager.register.GetPairPigQRHttpProxy;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :小猪分页Fragment
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class PigFragment extends BaseFragment {

    @BindView(R.id.ubt_layout_tips)
    View mTipsView;

    @BindView(R.id.ubt_bind_tv)
    View mBindClickTv;

    @BindView(R.id.view_pig_pair_add)
    View viewPigPairAdd;

    @BindView(R.id.view_pig_pair_info)
    View viewPigPairInfo;

    @BindView(R.id.rl_pair_pig)
    View rlPairPig;

    @BindView(R.id.ll_voicechat)
    LinearLayout llVoiceChat;

    int pairUserId;
    String serialNumber;
    String pairSerialNumber;
    int userId;

    public PigFragment() {
        super();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pig, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
        if (pigInfo != null && pigInfo.isAdmin) {
            rlPairPig.setAlpha(1.0f);
        } else {
            rlPairPig.setAlpha(0.5f);
        }
        updatePigPair();
    }

    private void updatePigPair() {
        new GetPairPigQRHttpProxy().getPairPigQR(getActivity(), CookieInterceptor.get().getToken(), BuildConfig.APP_ID, new GetPairPigQRHttpProxy.GetPairPigQRCallBack() {
            @Override
            public void onError(String error) {

                //TODO 获取配对数据失败
                showPigPair(false);
            }

            @Override
            public void onSuccess(String response) {

                //TODO 刷新配对信息
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject != null) {
                            JSONObject pairData = new JSONObject(jsonObject.optString("pairData"));
                            if (pairData != null) {
                                pairUserId = pairData.optInt("pairUserId");
                                serialNumber = pairData.optString("serialNumber");
                                pairSerialNumber = pairData.optString("pairSerialNumber");
                                userId = pairData.optInt("userId");
                                showPigPair(true);
                            }
                        }
                    } catch (JSONException e) {
                        UBTLog.e("pig", e.getMessage());
                    }
                }
            }
        });
    }

    private void showPigPair(boolean hasPair) {
        viewPigPairInfo.setVisibility(hasPair ? View.VISIBLE : View.GONE);
        viewPigPairAdd.setVisibility(!hasPair ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onNoPig() {

    }

    @Override
    protected void onNoSetNet() {

    }

    @Override
    protected void onHasPig() {

    }

    @Override
    protected void onSetedNet() {

    }

    @OnClick({R.id.ubt_bind_tv, R.id.ll_record, R.id.ll_voicechat, R.id.view_pig_pair_add, R.id.view_pig_pair_info})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.ll_voicechat:
                PigInfo pigInfo0 = AuthLive.getInstance().getCurrentPig();
                if (pigInfo0 != null && pigInfo0.isAdmin) {
                    ActivityRoute.toAnotherActivity(getActivity(), ChatActivity.class, false);
                }
                ActivityRoute.toAnotherActivity(getActivity(), ChatActivity.class, false);
                break;
            case R.id.view_pig_pair_add:
                PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                if (pigInfo != null && pigInfo.isAdmin) {
                    //TODO 配对小猪
                    ActivityRoute.toAnotherActivity(getActivity(), MemberQRScannerActivity.class, false);
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
                }
                break;
            case R.id.view_pig_pair_info:
                //TODO 解除配对
                break;
            case R.id.ubt_bind_tv:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, false);
                break;
            case R.id.ll_record:
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    ActivityRoute.toAnotherActivity(getActivity(), RecordActivity.class, false);
                }
                break;
            default:
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}

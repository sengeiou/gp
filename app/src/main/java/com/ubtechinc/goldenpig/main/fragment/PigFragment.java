package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.login.observable.AuthLive;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.pigmanager.bean.PigInfo;
import com.ubtechinc.goldenpig.route.ActivityRoute;

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

    @BindView(R.id.ubt_imgbtn_add_pig)
    View ubtImgbtnAddPig;

    @BindView(R.id.textView4)
    View textView4;

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
            ubtImgbtnAddPig.setAlpha(1.0f);
            textView4.setAlpha(1.0f);
        } else {
            ubtImgbtnAddPig.setAlpha(0.5f);
            textView4.setAlpha(0.5f);
        }
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

    @OnClick({R.id.ubt_bind_tv, R.id.ll_record, R.id.ubt_imgbtn_add_pig})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.ubt_bind_tv:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class, false);
                break;
            case R.id.ll_record:
                if (AuthLive.getInstance().getCurrentPig() != null) {
                    ActivityRoute.toAnotherActivity(getActivity(), RecordActivity.class, false);
                }
                break;
            case R.id.ubt_imgbtn_add_pig:
                PigInfo pigInfo = AuthLive.getInstance().getCurrentPig();
                if (pigInfo != null && pigInfo.isAdmin) {
                    //TODO 配对小猪
                } else {
                    ToastUtils.showShortToast(R.string.only_admin_operate);
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

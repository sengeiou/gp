package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.qrcodelib.QRScannerActivity;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.pigmanager.RecordActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;
import com.ubtechinc.goldenpig.voiceChat.ui.ChatActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   :小猪分页Fragment
 * @time       :2018/8/17 18:00
 * @change     :
 * @changTime  :2018/8/17 18:00
 */
public class PigFragment extends BaseFragment{
    @BindView(R.id.ubt_layout_tips)
    View mTipsView;
    @BindView(R.id.ubt_bind_tv)
    View mBindClickTv;
    public PigFragment(){
        super();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_pig,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
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

    @OnClick({R.id.ubt_bind_tv,R.id.ll_record,R.id.ll_voicechat,R.id.ubt_imgbtn_add_pig})
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.ll_voicechat:
                ActivityRoute.toAnotherActivity(getActivity(),ChatActivity.class,false);
                break;
            case R.id.ubt_imgbtn_add_pig:
                ActivityRoute.toAnotherActivity(getActivity(),QRScannerActivity.class,false);
                break;
            case R.id.ubt_bind_tv:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class,false);
                break;
            case R.id.ll_record:
                ActivityRoute.toAnotherActivity(getActivity(), RecordActivity.class,false);
                break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}

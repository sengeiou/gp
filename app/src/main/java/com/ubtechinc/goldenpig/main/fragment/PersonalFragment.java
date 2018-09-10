package com.ubtechinc.goldenpig.main.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.commlib.view.UbtSubTxtButton;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.about.UbtAboutActivtiy;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.feedback.FeedBackActivity;
import com.ubtechinc.goldenpig.pigmanager.SetNetWorkEnterActivity;
import com.ubtechinc.goldenpig.route.ActivityRoute;

/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   :  个人信息页Fragment
 * @time       :2018/8/17 17:58
 * @change     :
 * @changTime  :2018/8/17 17:58
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener{
    private View mSetNetBtn;   //绑定配网按钮
    private View mFeedBackBtn; //反馈帮助
    private UbtSubTxtButton mAboutBtn; //关于页按钮
    public PersonalFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_person,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inits();
        translucentStatusBar(getActivity(),true);
    }


    private void inits(){
        mSetNetBtn=getActivity().findViewById(R.id.ubt_btn_person_set_wifi);
        mSetNetBtn.setOnClickListener(this);

        mFeedBackBtn=getActivity().findViewById(R.id.ubt_btn_person_feedback);
        mFeedBackBtn.setOnClickListener(this);

        mAboutBtn=(UbtSubTxtButton)getActivity().findViewById(R.id.ubt_btn_person_about);
        mAboutBtn.setOnClickListener(this);
        try {
            String versionName = String.format(getString(R.string.ubt_version_format), ContextUtils.getVerName(getContext()));
            mAboutBtn.setRightText(versionName);
        }catch (RuntimeException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_btn_person_set_wifi:
                ActivityRoute.toAnotherActivity(getActivity(), SetNetWorkEnterActivity.class,false);
                break;
            case R.id.ubt_btn_person_feedback:
                ActivityRoute.toAnotherActivity(getActivity(), FeedBackActivity.class,false);
                break;
            case R.id.ubt_btn_person_about:
                ActivityRoute.toAnotherActivity(getActivity(), UbtAboutActivtiy.class,false);
                break;
        }
    }
}

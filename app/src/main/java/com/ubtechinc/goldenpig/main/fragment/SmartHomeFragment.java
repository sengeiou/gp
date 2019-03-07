package com.ubtechinc.goldenpig.main.fragment;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseFragment;
import com.ubtechinc.goldenpig.main.SkillActivity;
import com.ubtechinc.goldenpig.main.SmartHomeWebActivity;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :主页中技能分页
 * @time :2018/8/17 18:00
 * @change :
 * @changTime :2018/8/17 18:00
 */
public class SmartHomeFragment extends BaseFragment {

    private FrameLayout content;

    private LocalActivityManager manager;

    public SmartHomeFragment() {
        super();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smarthome, container, false);
        manager = new LocalActivityManager(getActivity(), true);
        manager.dispatchCreate(savedInstanceState);
        content = view.findViewById(R.id.fm_container);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        content.addView(getContentView());
    }

    private View getContentView() {
        Intent intent = new Intent(getActivity(), SmartHomeWebActivity.class);
        return manager.startActivity("View" + 0, intent).getDecorView();
    }
}

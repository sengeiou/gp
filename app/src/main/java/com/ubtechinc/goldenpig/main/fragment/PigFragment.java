package com.ubtechinc.goldenpig.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseFragment;
/**
 * @author     : HQT
 * @email      :qiangta.huang@ubtrobot.com
 * @describe   :小猪分页Fragment
 * @time       :2018/8/17 18:00
 * @change     :
 * @changTime  :2018/8/17 18:00
 */
public class PigFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_pig,container,false);
        return view;
    }
}

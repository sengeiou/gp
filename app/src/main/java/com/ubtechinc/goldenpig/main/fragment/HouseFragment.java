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
 * @describe   :主页中家居分页
 * @time       :2018/8/17 18:00
 * @change     :
 * @changTime  :2018/8/17 18:00
 */
public class HouseFragment extends BaseFragment {
    public HouseFragment(){
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
        View view =inflater.inflate(R.layout.fragment_house,container,false);
        return view;
    }
}

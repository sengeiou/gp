package com.ubtechinc.goldenpig.main.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainFragmentAdpater extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    public MainFragmentAdpater(FragmentManager fm,ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments!=null&&position>=0&&position<fragments.size()){
            return fragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (fragments!=null){
            return  fragments.size();
        }
        return 0;

    }
}

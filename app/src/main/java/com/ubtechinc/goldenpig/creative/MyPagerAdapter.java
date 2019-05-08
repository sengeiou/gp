package com.ubtechinc.goldenpig.creative;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2018/9/7 15:31
 * @modifier：ubt
 * @modify_date：2018/9/7 15:31
 * [A brief description]
 * version
 */


public class MyPagerAdapter extends FragmentPagerAdapter {
    List<String> list;
    List<Fragment> fragments = new ArrayList<>();

    public MyPagerAdapter(FragmentManager fm, List<String> list, List<Fragment> fragments) {
        super(fm);
        this.list = list;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }
}

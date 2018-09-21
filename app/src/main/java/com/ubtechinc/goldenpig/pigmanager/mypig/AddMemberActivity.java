package com.ubtechinc.goldenpig.pigmanager.mypig;

import android.app.Activity;
import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;
/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :添加成员界面
 *@time          :2018/9/21 17:39
 *@change        :
 *@changetime    :2018/9/21 17:39
*/
public class AddMemberActivity extends BaseToolBarActivity {
    @Override
    protected int getConentView() {
        return R.layout.activity_add_member;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitleBack(true);
        setToolBarTitle(R.string.ubt_add_member);
    }
}

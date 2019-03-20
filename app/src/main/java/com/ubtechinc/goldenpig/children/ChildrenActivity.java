package com.ubtechinc.goldenpig.children;

import android.os.Bundle;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseToolBarActivity;

public class ChildrenActivity extends BaseToolBarActivity {

    private static final String TAG = ChildrenActivity.class.getSimpleName();

    @Override
    protected int getConentView() {
        return R.layout.activity_children;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setToolBarTitle("儿童");
        setTitleBack(true);
    }


}

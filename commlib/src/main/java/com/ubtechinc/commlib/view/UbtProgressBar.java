package com.ubtechinc.commlib.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.ubtechinc.commlib.R;

public class UbtProgressBar extends ProgressBar {

    public UbtProgressBar(Context context) {
        this(context, null);
    }

    public UbtProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UbtProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
            final Drawable drawable =  getContext().getApplicationContext().getResources().getDrawableForDensity(R.drawable.ic_loding2,getContext().getResources().getDisplayMetrics().densityDpi,null);
            setIndeterminateDrawable(drawable);
        }
    }
}

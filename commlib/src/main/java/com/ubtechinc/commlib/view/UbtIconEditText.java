package com.ubtechinc.commlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.ubtechinc.commlib.R;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :自定义带ICon 的EditText
 *@time          :2018/8/30 10:59
 *@change        :
 *@changetime    :2018/8/30 10:59
*/
public class UbtIconEditText extends AppCompatEditText {
    protected static final int DRAWABLE_LEFT = 0;
    protected static final int DRAWABLE_TOP = 1;
    protected static final int DRAWABLE_RIGHT = 2;
    protected static final int DRAWABLE_BOTTOM = 3;
    protected Drawable mDefaultDrawable;

    public UbtIconEditText(Context context) {
        this(context,null);
    }

    public UbtIconEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UbtIconEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.UbtIconEditTextStyle);
            if (ta != null) {
                mDefaultDrawable = ta.getDrawable(R.styleable.UbtIconEditTextStyle_defaultIcon);
                ta.recycle();
            }
        }
    }

    /**控制ICON显示**/
    protected void setIconVisible(boolean visible) {
        if (mDefaultDrawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[DRAWABLE_LEFT], getCompoundDrawables()[DRAWABLE_TOP],
                    visible ? mDefaultDrawable : null, getCompoundDrawables()[DRAWABLE_BOTTOM]);
        }

    }
}

package com.ubtechinc.commlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ubtechinc.commlib.R;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :自定义在密码和明文密码间切换的EditText
 *@time          :2018/8/30 10:56
 *@change        :
 *@changetime    :2018/8/30 10:56
*/
public class UbtPasswordEditText extends UbtIconEditText {
    private boolean isVisualPsd=false;//密码是否可视化
    private Drawable mPsdDrawable;
    public UbtPasswordEditText(Context context) {
        this(context,null);
    }

    public UbtPasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UbtPasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.UbtPsdEditTextStyle);
            if (ta != null) {
                mPsdDrawable = ta.getDrawable(R.styleable.UbtPsdEditTextStyle_psdIcon);
                ta.recycle();
            }
        }
        setIconVisible(true);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                if (mDefaultDrawable!=null&&mPsdDrawable!=null) {
                    Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (drawable != null && event.getX() <= (getWidth() - getPaddingRight()) && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                        isVisualPsd=!isVisualPsd;
                        if (isVisualPsd){
                            setIconVisible(true);
                            setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }else {
                            setVisualPsdIconVisible(true);
                            setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        }
                        invalidate();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    /**控制ICON显示**/
    protected void setVisualPsdIconVisible(boolean visible) {
        if (mPsdDrawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[DRAWABLE_LEFT], getCompoundDrawables()[DRAWABLE_TOP],
                    visible ? mPsdDrawable : null, getCompoundDrawables()[DRAWABLE_BOTTOM]);
        }

    }
}

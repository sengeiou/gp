package com.ubtechinc.commlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ubtechinc.commlib.R;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :带删除按钮功能的edittext
 * @time :2018/8/30 9:57
 * @change :
 * @changetime :2018/8/30 9:57
 */
public class UbtClearableEditText extends UbtIconEditText {


    public UbtClearableEditText(Context context) {
        this(context, null);
    }

    public UbtClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UbtClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {


    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setIconVisible(hasFocus() && text.length() > 0);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setIconVisible(focused && length() > 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mDefaultDrawable!=null) {
                    Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (drawable != null && event.getX() <= (getWidth() - getPaddingRight()) && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                        setText("");
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}

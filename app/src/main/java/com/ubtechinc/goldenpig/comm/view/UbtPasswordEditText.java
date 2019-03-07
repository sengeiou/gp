package com.ubtechinc.goldenpig.comm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubtechinc.commlib.view.UbtClearableEditText;
import com.ubtechinc.goldenpig.R;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :自定义在密码和明文密码间切换的EditText
 * @time :2018/8/30 10:56
 * @change :
 * @changetime :2018/8/30 10:56
 */
public class UbtPasswordEditText extends RelativeLayout implements View.OnClickListener {
    private boolean isVisualPsd = false;//密码是否可视化
    private Drawable mPsdDrawable;
    private UbtClearableEditText mClearEdt;
    private ImageView mChangBtn;

    public UbtPasswordEditText(Context context) {
        this(context, null);
    }

    public UbtPasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UbtPasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_ubt_pwd_edittext, this, true);
        mClearEdt = (UbtClearableEditText) findViewById(R.id.ubt_clearedt_wifi_pwd);
        mClearEdt.setFocusable(true);
        mClearEdt.setFocusableInTouchMode(true);
        mClearEdt.requestFocus();

        mChangBtn = findViewById(R.id.ubt_btn_pwd_visual);
        mChangBtn.setOnClickListener(this);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.UbtPsdEditTextStyle);
            if (ta != null) {
                mPsdDrawable = ta.getDrawable(R.styleable.UbtPsdEditTextStyle_psdIcon);
                ta.recycle();
            }
        }

    }

    public String getPwd() {
        if (mClearEdt != null) {
            return mClearEdt.getText().toString();
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        if (v == mChangBtn) {
            if (isVisualPsd) {
                mChangBtn.setImageResource(R.drawable.ic_closeeyes);
                mClearEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mClearEdt.setSelection(mClearEdt.length());
            } else {
                mChangBtn.setImageResource(R.drawable.ic_see);
                mClearEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mClearEdt.setSelection(mClearEdt.length());
            }
            isVisualPsd = !isVisualPsd;
        }
    }

}

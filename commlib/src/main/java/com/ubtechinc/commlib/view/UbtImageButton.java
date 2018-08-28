package com.ubtechinc.commlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.ubtechinc.commlib.R;

/**
 * @author : HQT
 * @email :qiangta.huang@ubtrobot.com
 * @describe :自定义的可准确配置ICON 属性的按钮控件
 * @time :2018/8/17 18:01
 * @change :
 * @changTime :2018/8/17 18:01
 */
public class UbtImageButton extends AppCompatButton {
    private Bitmap mIcon;
    private int mIconWidth;  //icon图片宽度
    private int mIconHeight;  //icon图片高度

    /*icon图片与父控件间距*/
    private int mIconMargin;
    private int mIconMarginTop;
    private int mIconMarginLeft;

    /*自定义图标坐标*/
    private int mIconX;
    private int mIconY;

    public UbtImageButton(Context context) {
        this(context, null);
    }

    public UbtImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UbtImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /*解析控件属性*/
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.UbtImageButtonStyle);
            if (ta != null) {
                int resId = ta.getResourceId(R.styleable.UbtImageButtonStyle_icon, 0);
                setIconResourceID(resId);

                mIconMargin = ta.getDimensionPixelSize(R.styleable.UbtImageButtonStyle_iconMargin, 0);
                mIconMarginTop = ta.getDimensionPixelSize(R.styleable.UbtImageButtonStyle_iconMarginTop, 0);
                mIconMarginLeft = ta.getDimensionPixelSize(R.styleable.UbtImageButtonStyle_iconMarginLeft, 0);

                ta.recycle();
            }
        }
    }

    /*计算自定义图标位置坐标*/
    private void computeLocationY() {
        if (mIconMarginTop == 0) {
            mIconY = (this.getMeasuredHeight() - (mIconHeight == 0 ? mIcon.getHeight() : mIconHeight)) / 2;
        } else {
            mIconY = mIconMarginTop;
        }

    }

    private void computeLocationX() {
        if (mIconMarginLeft != 0) {
            mIconX = mIconMarginLeft;
        } else if (mIconMargin != 0) {
            mIconX = mIconMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        computeLocationY();
        computeLocationX();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIcon != null) {
            canvas.translate(0, 0);
            canvas.drawBitmap(mIcon, mIconX, mIconY, null);
        }
    }

    public void setIconResourceID(int resourceId) {
        if (resourceId > 0) {
            this.mIcon = BitmapFactory.decodeResource(getResources(), resourceId);
        }
    }

    public void setIconDrawable(BitmapDrawable drawable) {
        if (drawable != null) {
            this.mIcon = drawable.getBitmap();
        } else {
            this.mIcon = null;
        }
    }

    public void setIconBitmap(Bitmap bitmap) {
        this.mIcon = bitmap;
    }

    public void setIconMargin(int margin) {
        this.mIconMargin = margin;
        computeLocationY();
    }


    public void setIconMarginTop(int iconMarginTop) {
        this.mIconMarginTop = iconMarginTop;
        computeLocationY();
    }

    public int getIconMarginTop() {
        return this.mIconMarginTop;
    }

    public int getIconMarginLeft() {
        return this.mIconMarginLeft;
    }

    public void setIconMarginLeft(int iconMarginLeft) {
        this.mIconMarginLeft = iconMarginLeft;
        computeLocationX();
    }

    public int getIconWeidth() {
        return mIconWidth;
    }

    public void setIconWeidth(int iconWeidth) {
        this.mIconWidth = iconWeidth;
    }

    public int getIconHeight() {
        return mIconHeight;
    }

    public void setIconHeight(int iconHeight) {
        this.mIconHeight = iconHeight;
        computeLocationY();
    }
}

package com.ubtechinc.commlib.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ubtechinc.commlib.R;
import com.ubtechinc.commlib.log.UbtLogger;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :自定义可在按钮右侧显示文字
 *@time          :2018/9/8 14:22
 *@change        :
 *@changetime    :2018/9/8 14:22
*/
public class UbtSubTxtButton extends android.support.v7.widget.AppCompatButton {
    private String mRightText;//自定义右侧显示文字
    private int mRightTextColor;
    private int mRightTextX,mRightTextY; /// 右侧文字坐标
    private int mRightTextPadding;      ///右侧文字距离右侧距离
    private float mRightTextSize;         ///右侧文字的大小
    public UbtSubTxtButton(Context context) {
        this(context,null);
    }

    public UbtSubTxtButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UbtSubTxtButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inits(context,attrs);
    }

    private void inits(Context context, AttributeSet attrs){
        if (attrs!=null){
            TypedArray typedArray= context.obtainStyledAttributes(attrs,
                    R.styleable.UbtRightTxtButtonStyle);
            if (typedArray!=null){
                if(typedArray.getText(R.styleable.UbtRightTxtButtonStyle_rightText)!=null){
                    mRightText=typedArray.getText(R.styleable.UbtRightTxtButtonStyle_rightText).toString();
                }
                mRightTextColor=typedArray.getColor(R.styleable.UbtRightTxtButtonStyle_rightTextColor,Color.BLACK);
                mRightTextPadding=typedArray.getDimensionPixelOffset(R.styleable.UbtRightTxtButtonStyle_rightTextPadding,0);
                mRightTextSize=typedArray.getDimensionPixelSize(R.styleable.UbtRightTxtButtonStyle_rightTextSize,(int) getTextSize());
                typedArray.recycle();
            }
        }else {
            mRightTextSize=getTextSize();
        }

    }
    public void setRightTextColor(int color){
        this.mRightTextColor=color;
    }

    public void setRightText(String rightText){
        this.mRightText=rightText;
        countTextLocation();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        countTextLocation();

    }
    private void countTextLocation(){
        if (!TextUtils.isEmpty(mRightText)){
            Rect mBound=new Rect();
            getPaint().getTextBounds(mRightText.toString(),0,mRightText.length(),mBound);
            mRightTextY=(getMeasuredHeight()+mBound.height()-4)/2;
            mRightTextX=getMeasuredWidth()-mRightTextPadding-mBound.width();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(mRightText)){
            getPaint().setColor(mRightTextColor);
            getPaint().setTextSize(mRightTextSize);
            canvas.drawText(mRightText.toString(),mRightTextX,mRightTextY,getPaint());
        }
    }
}

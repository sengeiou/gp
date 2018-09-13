package com.ubtechinc.goldenpig.actionbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;

/* 
 02.                   _ooOoo_ 
 03.                  o8888888o 
 04.                  88" . "88 
 05.                  (| -_- |) 
 06.                  O\  =  /O 
 07.               ____/`---'\____ 
 08.             .'  \\|     |//  `. 
 09.            /  \\|||  :  |||//  \ 
 10.           /  _||||| -:- |||||-  \ 
 11.           |   | \\\  -  /// |   | 
 12.           | \_|  ''\---/''  |   | 
 13.           \  .-\__  `-`  ___/-. / 
 14.         ___`. .'  /--.--\  `. . __ 
 15.      ."" '<  `.___\_<|>_/___.'  >'"". 
 16.     | | :  `- \`.;`\ _ /`;.`/ - ` : | | 
 17.     \  \ `-.   \_ __\ /__ _/   .-` /  / 
 18.======`-.____`-.___\_____/___.-`____.-'====== 
 19.                   `=---=' 
 20.^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 
 21.               佛祖保佑       永无BUG 
 22.*/
public class SecondTitleBarViewImg extends LinearLayout {

    private Context mContext;

    ImageView ivLeft, ivRight;

    TextView CenterTitle;

    public SecondTitleBarViewImg(Context context) {
        super(context);
        initView(context);
    }

    public SecondTitleBarViewImg(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        if (isInEditMode()) {
            return;
        }
        this.mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.second_titlebar_img, this);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        CenterTitle = (TextView) findViewById(R.id.tv_center);
        /*AnimationDrawable animDrawable = (AnimationDrawable) ivLeft
                .getDrawable();
        animDrawable.start();*/

    }

    public void setCommonTitle(int LeftVisibility, int centerTextVisibility, int rightVisibility) {
        ivLeft.setVisibility(LeftVisibility);
        ivRight.setVisibility(rightVisibility);
        CenterTitle.setVisibility(centerTextVisibility);
    }

    /**
     * @Description: 设置左边按钮图片
     */
    public void setIvLeft(int icon) {
        ivLeft.setImageResource(icon);
    }

    /**
     * @Description: 设置右边按钮图片
     */
    public void setIvRight(int icon) {
        ivRight.setImageResource(icon);
        ivRight.setVisibility(View.VISIBLE);
    }

    /**
     * @Description: 设置标题文字
     */
    public void setTitleText(int txtRes) {
        CenterTitle.setText(txtRes);
    }

    /**
     * @Description: 设置标题文字
     */
    public void setTitleText(String title) {
        CenterTitle.setText(title);
    }

    /**
     * @Description: 左边按钮点击事件
     */
    public void setLeftOnclickListener(OnClickListener listener) {
        ivLeft.setOnClickListener(listener);
    }

    /**
     * @Description: 右边按钮点击事件
     */
    public void setRightOnclickListener(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
    }

    public ImageView getIvleft() {
        return ivLeft;
    }

    public void setIvLeft(ImageView ivLeft) {
        this.ivLeft = ivLeft;
    }

    public ImageView getIvRight() {
        return ivRight;
    }

    public void setIvRight(ImageView ivRight) {
        this.ivRight = ivRight;
    }

    public TextView getCenterTitle() {
        return CenterTitle;
    }

    public void setCenterTitle(TextView centerTitle) {
        CenterTitle = centerTitle;
    }

    public void hideIvRight() {
        ivRight.setVisibility(View.GONE);
    }
}

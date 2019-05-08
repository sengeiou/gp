package com.ubtechinc.goldenpig.actionbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class SecondTitleBarViewTv extends LinearLayout {

    private Context mContext;

    private ImageView ivLeft;

    private TextView tv_right;
    private RelativeLayout rl_layout;
    TextView CenterTitle;

    public SecondTitleBarViewTv(Context context) {
        super(context);
        initView(context);
    }

    public SecondTitleBarViewTv(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        if (isInEditMode()) {
            return;
        }
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.second_titlebar_tv, this);
        ivLeft = view.findViewById(R.id.iv_left);
        tv_right = view.findViewById(R.id.tv_right);
        CenterTitle = view.findViewById(R.id.tv_center);
        rl_layout = view.findViewById(R.id.rl_layout);
        /*AnimationDrawable animDrawable = (AnimationDrawable) ivLeft
                .getDrawable();
        animDrawable.start();*/
    }

    public void setCommonTitle(int LeftVisibility, int centerTextVisibility, int rightVisibility) {
        ivLeft.setVisibility(LeftVisibility);
        tv_right.setVisibility(rightVisibility);
        CenterTitle.setVisibility(centerTextVisibility);
    }

    /**
     * @Description: 设置左边按钮图片
     */
    public void setIvLeft(int icon) {
        ivLeft.setImageResource(icon);
    }

    /**
     * @Description: 设置右边按钮背景图片
     */
    public void setTvRightBackGround(int icon) {
        tv_right.setBackgroundResource(icon);
    }

    /**
     * @Description: 设置右边按钮文字
     */
    public void setTvRightName(String str) {
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(str);
    }

    public void setTvRightColor(int color){
        tv_right.setTextColor(color);
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

    public void setLayoutBackgroundColor(int color) {
        rl_layout.setBackgroundColor(color);
    }

    public void setLayoutBackgroundResource(int resid) {
        rl_layout.setBackgroundResource(resid);
    }

    /**
     * @Description: 右边按钮点击事件
     */
    public void setRightOnclickListener(OnClickListener listener) {
        tv_right.setOnClickListener(listener);
    }

    public ImageView getIvleft() {
        return ivLeft;
    }

    public void setIvLeft(ImageView ivLeft) {
        this.ivLeft = ivLeft;
    }

    public TextView getTvRight() {
        return tv_right;
    }

    public void setTvRight(TextView tv_right) {
        this.tv_right = tv_right;
    }

    public TextView getCenterTitle() {
        return CenterTitle;
    }

    public void setCenterTitle(TextView centerTitle) {
        CenterTitle = centerTitle;
    }

    public void hideTvRight() {
        if (tv_right != null) {
            tv_right.setVisibility(View.GONE);
        }
    }

    public void showTvRight() {
        if (tv_right != null) {
            tv_right.setVisibility(View.VISIBLE);
        }
    }
}

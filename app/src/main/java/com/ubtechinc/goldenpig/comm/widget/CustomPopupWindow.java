package com.ubtechinc.goldenpig.comm.widget;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.utils.AnimUtil;

/**
 * Created by haili on 2018/12/19.
 */

public class CustomPopupWindow extends PopupWindow {

    private static final String TAG = CustomPopupWindow.class.getSimpleName();

    private CustomPopupWindow mPopupWindow;

    private AnimUtil animUtil;
    private float bgAlpha = 1f;
    private boolean bright = false;

    private static final long DURATION = 500;
    private static final float START_ALPHA = 0.7f;
    private static final float END_ALPHA = 1f;

    private Activity context;

    private TextView tvVal = null;
    private String mShowVal = null;

    private Callback mCallback = null;

    public CustomPopupWindow(Activity context) {
        super(context);
        this.context = context;

        mPopupWindow = this;
        animUtil = new AnimUtil();

        init();
    }

    private void init() {

        // 设置布局文件
        mPopupWindow.setContentView(LayoutInflater.from(context).inflate(R.layout.pop_view, null));
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        mPopupWindow.setAnimationStyle(R.style.pop_view);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        //mPopupWindow.showAsDropDown(iv_add, -100, 0);
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                toggleBright();

                mCallback.onDismiss();
                mCallback = null;
            }
        });

        tvVal = mPopupWindow.getContentView().findViewById(R.id.tv_val);
        tvVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"tvVal");
                mPopupWindow.dismiss();
            }
        });
    }

    public CustomPopupWindow setCallback(Callback callback){
        mCallback = callback;
        return mPopupWindow;
    }

    public CustomPopupWindow setShowVal(String value) {
        mShowVal = value;
        tvVal.setText(TextUtils.isEmpty(mShowVal) ? "" : mShowVal);
        return mPopupWindow;
    }

    public CustomPopupWindow showAtBottom(View view) {
        Log.d(TAG,"showAtBottom");
        //弹窗位置设置
        mPopupWindow.showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 5);
//        toggleBright();
        return mPopupWindow;
    }

    private void toggleBright() {

        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgAlpha);
            }
        });
        animUtil.addEndListner(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                // 在一次动画结束的时候，翻转状态
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    /**
     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;

        context.getWindow().setAttributes(lp);
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public interface Callback{
        void onDismiss();
    }
}

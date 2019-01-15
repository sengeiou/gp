package com.ubtechinc.goldenpig.comm.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseDialog;

/**
 * @auther :hqt
 * @email :qiangta.huang@ubtrobot.com
 * @description :项目基础Dialog，统一设置背景和布局样式
 * @time :2018/8/24 14:08
 * @change :
 * @changetime :2018/8/24 14:08
 */
public class UBTSubTitleDialog extends BaseDialog implements View.OnClickListener {
    private OnUbtDialogClickLinsenter onUbtDialogClickLinsenter;
    private OnUbtDialogContentClickLinsenter onUbtDialogContentClickLinsenter;
    private Button mLeftBtn, mRightBtn;
    private TextView mTipsTv;  //对话框提示语
    private int mLeftBtnColor = -1, mRightBtnColor = -1;
    private TextView mSubTipsView;
    private TextView mRadioTip;
    private View ubtBtnDecor;

    public UBTSubTitleDialog(@NonNull Context context) {
        this(context, 0);
    }

    public UBTSubTitleDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected UBTSubTitleDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.dialog_ubt_subtitle, null);

        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);

        setCanceledOnTouchOutside(false);

        this.setContentView(root);
        ubtBtnDecor = findViewById(R.id.ubt_btn_decor);
        mSubTipsView = findViewById(R.id.ubt_tv_dialog_subtips);
        mRadioTip = findViewById(R.id.ubt_tv_dialog_notip);
        mRadioTip.setOnClickListener(this);

        mLeftBtn = (Button) findViewById(R.id.ubt_dialog_left_btn);
        if (mLeftBtnColor != -1) {
            mLeftBtn.setTextColor(mLeftBtnColor);
        }
        mLeftBtn.setOnClickListener(this);

        mRightBtn = (Button) findViewById(R.id.ubt_dialog_right_btn);
        if (mRightBtnColor != -1) {
            mRightBtn.setTextColor(mRightBtnColor);
        }
        mRightBtn.setOnClickListener(this);
    }

    public void showNoTip(boolean isShow) {
        if (mRadioTip != null) {
            mRadioTip.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    public void setRadioText(String value) {
        if (mRadioTip != null) {
            mRadioTip.setVisibility(View.VISIBLE);
            mRadioTip.setText(value);
        }
    }

    public void setRadioSelected(boolean isSelect) {
        if (mRadioTip != null) {
            mRadioTip.setSelected(true);
        }
    }

    public void setTips(String tips) {
        if (null == mTipsTv) {
            mTipsTv = (TextView) findViewById(R.id.ubt_tv_dialog_tips);
        }
        mTipsTv.setText(tips);
    }

    public void setLeftButtonTxt(String btnStr) {
        if (mLeftBtn != null) {
            mLeftBtn.setText(btnStr);
        }
    }

    public void setRightButtonTxt(String btnStr) {
        if (mRightBtn != null) {
            mRightBtn.setText(btnStr);
        }
    }

    public OnUbtDialogClickLinsenter getOnUbtDialogClickLinsenter() {
        return onUbtDialogClickLinsenter;
    }

    public void setOnUbtDialogClickLinsenter(OnUbtDialogClickLinsenter onUbtDialogClickLinsenter) {
        this.onUbtDialogClickLinsenter = onUbtDialogClickLinsenter;
    }

    public void setOnUbtDialogContentClickLinsenter(OnUbtDialogContentClickLinsenter onUbtDialogContentClickLinsenter) {
        this.onUbtDialogContentClickLinsenter = onUbtDialogContentClickLinsenter;
    }

    public void setSubTips(String subTips) {
        if (mSubTipsView != null) {
            mSubTipsView.setText(subTips);
        }
    }

    public void setSubTipGravity(int gravity) {
        if (mSubTipsView != null) {
            mSubTipsView.setGravity(gravity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ubt_dialog_left_btn:
                if (onUbtDialogClickLinsenter != null) {
                    onUbtDialogClickLinsenter.onLeftButtonClick(v);
                }
                dismiss();
                break;
            case R.id.ubt_dialog_right_btn:
                if (onUbtDialogClickLinsenter != null) {
                    onUbtDialogClickLinsenter.onRightButtonClick(v);
                }
                dismiss();
                break;
            case R.id.ubt_tv_dialog_notip:
                mRadioTip.setSelected(!mRadioTip.isSelected());
                if (onUbtDialogContentClickLinsenter != null) {
                    onUbtDialogContentClickLinsenter.onNotipClick(v);
                }
                break;
        }
    }

    public void setLeftBtnColor(int color) {
        if (mLeftBtn != null) {
            mLeftBtn.setTextColor(color);
        }
        mLeftBtnColor = color;
    }

    public void setSubTipColor(int color) {
        if (mSubTipsView != null) {
            mSubTipsView.setTextColor(color);
        }
    }

    public void setRightBtnColor(int color) {
        if (mRightBtn != null) {
            mRightBtn.setTextColor(color);
        }
        mRightBtnColor = color;
    }

    public interface OnUbtDialogClickLinsenter {
        void onLeftButtonClick(View view);

        void onRightButtonClick(View view);
    }

    public interface OnUbtDialogContentClickLinsenter {
        void onNotipClick(View view);
    }

    public void setOnlyOneButton() {
        if (mLeftBtn != null) {
            mLeftBtn.setVisibility(View.GONE);
        }
        if (ubtBtnDecor != null) {
            ubtBtnDecor.setVisibility(View.GONE);
        }
    }
}

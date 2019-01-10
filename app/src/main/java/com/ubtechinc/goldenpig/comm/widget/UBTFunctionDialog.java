package com.ubtechinc.goldenpig.comm.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseDialog;

/**
 * @auther :zzj
 * @email :zhijun.zhou@ubtrobot.com
 * @Description: :功能性弹框
 * @time :2019/1/10 11:32
 * @change :
 * @changetime :2019/1/10 11:32
 */
public class UBTFunctionDialog extends BaseDialog implements View.OnClickListener {
    private OnUbtDialogClickLinsenter onUbtDialogClickLinsenter;
    private TextView mTvFunc1, mTvFunc2;
    private View mIvClose;
    private View ubtBtnDecor;
    private TextView mTipsTv;  //对话框提示语
    private int mLeftBtnColor = -1, mRightBtnColor = -1;

    public UBTFunctionDialog(@NonNull Context context) {
        this(context, 0);
    }

    public UBTFunctionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected UBTFunctionDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.dialog_ubt_function, new LinearLayout(getContext()));

        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);

        setCanceledOnTouchOutside(false);

        this.setContentView(root);
        mTvFunc1 = findViewById(R.id.tv_dialog_tv_1);
        ubtBtnDecor = findViewById(R.id.ubt_btn_decor);
        if (mLeftBtnColor != -1) {
            mTvFunc1.setTextColor(mLeftBtnColor);
        }
        mTvFunc1.setOnClickListener(this);

        mTvFunc2 = findViewById(R.id.tv_dialog_tv_2);
        if (mRightBtnColor != -1) {
            mTvFunc2.setTextColor(mRightBtnColor);
        }
        mTvFunc2.setOnClickListener(this);

        mIvClose = findViewById(R.id.iv_dialog_close);
        mIvClose.setOnClickListener(this);
    }

    public void setTips(String tips) {
        if (null == mTipsTv) {
            mTipsTv = findViewById(R.id.ubt_tv_dialog_tips);
        }
        mTipsTv.setText(tips);
    }

    public void setFunc1Txt(String value) {
        if (mTvFunc1 != null) {
            mTvFunc1.setText(value);
        }
    }

    public void setFunc2Txt(String value) {
        if (mTvFunc2 != null) {
            mTvFunc2.setText(value);
        }
    }

    public void showCloseIcon(boolean isShow) {
        if (mIvClose != null) {
            mIvClose.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    public OnUbtDialogClickLinsenter getOnUbtDialogClickLinsenter() {
        return onUbtDialogClickLinsenter;
    }

    public void setOnUbtDialogClickLinsenter(OnUbtDialogClickLinsenter onUbtDialogClickLinsenter) {
        this.onUbtDialogClickLinsenter = onUbtDialogClickLinsenter;
    }

    @Override
    public void onClick(View v) {
        if (onUbtDialogClickLinsenter != null) {
            switch (v.getId()) {
                case R.id.tv_dialog_tv_1:
                    onUbtDialogClickLinsenter.onFunc1Click(v);
                    break;
                case R.id.tv_dialog_tv_2:
                    onUbtDialogClickLinsenter.onFunc2Click(v);
                    break;
                case R.id.iv_dialog_close:
                    onUbtDialogClickLinsenter.onClose(v);
                    break;
            }
        }
        dismiss();
    }

    public interface OnUbtDialogClickLinsenter {
        void onFunc1Click(View view);

        void onFunc2Click(View view);

        void onClose(View view);
    }
}

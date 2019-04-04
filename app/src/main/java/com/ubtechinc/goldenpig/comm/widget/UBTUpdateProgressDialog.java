package com.ubtechinc.goldenpig.comm.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ubtechinc.commlib.log.UbtLogger;
import com.ubtechinc.commlib.view.UbtProgressBar;
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
public class UBTUpdateProgressDialog extends BaseDialog implements View.OnClickListener {
    private OnUbtDialogClickLinsenter onUbtDialogClickLinsenter;
    private OnUbtDialogContentClickLinsenter onUbtDialogContentClickLinsenter;

    private TextView mTipsTv;  //对话框提示语
    private SeekBar mSeekBar;





    public UBTUpdateProgressDialog(@NonNull Context context) {
        this(context, 0);
    }

    public UBTUpdateProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected UBTUpdateProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.dialog_ubt_update_progress, null);
        mSeekBar = root.findViewById(R.id.sk_update_progress);
        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);
        setCanceledOnTouchOutside(false);
        this.setContentView(root);

    }




    public void setTips(String tips) {
        if (null == mTipsTv) {
            mTipsTv = (TextView) findViewById(R.id.ubt_tv_dialog_tips);
        }
        mTipsTv.setText(tips);
    }

    public void updateProgress(int progress){
        if(null == mSeekBar){
            mSeekBar = (SeekBar) findViewById(R.id.sk_update_progress);
        }
        UbtLogger.d("UBTUpdateProgressDialog", "updateProgress:" + progress );
        mSeekBar.setMax(100);
        mSeekBar.setProgress(progress);
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

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface OnUbtDialogClickLinsenter {
        void onLeftButtonClick(View view);

        void onRightButtonClick(View view);
    }

    public interface OnUbtDialogContentClickLinsenter {
        void onNotipClick(View view);
    }


}

package com.ubtechinc.goldenpig.comm.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseDialog;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :项目基础Dialog，统一设置背景和布局样式
 *@time          :2018/8/24 14:08
 *@change        :
 *@changetime    :2018/8/24 14:08
*/
public class UBTBaseDialog extends BaseDialog implements View.OnClickListener{
    private OnUbtDialogClickLinsenter onUbtDialogClickLinsenter;
    private Button mLeftBtn,mRightBtn;
    private TextView mTipsTv;  //对话框提示语
    private int mLeftBtnColor=-1,mRightBtnColor=-1;
    public UBTBaseDialog(@NonNull Context context) {
        this(context,0);
    }

    public UBTBaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected UBTBaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init(){
        View root = View.inflate(getContext(), R.layout.dialog_ubt_base, null);

        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);


        this.setContentView(root);
        mLeftBtn=(Button) findViewById(R.id.ubt_dialog_left_btn);
        if (mLeftBtnColor!=-1){
            mLeftBtn.setTextColor(mLeftBtnColor);
        }
        mLeftBtn.setOnClickListener(this);

        mRightBtn=(Button)findViewById(R.id.ubt_dialog_right_btn);
        if (mRightBtnColor!=-1){
            mRightBtn.setTextColor(mRightBtnColor);
        }
        mRightBtn.setOnClickListener(this);
    }
    public void setTips(String tips){
        if (null==mTipsTv){
            mTipsTv=(TextView)findViewById(R.id.ubt_tv_dialog_tips);
        }
        mTipsTv.setText(tips);
    }

    public void setLeftButtonTxt(String btnStr){
        if (mLeftBtn!=null){
            mLeftBtn.setText(btnStr);
        }
    }
    public void setRightButtonTxt(String btnStr){
        if (mRightBtn!=null){
            mRightBtn.setText(btnStr);
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
        if (onUbtDialogClickLinsenter!=null) {
            switch (v.getId()) {
                case R.id.ubt_dialog_left_btn:
                    onUbtDialogClickLinsenter.onLeftButtonClick(v);
                    break;
                case R.id.ubt_dialog_right_btn:
                    onUbtDialogClickLinsenter.onRightButtonClick(v);
                    break;
            }
        }
        dismiss();
    }
    public void setLeftBtnColor(int color){
        if (mLeftBtn!=null){
            mLeftBtn.setTextColor(color);
        }
        mLeftBtnColor=color;
    }
    public void setRightBtnColor(int color){
        if (mRightBtn!=null){
            mRightBtn.setTextColor(color);
        }
        mRightBtnColor=color;
    }
    public interface OnUbtDialogClickLinsenter{
        void onLeftButtonClick(View view);
        void onRightButtonClick(View view);
    }
}

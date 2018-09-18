package com.ubtechinc.goldenpig.comm.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.ubtechinc.commlib.view.UbtClearableEditText;
import com.ubtechinc.goldenpig.R;
import com.ubtechinc.goldenpig.base.BaseDialog;
/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :可编辑对话框
 *@time          :2018/9/18 16:03
 *@change        :
 *@changetime    :2018/9/18 16:03
*/
public class UbtEditDialog extends BaseDialog implements View.OnClickListener{
    private OnEnterClickListener onClickListener;
    private Button mLeftBtn,mRightBtn;
    private TextView mTipsTv;  //对话框提示语
    private UbtClearableEditText mClearEdt;
    public UbtEditDialog(@NonNull Context context) {
        this(context,0);
    }

    public UbtEditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        inits();
    }

    protected UbtEditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        inits();
    }

    private void inits(){
        View root = View.inflate(getContext(), R.layout.dialog_edtable, null);

        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.ubt_transparent);
        this.setContentView(root);
        mLeftBtn=(Button) findViewById(R.id.ubt_dialog_left_btn);
        mLeftBtn.setOnClickListener(this);

        mRightBtn=(Button)findViewById(R.id.ubt_dialog_right_btn);
        mRightBtn.setOnClickListener(this);

        mTipsTv=findViewById(R.id.ubt_hotspot_title);
        mClearEdt=(UbtClearableEditText)findViewById(R.id.ubt_edt_dialog);
    }
    public void setTipsTxt(String tipsTxt){
        if (mTipsTv!=null){
            mTipsTv.setText(tipsTxt);
        }
    }
    public void setRawTxt(String rawTxt){
        if (mClearEdt!=null){
            mClearEdt.setText(rawTxt);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        showKeyboard();
    }

    public void showKeyboard() {
        if(mClearEdt!=null){
            //设置可获得焦点
            mClearEdt.setFocusable(true);
            mClearEdt.setFocusableInTouchMode(true);
            //请求获得焦点
            mClearEdt.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) mClearEdt
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mClearEdt, 0);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ubt_dialog_left_btn:
                dismiss();
                break;
            case  R.id.ubt_dialog_right_btn:
                if (onClickListener!=null){
                    onClickListener.onEnterClick(v,mClearEdt.getText().toString());
                }
                dismiss();
                break;
        }
    }

    public void setOnEnterClickListener(OnEnterClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnEnterClickListener{
        void onEnterClick(View view,String newStr);
    }
}

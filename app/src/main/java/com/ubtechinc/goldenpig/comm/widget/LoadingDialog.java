package com.ubtechinc.goldenpig.comm.widget;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.goldenpig.R;


public class LoadingDialog extends Dialog {

    public static LoadingDialog mDia;
    public static final int CHECK_TIME_OUT = 1000;
    private int mTimeout = 20000;
    private static Boolean showToast = false;

    public LoadingDialog setShowToast(Boolean showToast) {
        this.showToast = showToast;
        return mDia;
    }

    public LoadingDialog setTimeout(int timeout) {
        this.mTimeout = timeout * 1000;
        return mDia;
    }

    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    public void show() {
        try {
            super.show();
            if (mHandler.hasMessages(CHECK_TIME_OUT)) {
                mHandler.removeMessages(CHECK_TIME_OUT);
            }
            mHandler.sendEmptyMessageDelayed(CHECK_TIME_OUT, mTimeout);// 20s 秒后检查加载框是否还在
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private LoadingDialog(Context context) {
        super(context, R.style.UBTLoaddialogTheme);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static LoadingDialog getInstance(Context _context) {
        if (mDia != null && mDia.isShowing()) {
            /*在dimiss一个dialog之前，必须确保所在的Activity没有被销毁，否则会出现崩溃异常*/
            try {
                mDia.cancel();
            } catch (Exception e) {
            }
        }
        showToast = false;
        mDia = new LoadingDialog(_context);
        mDia.initDia();
        return mDia;
    }

    public static void dissMiss() {
        try {
            if (mDia != null && mDia.isShowing())
                mDia.cancel();
            if (mHandler.hasMessages(CHECK_TIME_OUT)) {
                mHandler.removeMessages(CHECK_TIME_OUT);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            mDia = null;
        }

    }

    private void initDia() {

        View root = View.inflate(getContext(), R.layout.dialog_loading, new LinearLayout
                (getContext()));
//		txt_dia_msg = (TextView) root.findViewById(R.id.txt_dia_msg);
        this.setContentView(root);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
//        this.getWindow().setBackgroundDrawable(colorDrawable);
        setTranslucentStatus(false);
        this.setCancelable(false);
    }

    private static Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CHECK_TIME_OUT:
                    if (mDia != null && mDia.isShowing()) {
                        try {
                            mDia.cancel();
                            if (showToast) {
                                ToastUtils.showShortToast("操作失败，请重试");
                            }
                        } catch (Exception e) {

                        } finally {
                            mDia = null;
                        }
                    }
                    mDia = null;
                    break;
            }
        }
    };

}

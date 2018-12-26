package com.ubtechinc.goldenpig.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ubtechinc.commlib.utils.ContextUtils;
import com.ubtechinc.goldenpig.R;

public class BaseDialog extends Dialog {

    private Context mContext;

    public BaseDialog(@NonNull Context context) {
        this(context, -1);
//        setDialogWidth();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
//        setDialogWidth();
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
//        setDialogWidth();
    }

    @Override
    public void show() {
        setDialogWidth();
        super.show();
    }

    private void setDialogWidth(){
        if (ContextUtils.isContextExisted(mContext)) {
            if (mContext instanceof Activity) {
                setOwnerActivity((Activity) mContext);
                WindowManager wm = (WindowManager) getOwnerActivity().getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;         // 屏幕宽度（像素）
//                int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.width =  width-(2*(getOwnerActivity().getResources().getDimensionPixelSize(R.dimen.ubt_dialog_margin_horizontal)));
                getWindow().setAttributes(params);
            }
        }
    }
}

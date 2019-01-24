package com.ubtechinc.goldenpig.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtechinc.goldenpig.R;

public class UbtToastUtils {

    private static Toast mToast;

    public static void showCustomToast(Context context, String tips) {
        View customView;
        if (mToast == null) {
            mToast = new Toast(context.getApplicationContext());
            customView = LayoutInflater.from(context).inflate(R.layout.toast_customer, null);
        } else {
            customView = mToast.getView();
            mToast.cancel();
            mToast = new Toast(context.getApplicationContext());
        }
        TextView tv = customView.findViewById(R.id.ubt_tv_toast_tips);
        tv.setText(tips);
        mToast.setView(customView);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}

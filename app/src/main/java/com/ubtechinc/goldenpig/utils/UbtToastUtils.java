package com.ubtechinc.goldenpig.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtechinc.goldenpig.R;

public class UbtToastUtils {

    public static void showCustomToast(Context context, String tips) {
        Toast customToast = new Toast(context.getApplicationContext());
        View customView = LayoutInflater.from(context).inflate(R.layout.toast_customer,null);

        TextView tv = customView.findViewById(R.id.ubt_tv_toast_tips);
        tv.setText(tips);
        customToast.setView(customView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER,0,0);
        customToast.show();
    }

}

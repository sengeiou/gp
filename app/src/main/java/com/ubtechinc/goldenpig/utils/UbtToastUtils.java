package com.ubtechinc.goldenpig.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtechinc.goldenpig.R;

public class UbtToastUtils {
    public static void showCustomToast(Activity activity,String tips) {
        Toast customToast = new Toast(activity.getApplicationContext());
        View customView = LayoutInflater.from(activity).inflate(R.layout.toast_customer,null);

        TextView tv = (TextView) customView.findViewById(R.id.ubt_tv_toast_tips);
        tv.setText(tips);
        customToast.setView(customView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER,0,0);
        customToast.show();
    }
}

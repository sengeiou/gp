package com.ubtechinc.commlib.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :弹出Toast工具类
 *@time          :2018/8/22 9:52
 *@change        :
 *@changetime    :2018/8/22 9:52
*/
public class ToastUtils {
    private static ArrayList<WeakReference<Toast>> toastList = new ArrayList<WeakReference<Toast> >();

    private static void showToast(Context context, String content) {
        cancelAll();
        Toast toast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        WeakReference<Toast> toastWeakReference=new WeakReference<>(toast);
        toastList.add(toastWeakReference);
        toast.show();
    }

    public static void showShortToast(Context context,int resId){
        String content=resId<1?"":context.getResources().getString(resId);
        showShortToast(context,content);
    }

    public static void showShortToast(Context context,String content){
        showToast(context,content,Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context,int resId){

        String content=resId<1?"":context.getResources().getString(resId);
        showShortToast(context,content);
    }

    public static void showLongToast(Context context,String content){
        showToast(context,content,Toast.LENGTH_LONG);
    }

    private static void showToast(Context context,String content, int duration){
        if (ContextUtils.isContextExisted(context)) {
            if (duration != 0 && duration != 1) {
                duration = 0;
            }
            Toast toast = Toast.makeText(context, content, duration);
            toast.show();
        }
    }
    public static void cancelAll() {
        if (!toastList.isEmpty()){
            for (WeakReference<Toast> weakReference : toastList) {
                if (weakReference!=null){
                   final Toast toast= weakReference.get();
                   if (toast!=null){
                       toast.show();
                   }
                }

            }
            toastList.clear();
        }
    }
}

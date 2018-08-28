package com.ubtechinc.goldenpig.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ubtechinc.commlib.utils.ContextUtils;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :图片加载封装类
 *@time          :2018/8/23 16:52
 *@change        :
 *@changetime    :2018/8/23 16:52
*/
public class ImageUtils {
    public static void showGif(Activity activity, ImageView gifView, int gifId){
        if (ContextUtils.isContextExisted(activity)&&gifView!=null){
            Glide.with(activity).load(gifId).into(gifView);
        }
    }
    public static void destroyGif(Activity activity){
        Glide.with(activity).onDestroy();
    }
}

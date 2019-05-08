package com.ubtechinc.tvlloginlib.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ubt on 2017/8/25.
 */

public class SharedPreferencesUtils {

    private final static String SP_NAME = "tvs_info";
    private final static String WX_OPENID = "wx_openid";
    private final static String QQ_OPENID = "qq_openid";
    /**
     * 是否首次打开众创空间
     */
    public final static String CREATE = "inter_create";
    /**
     * 是否打开过蒙层
     */
    public final static String CREATEGUIDE = "create_guide";
    /**
     * 是否打开过定制问答蒙层
     */
    public final static String INTERLOCTIONGUIDE = "interloction_guide";

    public static String getString(Context context, String name, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, defaultValue);
    }

    public static void putString(Context context, String name, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String name, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, defaultValue);
    }

    public static void putBoolean(Context context, String name, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static void putLong(Context context, String name, long longs){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(name, longs);
        editor.commit();
    }

    public static long getLong(Context context, String name, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(name, defaultValue);
    }


    public static String getWeixinOpenId(Context context) {
        return getString(context, WX_OPENID, null);
    }

    public static void putWeixinOpenId(Context context, String weiXinId) {
        putString(context, WX_OPENID, weiXinId);
    }

    public static String getQQOpenId(Context context) {
        return getString(context, QQ_OPENID, null);
    }

    public static void putQQOpenId(Context context, String weiXinId) {
        putString(context, QQ_OPENID, weiXinId);
    }

    public static boolean getHidePresent(Context context, String robotId) {
        if(robotId == null) {
            return false;
        }
        return getBoolean(context, robotId, false);
    }

    public static void putHidePresent(Context context, String robotId) {
        if(robotId == null) {
            return;
        }
        putBoolean(context, robotId, true);
    }
}

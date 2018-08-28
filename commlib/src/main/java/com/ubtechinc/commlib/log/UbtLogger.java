package com.ubtechinc.commlib.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 *@ des         日志工具类，可根据是否Realease版本显示，
 *              或当前信息级别显示
 * @author  黄强太
 *
 */
public class UbtLogger {
    private static boolean isLoggable=false; //是否显示Log
    private UbtLogger() {
        throw new UnsupportedOperationException(" 没有正确调用，初始化");
    }
    public static void init(Context context){
        try {
            ApplicationInfo info = context.getApplicationInfo();
            isLoggable= (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    public static void i(String tag,String msg){
        i(tag,msg,null);
    }
    public static void i(String tag,String msg,Throwable throwable){
        if (msg==null)
            msg="";
        if (isLoggable) {
            Log.i(tag, "\n--------------------------------------------------------------------------------------------------");
            Log.i(tag, "|" + Thread.currentThread().getName());
            Log.i(tag, "--------------------------------------------------------------------------------------------------");
            if (throwable==null) {
                Log.i(tag, msg);
            }else {
                Log.i(tag,msg,throwable);
            }
            Log.i(tag, "--------------------------------------------------------------------------------------------------");
        }
    }
    public static void e(String tag,String msg){
         e(tag,msg,null);
    }
    public static void e(String tag,String msg,Throwable throwable){
        if (msg==null)
            msg="";
        if (isLoggable) {
            Log.e(tag, "\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            Log.e(tag, "|" + Thread.currentThread().getName());
            Log.e(tag, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            if (throwable==null) {
                Log.e(tag, msg);
            }else {
                Log.e(tag,msg,throwable);
            }
            Log.e(tag, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }
    public static void d(String tag,String msg){
         d(tag,msg,null);
    }
    public static void d(String tag,String msg,Throwable throwable){
        if (msg==null)
            msg="";
        if (isLoggable) {
            Log.d(tag, "\n**************************************************************************************************");
            Log.d(tag, "|" + Thread.currentThread().getName());
            Log.d(tag, "**************************************************************************************************");
            if (throwable==null) {
                Log.d(tag, msg);
            }else {
                Log.d(tag,msg,throwable);
            }
            Log.d(tag, "**************************************************************************************************");
        }
    }
    public static void w(String tag,String msg){
         w(tag,msg,null);
    }
    public static void w(String tag,String msg,Throwable throwable){
        if (msg==null)
            msg="";
        if (isLoggable) {
            Log.w(tag, "\n...................................................................................................");
            Log.w(tag, "|" + Thread.currentThread().getName());
            Log.w(tag, "...................................................................................................");
            if (throwable==null) {
                Log.w(tag, msg);
            }else {
                Log.w(tag,msg,throwable);
            };
            Log.w(tag, "...................................................................................................");
        }
    }
}

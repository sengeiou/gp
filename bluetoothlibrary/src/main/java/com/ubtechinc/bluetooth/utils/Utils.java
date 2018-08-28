package com.ubtechinc.bluetooth.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ubtechinc.bluetooth.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author：wululin
 * @date：2017/10/20 15:06
 * @modifier：ubt
 * @modify_date：2017/10/20 15:06
 * [A brief description]
 * version
 */

public class Utils {
    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;
    private static final String TAG = "Utils";
    public static String pactkCommandToRobot(int command){
        JSONObject sendJsonObj = new JSONObject();
        try {
            sendJsonObj.put(Constants.DATA_COMMAND, command);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObj.toString();
    }
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 组装向机器人发送clinetId的指令
     * @param clientId
     * @return
     */
    public static String pactkClientIdCommandToRobot(String clientId){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.DATA_COMMAND,Constants.CLIENT_ID_TRANS);
            jsonObject.put(Constants.CLIENTID,clientId);
            return jsonObject.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            Log.v("isFastDoubleClick", "短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }



    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 跳转到权限设置界面
     */
    public static  void getAppDetailSettingIntent(Context context){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 9){
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if(Build.VERSION.SDK_INT <= 8){
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public static void saveImageToAblum(Context context, File file) {



         /*将数据插入多媒体数据库中，相册会自动刷新界面显示新增的相册*/
        ContentValues localContentValues = new ContentValues();

        localContentValues.put("_data", file.toString());

        localContentValues.put("mime_type", "image/jpeg");

        ContentResolver localContentResolver = context.getContentResolver();

        Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        localContentResolver.insert(localUri, localContentValues);
    }
}

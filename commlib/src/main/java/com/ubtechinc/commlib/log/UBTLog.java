package com.ubtechinc.commlib.log;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日志，日志默认会存储在SDcar里。
 * 1,单个日志文件最大2M。
 * 2，日志最多保存7天。
 *
 */
public class UBTLog  {


    private static String UBT_LOG_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/ubtech/log/";  //log文件保存路径
    private static String UBT_LOG_NAME = "log";

    private static OutputStreamWriter writer; //文件输出流
    private static SimpleDateFormat sdfDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd");  //文件名日期格式
    private static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");  //log内容日期格式

    private static int LOG_FILE_MAX_SIZE = 2*1024*1024; // 2M
    private static byte mLogFileIndex = 0;
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 7;  //7  天
    public static boolean isDebug = true; //是否开启输出到控制台的LOG
    private static boolean isWriteToFile = true; //是否将日志保存到文件


    public static void Init() {
        deleteSDcardExpiredLog();
    }

    public static void d(String tag, String msg) {
        if(isDebug){
            Log.d(tag, msg);
        }

        if(isWriteToFile){
            writeToFile(tag, msg);
        }

    }

    public static void i(String tag, String msg) {
        if(isDebug){
            Log.i(tag, msg);
        }

        if(isWriteToFile){
            writeToFile(tag, msg);
        }

    }

    public static void v(String tag, String msg) {
        if(isDebug){
            Log.v(tag, msg);
        }

        if(isWriteToFile){
            writeToFile(tag, msg);
        }

    }

    public static void e(String tag, String msg) {
        if(isDebug){
            Log.e(tag, msg);
        }

        if(isWriteToFile){
            writeToFile(tag, msg);
        }

    }


    /**
     * 日志输出到文件
     * @param tag
     * @param msg
     */
    private static void writeToFile(String tag, String msg) {
        File file = new File(UBT_LOG_PATH);
        OutputStreamWriter writer = null;
        if (!file.exists()) {
            file.mkdir();
            file.exists();
        }
        file = getLogFile();
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file, true));
            writer.write(sdf.format(new Date(System.currentTimeMillis())));
            writer.write(" "+tag+" : " + msg);
            writer.write("\n");
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file != null) {
                file.exists();
            }
        }
    }

    /**
     * 获取可写入的文件
     * @return
     */
    private static File getLogFile() {
        File file = null;
        try {
            UBT_LOG_NAME = sdfDateFormat.format(new Date()) + "_"
                    + mLogFileIndex + ".log";
            file = new File(UBT_LOG_PATH + UBT_LOG_NAME);
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() >= LOG_FILE_MAX_SIZE) {
                mLogFileIndex++;
                UBT_LOG_NAME = sdfDateFormat.format(new Date()) + "_"
                        + mLogFileIndex + ".log";
                file = new File(UBT_LOG_PATH + UBT_LOG_NAME);
                file.createNewFile();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;

    }

    /**
     * 删除SDCard下过期的日志
     */
    private static void deleteSDcardExpiredLog() {
        File file = new File(UBT_LOG_PATH);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                if (UBT_LOG_NAME.equals(fileName)) {
                    continue;
                }
                String createDateInfo = fileName.substring(0,10);
                if (canDeleteSDLog(createDateInfo, fileName)) {
                    d("zz", "删除日志文件:"+logFile.getName());
                    logFile.delete();
                }
            }
        }
    }

    /**
     * 判断sdcard上的日志文件是否可以删除
     *
     * @param createDateStr
     * @return
     */
    private static boolean canDeleteSDLog(String createDateStr, String absFilePath) {
        boolean canDel = false;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * SDCARD_LOG_FILE_SAVE_DAYS);// 删除7天之前日志
        Date expiredDate = calendar.getTime();
        try {
            Date createDate = sdfDateFormat.parse(createDateStr);
            canDel = createDate.before(expiredDate);
        } catch (ParseException e) {
            e.printStackTrace();
            File file = new File(UBT_LOG_PATH+absFilePath);
            file.delete();
            canDel = false;
        }

        return canDel;
    }


}

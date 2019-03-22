package com.ubtechinc.goldenpig.utils;

import android.app.Activity;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ubtechinc.commlib.utils.ContextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    public static String getCameraPath() {
        String path = getDCIMFilePath();
        File f = new File(path, "Camera");
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getPath();
    }

    public static String getDCIMFilePath() {
        if (checkSDCard()) {
            // Log.d("hdf_text", "HaveSDCard()");
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            // sdPath += "/Download";
            // sdPath += UMConstants.Configure.DOWNLOAD_PATH;
            sdPath += "DCIM";
            File file = new File(sdPath);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            return sdPath;
        } else {
            // Log.d("hdf_text", "NoSDCard()");
            File sdPath = new File(getExternalSdCardPath(), "DCIM");
            String dirPath = sdPath + "";
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            return dirPath;
        }
    }

    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取扩展SD卡存储目录
     * <p>
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录 否则：返回内置SD卡目录
     *
     * @return
     */
    public static String getExternalSdCardPath() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sdCardFile.getAbsolutePath();
        }
        // Log.d("hdf_text", "fdfdfdfd");
        String path = null;

        File sdCardFile = null;

        ArrayList<String> devMountList = getDevMountList();

        for (String devMount : devMountList) {
            File file = new File(devMount);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
                File testWritable = new File(path, "ckg_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
            return sdCardFile.getAbsolutePath();
        }

        return null;
    }

    private static ArrayList<String> getDevMountList() {
        String[] toSearch = null;
        try {
            toSearch = readFile("/etc/vold.fstab").split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    public static String readFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            //res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String generateFileName(String type) {
        StringBuffer stringBuffer =
                new StringBuffer(String.valueOf(System.currentTimeMillis()));
        stringBuffer.append(String.valueOf((int) (Math.random() * 1000)));
        stringBuffer.append(type);
        return stringBuffer.toString();
    }
}

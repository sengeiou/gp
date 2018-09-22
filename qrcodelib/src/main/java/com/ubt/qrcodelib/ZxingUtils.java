package com.ubt.qrcodelib;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :二维码生成工具
 *@time          :2018/9/22 13:47
 *@change        :
 *@changetime    :2018/9/22 13:47
*/
public class ZxingUtils {
    public static Bitmap createBitmap(String str,int width,int height){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){ // ?
             return null;
        }
        return bitmap;
    }
}


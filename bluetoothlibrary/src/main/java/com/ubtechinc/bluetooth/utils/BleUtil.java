package com.ubtechinc.bluetooth.utils;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author：wululin
 * @date：2017/10/18 18:12
 * @modifier：ubt
 * @modify_date：2017/10/18 18:12
 * [A brief description]
 * version
 */

public class BleUtil {
    /**
     * 判断是否需要加密
     * @param scanRecord
     * @return
     */
    public static boolean needEncryption(byte[] scanRecord) {
        int index = 0;
        boolean flag = false;
        while (index < scanRecord.length) {
            int length = scanRecord[index++];
            if (length == 0) break;

            int type = scanRecord[index];
            if (type == 0) break;

            byte[] data = Arrays.copyOfRange(scanRecord, index+1, index+length);

            try {
                String decodedRecord = new String(data,"UTF-8");
                //Log.d(TAG, "decode :" + decodedRecord);
                if (type == 22 && !TextUtils.isEmpty(decodedRecord)){//检索"V"
                    int dex = decodedRecord.indexOf("V");
                    flag = dex != -1;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            index += length;
        }
        return flag;
    }





    /**
     * 更加rssi信号转换成距离
     * d=10^((ABS(RSSI)-A)/(10*n))、A 代表在距离一米时的信号强度(45 ~ 49), n 代表环境对信号的衰减系数(3.25 ~ 4.5)
     * @param rssi
     * @return
     */
    public static float getDistance(int rssi) {
        float A_Value = 53;
        float n_Value = 2.0f;
        int iRssi = Math.abs(rssi);
        float power = (iRssi-A_Value)/(10* n_Value);
        return (float) Math.pow(10,power);
    }

}

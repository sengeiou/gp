package com.ubtechinc.bluetooth.utils;

import android.text.TextUtils;
import android.util.Log;

import java.security.GeneralSecurityException;

/**
 * 蓝牙加密
 * Created by ubt on 2017/6/29.
 */

public class BLEcryption {
    private String TAG = getClass().getSimpleName();
    private String mPassWord;

    private boolean isNeedEncrpt = true;

    /**
     * 加密传输信息
     * @param data 需要加密的数据
     * @return  加密后的字符串
     */
    public String encryptionMessage(String data){
        if (!isNeedEncrpt){
            Log.d(TAG, "origin data :" + data + "need encrption :" + isNeedEncrpt);
            return data;
        }
        String messageAfterEncrypt;
        if (!TextUtils.isEmpty(mPassWord)){
            try {
                messageAfterEncrypt = AESCrypt.encrypt(mPassWord, data);
                //Log.d(TAG,"messageAfterEncrypt    " +messageAfterEncrypt);
                return messageAfterEncrypt;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            Log.d(TAG, "password is null!!");
            return null;
        }
    }

    public void setNeedEncrpt(boolean needEncrpt){
        this.isNeedEncrpt = needEncrpt;
    }

    /**
     * 收到的数据解密
     * @param data
     * @return
     */
    public String decryptionMessage(String data){
        if (!isNeedEncrpt){
            Log.d(TAG, "origin data :" + data + "need encrption :" + isNeedEncrpt);
            return data;
        }
        if (TextUtils.isEmpty(mPassWord)){
            Log.e(TAG, "password is null!!!first,you have to generatorPassWord!!!");
            return null;
        }
        String messageAfterDecrypt="";
        try {
            messageAfterDecrypt = AESCrypt.decrypt(mPassWord, data);
            //Log.d(TAG,"messageAfterDecrypt    " +messageAfterDecrypt  + "message origin data :" + data);
        }catch (GeneralSecurityException e){
            //handle error - could be due to incorrect password or tampered encryptedMsg
            Log.d(TAG, "data decrption wrong!!!" + data);
        }
        return  messageAfterDecrypt;
    }


    /**
     * 生成加密的秘钥Password
     * @param searialNumber 序列号
     */
    public void genatorPassWord(String searialNumber){
        if (TextUtils.isEmpty(searialNumber)){
            Log.e(TAG, "searialNumber is null!!!!!!!");
            return;
        }
        String partSearial = searialNumber.substring(searialNumber.length()-4);
        mPassWord = DesUtil.getMD5(partSearial, 32);//加密规则：序列号的后四位,经过MD5处理以后得到秘钥PassWord
        //Log.i(TAG, "searialNumber : " + searialNumber + "/n password : " + mPassWord);
    }

}

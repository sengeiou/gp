package com.ubtechinc.bluetooth.command;

import android.util.Log;

import com.ubtechinc.bluetooth.utils.BLEDataUtil;
import com.ubtechinc.bluetooth.utils.BLEcryption;

/**
 * @desc : Json形式加密和拆分编码
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/3
 */

public class JsonCommandEncode implements ICommandEncode{

    private static final String TAG = "JsonCommandEncode";
    protected BLEcryption blEcryption;
    private byte[] mResultByte = null;
    private boolean needEncrption;

    public JsonCommandEncode(String searialNumber, boolean needEncrption) {
        this.needEncrption = needEncrption;
        blEcryption = new BLEcryption();
        blEcryption.setNeedEncrpt(needEncrption);
        blEcryption.genatorPassWord(searialNumber);
    }

    @Override
    public String encryption(String content) {
        if(needEncrption) {
            return blEcryption.encryptionMessage(content);
        } else {
            return content;
        }
    }

    @Override
    public byte[][] encode(String content) {
        return BLEDataUtil.encode(content);
    }

    @Override
    public boolean addData(byte[] data) {
        mResultByte = BLEDataUtil.decode(data, mResultByte);//作为全局变量..
        return BLEDataUtil.isEnd(data);
    }

    @Override
    public String getCommand() {
        String result = new String(mResultByte);
        //Log.d(TAG, " result : " + result);
        String decryptionResult = blEcryption.decryptionMessage(result);
        mResultByte = null;
        return decryptionResult;
    }
}

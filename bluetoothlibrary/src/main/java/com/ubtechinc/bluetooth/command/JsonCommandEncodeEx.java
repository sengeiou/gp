package com.ubtechinc.bluetooth.command;

import android.util.Log;
import android.util.SparseArray;

/**
 * @desc : Json形式加密和拆分编码，文件头字节通过序号来定序
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/8
 */

public class JsonCommandEncodeEx extends JsonCommandEncode{

    /**最大发送20个字节，但是首个字节由标志位占用*/
    private static final int MAX_SIZE = 18;
    private static final int INVALID_SIZE = -1;
    private static final String TAG = "JsonCommandEncodeEx";
    private static final int BEGIN_INDEX = 1;

    private SparseArray<byte[]> sparseArray = new SparseArray<>();
    private String values;
    // 标志位最后一位为1的数据的index表示size
    private int size = INVALID_SIZE;

    public JsonCommandEncodeEx(String mSerialNumber, boolean needEncrption) {
        super(mSerialNumber, needEncrption);
    }

    @Override
    public byte[][] encode(String content) {
        try{
            byte[] originData = content.getBytes("UTF-8");
            int size = (int)Math.ceil(originData.length / (MAX_SIZE*1.0));
            byte[][] data = new byte[size][MAX_SIZE+1];

            int start = 0;
            int end = 0;
            int index  = 0;
            while(index < size) {
                index ++ ;
                data[index - 1][0] = (byte)(index << 1 | (index == size ? 0x01 : 0x00));
                Log.d(TAG, "encode before -- index " + index  + " index : " + getIndex(data[index - 1][0]));
                end = Math.min(start + MAX_SIZE, originData.length);
                System.arraycopy(originData, start, data[index-1], 1, end - start);
                start = end;
            }
            return data;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addData(byte[] data) {
        int index = getIndex(data[0]);
        Log.d(TAG, "addData -- index " + index + " count : " + getCount(data[0]));
        // 有重复的表示新的指令
        if(sparseArray.get(index) != null) {
            clear();
        }
        sparseArray.put(index, data);
        parseCount(data);
        if (isEnd(data[0])) {
            values = getCommandInner();
            clear();
            return true;
        }
        return super.addData(data);
    }

    private void clear() {
        sparseArray.clear();
        size = INVALID_SIZE;
    }

    private void parseCount(byte[] data) {
        boolean isEnd = (data[0] & 0x01) == 0x01;
        if(isEnd) {
            size = getIndex(data[0]);
        } else {
            return;
        }
        Log.d(TAG, " isEnd : " + isEnd + " size : " + size);
    }

    @Override
    public String getCommand() {
        return values;
    }

    private boolean isEnd(byte value) {
        Log.d(TAG, "isEnd ; sparseArray.size : " + sparseArray.size() + " size : " + size);
        return sparseArray.size() == size;
    }

    private boolean isStart(byte value) {
        return getIndex(value) == BEGIN_INDEX;
    }

    private int getIndex(byte value) {
        int index = value >> 1;
        return index;
    }

    private int getCount(byte value) {
        int count = value & 0x0f;
        Log.d(TAG, " getCount -- count : " + count);
        return count;
    }

    public String getCommandInner() {
        int size = sparseArray.size();
        byte[] bytes = null;
        for(int i = 1; i <= size; i ++) {
            bytes = decode(sparseArray.get(i), bytes);
        }
        String result = new String(bytes);
        String decryptionResult = blEcryption.decryptionMessage(result);
        Log.d(TAG, "getCommandInner -- decryptionResult : " + decryptionResult);
        return decryptionResult;
    }

    /**
     * 字节拼接
     * */
    public byte[] decode(byte[] data, byte[] result) {

        byte[] tempResult;

        if(result == null || isStart(data[0])) {
            tempResult = new byte[data.length - 1];
            System.arraycopy(data, 1, tempResult, 0, data.length-1);
        } else {
            tempResult = new byte[data.length - 1 + result.length];
            System.arraycopy(result, 0, tempResult, 0, result.length);
            System.arraycopy(data, 1, tempResult, result.length, data.length-1);
        }

        if(isEnd(data[0])) {
            //去掉结尾的0x00字符，避免转换成字符串乱码
            byte zeroByte = 0x00;
            int length = tempResult.length;
            for(int i=0; i<length; i++) {
                if(tempResult[length - i - 1] == zeroByte) {
                    continue;
                }

                byte[] realResult = new byte[length - i];
                System.arraycopy(tempResult, 0, realResult, 0, length - i);
                tempResult = realResult;
                break;
            }
        }

        return tempResult;
    }
}

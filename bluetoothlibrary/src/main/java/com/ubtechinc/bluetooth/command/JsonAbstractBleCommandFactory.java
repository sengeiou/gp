package com.ubtechinc.bluetooth.command;

/**
 * @desc : JSON类型数据工厂类
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/3
 */

public class JsonAbstractBleCommandFactory implements IAbstractBleCommandFactory{

    private String mSerialNumber;
    private boolean needEncrption;

    public JsonAbstractBleCommandFactory(String mSerialNumber, boolean needEncrption) {
        this.mSerialNumber = mSerialNumber;
        this.needEncrption = needEncrption;
    }

    @Override
    public ICommandProduce getCommandProduce() {
        return new JsonCommandProduce();
    }

    @Override
    public ICommandEncode getCommandEncode() {
        return new MiniBleProtoEncode();
    }
}

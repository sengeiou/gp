package com.ubtechinc.bluetooth.command;

/**
 * @desc : 蓝牙命令的抽象工厂
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/3
 */

public interface IAbstractBleCommandFactory {
    ICommandProduce getCommandProduce();
    ICommandEncode getCommandEncode();
}

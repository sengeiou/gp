package com.ubtechinc.bluetooth.command;

/**
 * @desc : 命令生成接口
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/3
 */

public interface ICommandProduce {

    /**
     *  获取ClientId接口
     * @param clientId clientId
     * @return 指令
     */
    String getClientId(String clientId);

    /**
     * 获取wifi在线状态
     * @return 指令
     */
    String getWifiState();

    /**
     * 获取蓝牙连接成功指令
     * @return 指令
     */
    String getBleSuc();

    /**
     * 获取wifi列表指令
     * @return 指令
     */
    String getWifiList();

    /**
     * 获取wifi密码信息指令
     */
    String getWifiPasswdInfo(String type, String name, String passwd);

    /**
     * 获取手机网络无效
     */
    String getNetworkNotAvailable();
}

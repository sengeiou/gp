package com.ubtechinc.goldenpig.pigmanager;
/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :扫描蓝牙状态
 *@time          :2018/8/27 17:09
 *@change        :
 *@changetime    :2018/8/27 17:09
*/
public interface BleScanState {
    public static final  byte SCANNING=0;
    public static final  byte SCANNING_WITH_ROBOT=1;
    public static final  byte SCANINIG_STOPSCAN=2;
    public static final  byte CONNECT=3;
}

package com.ubtechinc.goldenpig.comm.view;

import android.net.wifi.ScanResult;
import android.view.View;

/**
 *@auther        :hqt
 *@email         :qiangta.huang@ubtrobot.com
 *@description   :
 *@time          :2018/8/31 14:45
 *@change        :
 *@changetime    :2018/8/31 14:45
*/
public interface OnUbtWifiListItemClickListener {
    void onClick(View view, ScanResult result);
}

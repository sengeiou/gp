package com.ubtechinc.bluetooth.command;

import com.ubtechinc.alpha.BleBindOrSwitchWifi;
import com.ubtechinc.protocollibrary.communit.ProtoBufferDisposer;
import com.ubtechinc.protocollibrary.protocol.CmdId;


public class ProtoAgent {
    public final static byte[] getData(int requestId, String content) {
        byte[] data = ProtoBufferDisposer.buildAlphaMessage(CmdId.BL_BIND_OR_SWITCH_WIFI_REQUEST, CmdId.IM_VERSION, requestId, 0, BleBindOrSwitchWifi.BindOrSwitchWifiRequest.newBuilder().setData(content).build());
        return data;

    }
}

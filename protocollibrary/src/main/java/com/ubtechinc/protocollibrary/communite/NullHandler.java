package com.ubtechinc.protocollibrary.communite;


import com.ubtechinc.protocollibrary.protocol.MiniMessage;

/**
 * Created by Administrator on 2017/5/25.
 */

public class NullHandler implements IMsgHandler {

    @Override
    public void handleMsg(short requestCmdId, short responseCmdId, MiniMessage requestBody, String peer) {

    }
}

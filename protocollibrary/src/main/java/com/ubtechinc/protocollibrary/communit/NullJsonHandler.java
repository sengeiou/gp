package com.ubtechinc.protocollibrary.communit;


import com.ubtechinc.protocollibrary.bean.IMJsonMsg;

/**
 * Created by Administrator on 2017/6/6.
 */

public class NullJsonHandler implements IMJsonMsgHandler {
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, IMJsonMsg jsonRequest, String peer) {

    }
}

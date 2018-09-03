package com.ubtechinc.bluetooth.command

import com.ubtech.utilcode.utils.ConvertUtils
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.alpha.BleBindOrSwitchWifi
import com.ubtechinc.protocollibrary.communit.ProtoBufferDisposer
import com.ubtechinc.protocollibrary.protocol.CmdId
import com.ubtechinc.protocollibrary.protocol.MiniBleProto
import com.ubtechinc.protocollibrary.protocol.MiniMessage
import com.ubtechinc.protocollibrary.protocol.PacketData
import java.util.concurrent.atomic.AtomicInteger

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/6/26 16:58
 */
class MiniBleProtoEncode : ICommandEncode {
    internal var requestSerial = AtomicInteger()
    internal var data : String = ""
    override fun encryption(content: String?): String? {
        return content
    }

    override fun encode(content: String?): Array<ByteArray> {
        val requestId = requestSerial.incrementAndGet()
        val data =ProtoAgent.getData(requestId,content)
        var divideData =  MiniBleProto.devide(data)
        var encodeData = emptyArray<ByteArray>()
        for (data in divideData) {
            encodeData+= data
        }
        return encodeData
    }

    override fun addData(data: ByteArray?): Boolean {
        if (data == null || data.isEmpty()) return false
        LogUtils.i( "handleProtoBuffMsg content =  " + ConvertUtils.bytes2HexString(data))
        val packetMessage = MiniBleProto.parsePacketMsg(data)
        // 先解析一次
        val (first, second) = MiniBleProto.unpack(packetMessage.dataContent)
        if (first == -1 || second.isEmpty()) {//如果不是一条命令则是数据包


            PacketData.get()!!.putBytes(packetMessage)
            val content = PacketData.get()!!.getBuffer()
            val (first1, second1) = MiniBleProto.unpack(content)
            if (first1 != -1 && second1.isNotEmpty()) {
                return parseMessage(second1)
            }


        } else {//组包完成
            return parseMessage(second)
        }
        return false
    }

    private fun parseMessage(dataPairs: Array<MiniMessage>) : Boolean {
        for (msgRequest in dataPairs) {
            if (msgRequest == null) {
                return false
            }

            val message = ProtoBufferDisposer.parseMessage(msgRequest.dataContent)
            val miniMessage = MiniMessage()
            miniMessage.responseSerial = message.header.responseSerial
            miniMessage.dataContent = message.bodyData.toByteArray()
            miniMessage.sendSerial = message.header.sendSerial
            miniMessage.commandId = message.header.commandId.toShort()
            if( miniMessage.commandId == CmdId.BL_BIND_OR_SWITCH_WIFI_RESPONSE) {
                var  response = ProtoBufferDisposer.unPackData(BleBindOrSwitchWifi.BindOrSwitchWifiResponse::class.java,  miniMessage.dataContent ) as BleBindOrSwitchWifi.BindOrSwitchWifiResponse?
                data = response!!.data
                return true
            }


        }
        PacketData.get()!!.clear()//
        return false
    }
    override fun getCommand(): String {

        return data
    }
}
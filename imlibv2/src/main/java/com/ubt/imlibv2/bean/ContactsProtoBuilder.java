package com.ubt.imlibv2.bean;

import com.google.protobuf.Any;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.improtolib.UserContacts;
import com.ubt.improtolib.UserRecords;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;

import com.ubtrobot.gold.GPCommons;
import com.ubtrobot.upgrade.VersionInformation;

import java.util.List;


public class ContactsProtoBuilder {
    /*  <call path="/im/mail/add"/>
    <call path="/im/mail/query"/>
    <call path="/im/mail/delete"/>
    <call path="/im/mail/update"/>
*/
    public static final String GET_VERSION_ACTION = "/upgrade_skill/get/current_version";
    public static final String GET_VERSION_STATE_ACTION = "/upgrade_skill/detect";
    public static final String UPATE_VERSION_ACTION = "/upgrade_skill/upgrade";

    public static final String UPAT_HOTSPOT = "/im/HotSpot/receiver"; /// 修改热点
    public static final String GET_HOTSPOT = "/im/HotSpot/Account"; ///查询热点

    public static byte[] getAddContactsInfo(String name, String number) {

        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/mail/add").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();

        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
        builder.setName(name);
        builder.setNumber(number);

        UserContacts.User user = builder.build();
        userContactBuilder.addUser(user);

        UserContacts.UserContact contact = userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer
                .ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }

    public static byte[] getUpdateContactsInfo(String name, String number, String key) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/mail/update").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();

        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
        builder.setName(name);
        builder.setNumber(number);
        builder.setId(Integer.valueOf(key));
        UserContacts.User user = builder.build();
        userContactBuilder.addUser(user);

        UserContacts.UserContact contact = userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer
                .ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }

    public static byte[] getDeleteContactsInfo(String name, String number, String key) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/mail/delete").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();

        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
        builder.setName(name);
        builder.setNumber(number);
        builder.setId(Integer.valueOf(key));
        UserContacts.User user = builder.build();
        userContactBuilder.addUser(user);

        UserContacts.UserContact contact = userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer
                .ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }

    public static byte[] getQueryData() {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/mail/query").setTime(System.currentTimeMillis()).build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer
                .ChannelMessage.newBuilder()
                .setHeader(header)
                .build();
        return channelMessage.toByteArray();
    }

    public static byte[] getQueryRecord() {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/record/query").setTime(System.currentTimeMillis()).build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer
                .ChannelMessage.newBuilder()
                .setHeader(header)
                .build();
        return channelMessage.toByteArray();
    }

    public static byte[] getDeleteRecordInfo(List<UserRecords.Record> list) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/record/delete").setTime(System.currentTimeMillis()).build();
        UserRecords.UserRecord.Builder userContactBuilder = UserRecords.UserRecord.newBuilder();
//        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
//        builder.setName(name);
//        builder.setNumber(number);
//        builder.setId(Integer.valueOf(key));
//        UserContacts.User user = builder.build();
        userContactBuilder.addAllRecord(list);

        UserRecords.UserRecord contact = userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer
                .ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }

    /*获取小猪版本命令*/
    public static byte[] getPigVersion() {

        return createBaseData(GET_VERSION_ACTION);

    }

    /*获取小猪升级状态命令*/
    public static byte[] getPigVersionState() {

        return createBaseData(GET_VERSION_STATE_ACTION);
    }

    /*小猪升级命令*/
    public static byte[] updatePigVersion() {
        return createBaseData(UPATE_VERSION_ACTION);
    }

    private static byte[] createBaseData(String action) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(action).setTime
                (System.currentTimeMillis()).build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .build();
        return channelMessage.toByteArray();
    }

    public static TIMMessage createTIMMsg(byte[] data) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();

        elem.setData(data);
        msg.addElement(elem);
        return msg;
    }

    public static byte[] getHotSpot() {
        return createBaseData(GET_HOTSPOT);
    }

    public static byte[] updateHotSpot(String hotName, String hotPwd) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(UPAT_HOTSPOT)
                .setTime(System.currentTimeMillis()).build();
        com.ubtrobot.gold.UserContacts.HotSpotMessage.Builder builder = com.ubtrobot.gold.UserContacts.HotSpotMessage
                .newBuilder();
        builder.setPassword(hotName);
        builder.setSsid(hotName);
        com.ubtrobot.gold.UserContacts.HotSpotMessage message = builder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(message))
                .build();
        return channelMessage.toByteArray();
    }

    public static byte[] getGuidIM() {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/GUID/Action").setTime(System.currentTimeMillis()).build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .build();
        return channelMessage.toByteArray();

    }
}

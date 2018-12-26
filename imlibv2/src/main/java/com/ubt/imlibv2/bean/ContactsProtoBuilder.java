package com.ubt.imlibv2.bean;

import com.google.protobuf.Any;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.improtolib.UserContacts;
import com.ubt.improtolib.UserRecords;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.channelservice.proto.GPADBSettingContainer;
import com.ubtrobot.channelservice.proto.GPRelationshipContainer;
import com.ubtrobot.tvs.proto.ClientIdUpdateContainer;

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

    public static final String GET_NATIVE_INFO = "/im/native_info"; //查询小猪基本信息

    public static final String IM_RELATIONSHIP_CHANGED = "/im/relationShip/changed";

    public static final String IM_ACCOUNT_CLIENTID = "/im/account/clientid";

    public static final String IM_SETTINGS_ABD = "/im/settings/adb";

    public static final String IM_RECORD_LATEST = "/im/record/latest";

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

    public static byte[] getLastRecord() {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/record/latest").setTime(System.currentTimeMillis()).build();
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

    /*获取八戒版本命令*/
    public static byte[] getPigVersion() {

        return createBaseData(GET_VERSION_ACTION);

    }

    /*获取八戒升级状态命令*/
    public static byte[] getPigVersionState() {

        return createBaseData(GET_VERSION_STATE_ACTION);
    }

    /*八戒升级命令*/
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

    public static TIMMessage createTIMMsg(String action) {
        return createTIMMsg(createBaseData(action));
    }

    /**
     * 1更新绑定关系 2更新配对关系 3全部更新
     * @param event
     * @return
     */
    public static byte[] syncPairInfo(int event) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(IM_RELATIONSHIP_CHANGED)
                .setTime(System.currentTimeMillis()).build();
        GPRelationshipContainer.RelationShip.Builder builder = GPRelationshipContainer.RelationShip.newBuilder();
        builder.setEvent(event);
        GPRelationshipContainer.RelationShip message = builder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(message))
                .build();
        return channelMessage.toByteArray();
    }

    public static byte[] getClientId(String clientId) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(IM_ACCOUNT_CLIENTID)
                .setTime(System.currentTimeMillis()).build();
        ClientIdUpdateContainer.ClientIdUpdate.Builder builder = ClientIdUpdateContainer.ClientIdUpdate.newBuilder();
        builder.setClientId(clientId);
        ClientIdUpdateContainer.ClientIdUpdate message = builder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(message))
                .build();
        return channelMessage.toByteArray();
    }

    public static byte[] adbOperate(boolean open) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(IM_SETTINGS_ABD)
                .setTime(System.currentTimeMillis()).build();
        GPADBSettingContainer.ADBSetting.Builder builder = GPADBSettingContainer.ADBSetting.newBuilder();
        builder.setStatus(open);
        GPADBSettingContainer.ADBSetting message = builder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(message))
                .build();
        return channelMessage.toByteArray();
    }

    /**
     * 查询小猪基本信息
     * @return
     */
    public static byte[] getNativeInfo(){
        return createBaseData(GET_NATIVE_INFO);
    }

    public static byte[] getHotSpot() {
        return createBaseData(GET_HOTSPOT);
    }

    public static byte[] updateHotSpot(String hotName, String hotPwd) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(UPAT_HOTSPOT)
                .setTime(System.currentTimeMillis()).build();
        com.ubtrobot.gold.UserContacts.HotSpotMessage.Builder builder = com.ubtrobot.gold.UserContacts.HotSpotMessage
                .newBuilder();
        builder.setPassword(hotPwd);
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

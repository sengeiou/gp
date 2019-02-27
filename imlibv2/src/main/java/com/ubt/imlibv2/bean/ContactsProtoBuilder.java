package com.ubt.imlibv2.bean;

import com.google.protobuf.Any;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.ubt.improtolib.UserContacts;
import com.ubt.improtolib.UserRecords;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;
import com.ubtrobot.channelservice.proto.GPADBSettingContainer;
import com.ubtrobot.channelservice.proto.GPRelationshipContainer;
import com.ubtrobot.clear.ClearContainer;
import com.ubtrobot.gold.GPSwitchContainer;
import com.ubtrobot.tvs.proto.ClientIdUpdateContainer;
import com.ubtrobot.wifi.WifiMessageContainer;

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
    public static final String UPATE_VERSION_RESULT_ACTION = "/upgrade_skill/get/upgradeResult";

    public static final String UPAT_HOTSPOT = "/im/HotSpot/receiver"; /// 修改热点
    public static final String GET_HOTSPOT = "/im/HotSpot/Account"; ///查询热点

    public static final String GET_NATIVE_INFO = "/im/native_info"; //查询小猪基本信息

    public static final String IM_RELATIONSHIP_CHANGED = "/im/relationShip/changed";

    public static final String IM_ACCOUNT_CLIENTID = "/im/account/clientid";

    public static final String IM_SETTINGS_ABD = "/im/settings/adb";

    public static final String IM_RECORD_LATEST = "/im/record/latest";

    /**
     * 连续语音对话
     */
    public static final String IM_DIALOG_SWITCH = "/im/dialog/switch";
    public static final String IM_DIALOG_REQUEST = "/im/dialog/request";

    /**
     * 八戒机器人信息
     */
    public static final String IM_DEVICE_INFO = "/im/device_info";

    /**
     * 机器人配网
     */
    public static final String IM_CONNECT_WIFI = "/connect/wifi";

    /**
     * 机器人wifi列表
     */
    public static final String IM_REQUEST_WIFI_LIST = "/request/wifi/list";

    /**
     * 清除本体信息
     */
    public static final String IM_CLEAR_REQUEST = "/im/clear/request";

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

    public static byte[] getAddContactsInfo(List<MyContact> users) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/mail/add").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();
        for (int i = 0; i < users.size(); i++) {
            UserContacts.User.Builder builder = UserContacts.User.newBuilder();
            builder.setName(users.get(i).lastname);
            builder.setNumber(users.get(i).mobile);
            UserContacts.User user = builder.build();
            userContactBuilder.addUser(user);
        }
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

    public static byte[] getDeleteContactsInfo(List<AddressBook> list) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder()
                .setAction("/im/mail/delete").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();
        for (int i = 0; i < list.size(); i++) {
            try{
                AddressBook book = list.get(i);
                UserContacts.User.Builder builder = UserContacts.User.newBuilder();
                builder.setName(book.nikeName);
                builder.setNumber(book.number);
                builder.setId(Integer.valueOf(book.userId));
                UserContacts.User user = builder.build();
                userContactBuilder.addUser(user);
            }catch (Exception e){
            }

        }
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

    /**
     * 获取八戒OTA升级结果
     * @return
     */
    public static byte[] getRobotUpdateResult() {
        return createBaseData(UPATE_VERSION_RESULT_ACTION);
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
     *
     * @param event
     * @return
     */
    public static byte[] syncPairInfo(int event) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction
                (IM_RELATIONSHIP_CHANGED)
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
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction
                (IM_ACCOUNT_CLIENTID)
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
     *
     * @return
     */
    public static byte[] getNativeInfo() {
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

    /**
     * 连续语音对话
     *
     * @return
     */
    public static byte[] requestContinuousVoiceState() {
        return createBaseData(IM_DIALOG_REQUEST);
    }

    public static byte[] requestContinuousVoiceSwitch(boolean state) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(IM_DIALOG_SWITCH)
                .setTime(System.currentTimeMillis()).build();
        GPSwitchContainer.Switch.Builder builder = GPSwitchContainer.Switch.newBuilder();
        builder.setState(state);
        GPSwitchContainer.Switch message = builder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(message))
                .build();
        return channelMessage.toByteArray();
    }

    /**
     * 获取八戒机器人信息
     *
     * @return
     */
    public static byte[] getPigDeviceInfo() {
        return createBaseData(IM_DEVICE_INFO);
    }

    /**
     * 获取八戒Wifi列表
     *
     * @return
     */
    public static byte[] getWifiList() {
        return createBaseData(IM_REQUEST_WIFI_LIST);
    }

    /**
     * 给机器人配网
     *
     * @param wifiName
     * @param wifiPwd
     * @param wifiCtype
     * @return
     */
    public static byte[] setWifi(String wifiName, String wifiPwd, String wifiCtype) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(IM_CONNECT_WIFI)
                .setTime(System.currentTimeMillis()).build();
        WifiMessageContainer.WifiMessage.Builder builder = WifiMessageContainer.WifiMessage.newBuilder();
        builder.setSsid(wifiName);
        builder.setPassword(wifiPwd);
        builder.setSecure(wifiCtype);
        WifiMessageContainer.WifiMessage message = builder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(message))
                .build();
        return channelMessage.toByteArray();
    }

    public static byte[] clearInfo(List<ClearContainer.Categories.Builder> categorys) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction(IM_CLEAR_REQUEST)
                .setTime(System.currentTimeMillis()).build();
        ClearContainer.Clear.Builder clearBuilder = ClearContainer.Clear.newBuilder();
        for (ClearContainer.Categories.Builder builder : categorys) {
            clearBuilder.addCategorie(builder.build());
        }
//        ClearContainer.Categories.Builder categoryBuilder1 = ClearContainer.Categories.newBuilder();
//        categoryBuilder1.setName("");
//        ClearContainer.Categories.Builder categoryBuilder2 = ClearContainer.Categories.newBuilder();
//        categoryBuilder2.setName("");
//        clearBuilder.addCategorie(categoryBuilder1.build());
//        clearBuilder.addCategorie(categoryBuilder2.build());
        ClearContainer.Clear clear = clearBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(clear))
                .build();
        return channelMessage.toByteArray();

    }

}

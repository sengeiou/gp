package com.ubt.im;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.ubt.improtolib.UserContacts;
import com.ubtrobot.channelservice.proto.ChannelMessageContainer;


public class ContactsProtoBuilder {
    /*  <call path="/im/mail/add"/>
    <call path="/im/mail/query"/>
    <call path="/im/mail/delete"/>
    <call path="/im/mail/update"/>
*/
    public static byte[] getAddContactsInfo(String name, String number) {

        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction("/im/mail/add").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();

        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
        builder.setName(name);
        builder.setNumber(number);

        UserContacts.User user = builder.build();
        userContactBuilder.addUser(user);

        UserContacts.UserContact contact=userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }
    public static byte[] getUpdateContactsInfo(String name, String number,String key) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction("/im/mail/delete").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();

        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
        builder.setName(name);
        builder.setNumber(number);
        builder.setId(Integer.valueOf(key));
        UserContacts.User user = builder.build();
        userContactBuilder.addUser(user);

        UserContacts.UserContact contact=userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }
    public static byte[] getDeleteContactsInfo(String name, String number,String key) {
        ChannelMessageContainer.Header header = ChannelMessageContainer.Header.newBuilder().setAction("/im/mail/delete").setTime(System.currentTimeMillis()).build();
        UserContacts.UserContact.Builder userContactBuilder = UserContacts.UserContact.newBuilder();

        UserContacts.User.Builder builder = UserContacts.User.newBuilder();
        builder.setName(name);
        builder.setNumber(number);
        builder.setId(Integer.valueOf(key));
        UserContacts.User user = builder.build();
        userContactBuilder.addUser(user);

        UserContacts.UserContact contact=userContactBuilder.build();
        ChannelMessageContainer.ChannelMessage channelMessage = ChannelMessageContainer.ChannelMessage.newBuilder()
                .setHeader(header)
                .setPayload(Any.pack(contact))
                .build();

        return channelMessage.toByteArray();
    }
}

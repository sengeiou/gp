package com.ubtechinc.goldenpig.message;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageModel implements Parcelable {
    public String id;
    public String title;
    public String url;
//    public String brief;
    public String content;
    public String imgUrl;
    public String status;
    public String createTime;
    /**
     * 0为否，1为是
     */
    public int type = 0;
    public int select = 0;

    public MessageModel() {
    }

    protected MessageModel(Parcel in) {
        id = in.readString();
        createTime = in.readString();
        status = in.readString();
        title = in.readString();
        imgUrl = in.readString();
        url = in.readString();
//        brief = in.readString();
        content = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(createTime);
        dest.writeString(status);
        dest.writeString(title);
        dest.writeString(imgUrl);
        dest.writeString(url);
//        dest.writeString(brief);
        dest.writeString(content);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };
}

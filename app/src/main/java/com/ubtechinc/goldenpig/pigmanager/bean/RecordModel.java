package com.ubtechinc.goldenpig.pigmanager.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RecordModel implements Parcelable {
    /**
     * 联系人名
     */
    public String name;
    /**
     * 电话？
     */
    public String number;
    /**
     * 数据id
     */
    public long id;
    /**
     * 通话时间
     */
    public long dateLong;
    /**
     * 通话类型：INCOMING_TYPE = 1, OUTGOING_TYPE = 2,MISSED_TYPE = 3,VOICEMAIL_TYPE = 4,REJECTED_TYPE
     * = 5
     */
    public int type;
    /**
     * 通话次数
     */
    public long duration;

    /**
     * 是否选中，在编辑中用到
     */
    public Boolean select = false;

    public int count = 1;

    public List<Long> ids;
    public RecordModel() {
    }

    protected RecordModel(Parcel in) {
        name = in.readString();
        number = in.readString();
        id = in.readLong();
        dateLong = in.readLong();
        type = in.readInt();
        count = in.readInt();
        duration = in.readLong();
        byte tmpSelect = in.readByte();
        select = tmpSelect == 0 ? null : tmpSelect == 1;
        ids = in.readArrayList(Long.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeLong(id);
        dest.writeLong(dateLong);
        dest.writeInt(type);
        dest.writeInt(count);
        dest.writeLong(duration);
        dest.writeByte((byte) (select == null ? 0 : select ? 1 : 2));
        dest.writeList(ids);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordModel> CREATOR = new Creator<RecordModel>() {
        @Override
        public RecordModel createFromParcel(Parcel in) {
            return new RecordModel(in);
        }

        @Override
        public RecordModel[] newArray(int size) {
            return new RecordModel[size];
        }
    };
}

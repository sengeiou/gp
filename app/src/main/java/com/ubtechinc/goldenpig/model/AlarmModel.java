package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class AlarmModel implements Parcelable {
    public String amOrpm;
    public String time;
    public String repeatDate;
    public long lAlarmId;
    public int eRepeatType;
    public long lStartTimeStamp;
    public String repeatName;
    /**
     * 0为否，1为是
     */
    public int select = 0;
    public AlarmModel() {
    }

    protected AlarmModel(Parcel in) {
        amOrpm = in.readString();
        time = in.readString();
        repeatDate = in.readString();
        lAlarmId = in.readLong();
        repeatName = in.readString();
        eRepeatType = in.readInt();
        lStartTimeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amOrpm);
        dest.writeString(time);
        dest.writeString(repeatDate);
        dest.writeLong(lAlarmId);
        dest.writeString(repeatName);
        dest.writeInt(eRepeatType);
        dest.writeLong(lStartTimeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlarmModel> CREATOR = new Creator<AlarmModel>() {
        @Override
        public AlarmModel createFromParcel(Parcel in) {
            return new AlarmModel(in);
        }

        @Override
        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };
}

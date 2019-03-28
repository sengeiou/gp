package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RemindModel implements Parcelable {
    public String amOrpm;
    public String time;
    public String repeatDate;
    public long lReminderId;
    public int eRepeatType;
    public long lStartTimeStamp;
    public String repeatName;
    public String sNote;
    public String date;
    /**
     * 0为否，1为是
     */
    public int select = 0;
    public RemindModel() {
    }

    protected RemindModel(Parcel in) {
        amOrpm = in.readString();
        time = in.readString();
        repeatDate = in.readString();
        lReminderId = in.readLong();
        eRepeatType = in.readInt();
        lStartTimeStamp = in.readLong();
        repeatName = in.readString();
        sNote = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amOrpm);
        dest.writeString(time);
        dest.writeString(repeatDate);
        dest.writeLong(lReminderId);
        dest.writeInt(eRepeatType);
        dest.writeLong(lStartTimeStamp);
        dest.writeString(repeatName);
        dest.writeString(sNote);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemindModel> CREATOR = new Creator<RemindModel>() {
        @Override
        public RemindModel createFromParcel(Parcel in) {
            return new RemindModel(in);
        }

        @Override
        public RemindModel[] newArray(int size) {
            return new RemindModel[size];
        }
    };
}

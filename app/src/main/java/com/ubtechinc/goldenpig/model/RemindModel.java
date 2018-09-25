package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RemindModel implements Parcelable {
    public String time_am;
    public String time;
    public String date;
    public String id;
    public String msg;
    public int type = 0;

    public RemindModel() {
    }

    protected RemindModel(Parcel in) {
        time_am = in.readString();
        time = in.readString();
        date = in.readString();
        id = in.readString();
        msg = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time_am);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(id);
        dest.writeString(msg);
        dest.writeInt(type);
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

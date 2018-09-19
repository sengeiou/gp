package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class AlarmModel implements Parcelable{
    public String time_state;
    public String time;
    public String date;
    public String id;


    public AlarmModel() {
    }


    protected AlarmModel(Parcel in) {
        time_state = in.readString();
        time = in.readString();
        date = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time_state);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(id);
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

package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RepeatModel implements Parcelable {
    public String name;
    public Boolean select = false;


    public RepeatModel() {
    }


    protected RepeatModel(Parcel in) {
        name = in.readString();
        byte tmpSelect = in.readByte();
        select = tmpSelect == 0 ? null : tmpSelect == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (select == null ? 0 : select ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RepeatModel> CREATOR = new Creator<RepeatModel>() {
        @Override
        public RepeatModel createFromParcel(Parcel in) {
            return new RepeatModel(in);
        }

        @Override
        public RepeatModel[] newArray(int size) {
            return new RepeatModel[size];
        }
    };
}

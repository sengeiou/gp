package com.ubt.imlibv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MyContact implements Parcelable {
    public String mobile;
    public String lastname;
    public String sortLetter;
    public Boolean select = false;

    public MyContact() {
    }

    protected MyContact(Parcel in) {
        mobile = in.readString();
        lastname = in.readString();
        sortLetter = in.readString();
    }

    public static final Creator<MyContact> CREATOR = new Creator<MyContact>() {
        @Override
        public MyContact createFromParcel(Parcel in) {
            return new MyContact(in);
        }

        @Override
        public MyContact[] newArray(int size) {
            return new MyContact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mobile);
        parcel.writeString(lastname);
        parcel.writeString(sortLetter);
    }
}

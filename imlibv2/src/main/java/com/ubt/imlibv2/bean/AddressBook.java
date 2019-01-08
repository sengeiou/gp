package com.ubt.imlibv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressBook implements Parcelable{
    public String nikeName;
    public String number;
    public String userId;

    public AddressBook() {

    }

    protected AddressBook(Parcel in) {
        nikeName = in.readString();
        number = in.readString();
        userId = in.readString();
    }

    public static final Creator<AddressBook> CREATOR = new Creator<AddressBook>() {
        @Override
        public AddressBook createFromParcel(Parcel in) {
            return new AddressBook(in);
        }

        @Override
        public AddressBook[] newArray(int size) {
            return new AddressBook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nikeName);
        dest.writeString(number);
        dest.writeString(userId);
    }
}

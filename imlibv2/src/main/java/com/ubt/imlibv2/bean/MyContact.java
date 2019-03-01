package com.ubt.imlibv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MyContact implements Parcelable {
    public String id;
    public String mobile;
    public String name;
    public String sortLetter;
    public String pinyin;
    public Boolean select = false;
    public List<String> numberList = new ArrayList<>();

    public MyContact() {
    }

    protected MyContact(Parcel in) {
        pinyin = in.readString();
        id = in.readString();
        mobile = in.readString();
        name = in.readString();
        sortLetter = in.readString();
        numberList = in.readArrayList(String.class.getClassLoader());
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
        parcel.writeString(pinyin);
        parcel.writeString(id);
        parcel.writeString(mobile);
        parcel.writeString(name);
        parcel.writeString(sortLetter);
        parcel.writeList(numberList);
    }
}

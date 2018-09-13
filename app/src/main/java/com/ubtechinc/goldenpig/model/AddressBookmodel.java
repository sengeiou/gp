package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class AddressBookmodel implements Parcelable, MultiItemEntity {
    public String name;
    public String phone;
    public int type=0;
    public AddressBookmodel() {
    }


    protected AddressBookmodel(Parcel in) {
        name = in.readString();
        phone = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressBookmodel> CREATOR = new Creator<AddressBookmodel>() {
        @Override
        public AddressBookmodel createFromParcel(Parcel in) {
            return new AddressBookmodel(in);
        }

        @Override
        public AddressBookmodel[] newArray(int size) {
            return new AddressBookmodel[size];
        }
    };

    @Override
    public int getItemType() {
        return type;
    }
}

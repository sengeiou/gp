package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QueriesModel implements Parcelable {
    public String strItemId;
    public String strQuery;

    public QueriesModel() {
    }

    protected QueriesModel(Parcel in) {
        strItemId = in.readString();
        strQuery = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strItemId);
        dest.writeString(strQuery);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QueriesModel> CREATOR = new Creator<QueriesModel>() {
        @Override
        public QueriesModel createFromParcel(Parcel in) {
            return new QueriesModel(in);
        }

        @Override
        public QueriesModel[] newArray(int size) {
            return new QueriesModel[size];
        }
    };
}

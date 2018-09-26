package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnswersModel implements Parcelable {
    public String strResourceUri;
    public String strText;
    public int iType;

    public AnswersModel() {
    }

    protected AnswersModel(Parcel in) {
        strResourceUri = in.readString();
        strText = in.readString();
        iType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strResourceUri);
        dest.writeString(strText);
        dest.writeInt(iType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnswersModel> CREATOR = new Creator<AnswersModel>() {
        @Override
        public AnswersModel createFromParcel(Parcel in) {
            return new AnswersModel(in);
        }

        @Override
        public AnswersModel[] newArray(int size) {
            return new AnswersModel[size];
        }
    };
}

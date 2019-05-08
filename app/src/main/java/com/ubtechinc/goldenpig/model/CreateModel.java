package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class CreateModel implements Parcelable, Comparable<CreateModel> {
    public int sid = -1;
    public String question;
    public String answer;
    public int type = 0;
    public long createTime;
    /**
     * 0为否，1为是
     */
    public int select = 0;

    public CreateModel() {
    }

    protected CreateModel(Parcel in) {
        sid = in.readInt();
        question = in.readString();
        answer = in.readString();
        createTime = in.readLong();
        type = in.readInt();
        select = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sid);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeLong(createTime);
        dest.writeInt(type);
        dest.writeInt(select);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreateModel> CREATOR = new Creator<CreateModel>() {
        @Override
        public CreateModel createFromParcel(Parcel in) {
            return new CreateModel(in);
        }

        @Override
        public CreateModel[] newArray(int size) {
            return new CreateModel[size];
        }
    };

    @Override
    public int compareTo(@NonNull CreateModel o) {
        try {
            if (createTime > o.createTime) {
                return -1;
            } else if (createTime < o.createTime) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

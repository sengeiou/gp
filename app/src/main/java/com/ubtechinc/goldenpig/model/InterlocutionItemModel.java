package com.ubtechinc.goldenpig.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class InterlocutionItemModel implements Parcelable {
    /**
     * 区分是否是头部
     */
    public int type = 0;
    public String iManageType;
    public String strDocId;
    public List<QueriesModel> vQueries = new ArrayList<>();
    public List<AnswersModel> vAnswers = new ArrayList<>();

    public InterlocutionItemModel() {
    }

    protected InterlocutionItemModel(Parcel in) {
        iManageType = in.readString();
        strDocId = in.readString();
        vQueries = in.createTypedArrayList(QueriesModel.CREATOR);
        vAnswers = in.createTypedArrayList(AnswersModel.CREATOR);
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iManageType);
        dest.writeString(strDocId);
        dest.writeTypedList(vQueries);
        dest.writeTypedList(vAnswers);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InterlocutionItemModel> CREATOR = new
            Creator<InterlocutionItemModel>() {
                @Override
                public InterlocutionItemModel createFromParcel(Parcel in) {
                    return new InterlocutionItemModel(in);
                }

                @Override
                public InterlocutionItemModel[] newArray(int size) {
                    return new InterlocutionItemModel[size];
                }
            };
}

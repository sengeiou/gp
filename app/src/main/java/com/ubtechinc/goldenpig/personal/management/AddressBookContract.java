package com.ubtechinc.goldenpig.personal.management;

import android.os.Handler;

import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.mvp.BasePresenter;
import com.ubtechinc.goldenpig.mvp.BaseView;

import java.util.List;

public class AddressBookContract {
    interface View extends BaseView {
        void onRefreshSuccess(List<AddressBookmodel> list);

        void onError(String str);
        Handler getHandler();
    }

    interface  Presenter extends BasePresenter<View> {
        void refreshData();
    }
}

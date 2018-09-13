package com.ubtechinc.goldenpig.personal.management;

import com.ubtechinc.goldenpig.model.AddressBookmodel;
import com.ubtechinc.goldenpig.mvp.BasePresenterImpl;

import java.util.ArrayList;
import java.util.List;

public class AddressBookPrestener extends BasePresenterImpl<AddressBookContract.View> implements
        AddressBookContract.Presenter {


    @Override
    public void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AddressBookmodel> list = new ArrayList<>();
                for (int i = 1; i < 11; i++) {
                    AddressBookmodel addressBookmodel = new AddressBookmodel();
                    addressBookmodel.name = "测试" + i;
                    addressBookmodel.phone = "1547784545";
                    list.add(addressBookmodel);
                }
                mView.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mView.onRefreshSuccess(list);
                    }
                }, 1500);
            }
        }).start();
    }
}

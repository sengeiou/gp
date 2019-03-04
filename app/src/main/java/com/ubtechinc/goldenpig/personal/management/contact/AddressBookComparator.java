package com.ubtechinc.goldenpig.personal.management.contact;


import com.ubt.imlibv2.bean.MyContact;
import com.ubtechinc.goldenpig.model.AddressBookmodel;

import java.text.Collator;
import java.util.Comparator;

/**
 *
 */
public class AddressBookComparator implements Comparator<AddressBookmodel> {
    Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);

    public int compare(AddressBookmodel o1, AddressBookmodel o2) {
        if (o1.pinyin.equals("@")
                || o2.pinyin.equals("#")) {
            return 1;
        } else if (o1.pinyin.equals("#")
                || o2.pinyin.equals("@")) {
            return -1;
        } else if (myCollator.compare(o1.pinyin, o2.pinyin) < 0) {
            return -1;
        } else if (myCollator.compare(o1.pinyin, o2.pinyin) > 0) {
            return 1;
        } else {
            return 0;
        }
    }

}

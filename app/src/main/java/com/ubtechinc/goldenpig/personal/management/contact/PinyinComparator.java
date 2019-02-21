package com.ubtechinc.goldenpig.personal.management.contact;


import com.ubt.imlibv2.bean.MyContact;

import java.util.Comparator;

/**
 *
 */
public class PinyinComparator implements Comparator<MyContact> {

    public int compare(MyContact o1, MyContact o2) {

        if (o1.sortLetter.equals("@")
                || o2.sortLetter.equals("#")) {
            return 1;
        } else if (o1.sortLetter.equals("#")
                || o2.sortLetter.equals("@")) {
            return -1;
        } else {
            return o1.sortLetter.compareTo(o2.sortLetter);
        }
    }

}

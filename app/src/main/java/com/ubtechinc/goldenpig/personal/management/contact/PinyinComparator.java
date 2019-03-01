package com.ubtechinc.goldenpig.personal.management.contact;


import com.ubt.imlibv2.bean.MyContact;

import java.text.Collator;
import java.util.Comparator;

/**
 *
 */
public class PinyinComparator implements Comparator<MyContact> {
    Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);

    public int compare(MyContact o1, MyContact o2) {
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
//        if (o1.pinyin.equals("@")
//                || o2.pinyin.equals("#")) {
//            return 1;
//        } else if (o1.pinyin.equals("#")
//                || o2.pinyin.equals("@")) {
//            return -1;
//        } else {
//            return o1.pinyin.compareTo(o2.pinyin);
//        }
    }

}

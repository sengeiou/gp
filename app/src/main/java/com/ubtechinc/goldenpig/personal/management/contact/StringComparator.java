package com.ubtechinc.goldenpig.personal.management.contact;


import java.util.Comparator;

/**
 *
 */
public class StringComparator implements Comparator<String> {

    public int compare(String o1, String o2) {

        if (o1.equals("@")
                || o2.equals("#")) {
            return 1;
        } else if (o1.equals("#")
                || o2.equals("@")) {
            return -1;
        } else {
            return o1.compareTo(o2);
        }
    }

}

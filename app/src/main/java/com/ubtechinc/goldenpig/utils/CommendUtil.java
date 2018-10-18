package com.ubtechinc.goldenpig.utils;

import android.text.TextUtils;

import com.ubtechinc.goldenpig.pigmanager.bean.RecordModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class CommendUtil {

    public static int getMsgLength(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        } else {
            return (str.getBytes().length - (str.getBytes().length - str.length()) / 2) / 2;
        }
    }

    public static List<RecordModel> handleList(List<RecordModel> list) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        ArrayList<RecordModel> result = new ArrayList<RecordModel>();
        for (int i = 0; i < list.size(); i++) {
            int k = 1;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).number.equals(list.get(j).number)) {
                    k++;
                } else {
                    continue;
                }
            }
            if (!map.containsKey(list.get(i).number)) {
                map.put(list.get(i).number, k);
            }

        }
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            for (int i = 0; i < map.size(); i++) {
                Map.Entry<String, Integer> next = iterator.next();
                for (int j = 0; j < list.size(); j++) {
                    if (next.getKey().equals(list.get(j).number)) {
                        list.get(j).count = next.getValue();
                        result.add(list.get(j));
                        break;
                    }
                }
            }


        }

        return removeDuplicate(result);
    }


    public static List<RecordModel> removeDuplicate(List<RecordModel> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).number.equals(list.get(i).number)) {
                    list.remove(j);
                }
            }
        }
        return list;
    }


    public static List<RecordModel> checkRecord(List<RecordModel> list) {
        List<RecordModel> result = new ArrayList<>();
        if (list.size() == 0) {
            return result;
        }
        result.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            for (int j = 0; j < result.size(); j++) {
                if (result.get(j).number.equals(list.get(i).number)
                        && result.get(j).type == list.get(i).type
                        && isSameDayOfMillis(result.get(j).dateLong, list.get(i).dateLong)) {
                    result.get(j).count++;
                    break;
                }
                if (j == result.size() - 1) {
                    result.add(list.get(i));
                    break;
                }
            }
        }
        return result;
    }

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    public static final int TIMEOUT = 15;
}

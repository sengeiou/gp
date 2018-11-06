package com.ubtechinc.nets.utils;

import java.util.Random;

public class RandomStringUtils {

    private static final String[] CS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static String randomAlphanumeric(int count) {
        int size = CS.length;
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            int ran = random.nextInt(size);
            stringBuffer.append(CS[ran]);
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        int c = 0;
        while(c < 50) {
            System.out.println(randomAlphanumeric(10));
            c++;
        }
    }
}

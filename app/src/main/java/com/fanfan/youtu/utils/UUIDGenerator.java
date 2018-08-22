package com.fanfan.youtu.utils;

import java.util.UUID;

/**
 * Created by android on 2017/12/19.
 */

public class UUIDGenerator {

    private UUIDGenerator() {
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    //获得指定数量的UUID
    public static String[] getUUID(int number) {
        if (number < 1) {
            return null;
        }
        String[] ss = new String[number];
        for (int i = 0; i < number; i++) {
            ss[i] = getUUID();
        }
        return ss;
    }

}

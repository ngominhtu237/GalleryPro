package com.ss.gallerypro.utils;

import java.util.ArrayList;
import java.util.Set;

public class DataTypeUtils {
    public static ArrayList<Integer> stringSetToArrayInteger(Set<String> a) {
        ArrayList<Integer> list = new ArrayList<>();
        for (String tmp : a) {
            list.add(Integer.parseInt(tmp));
        }
        return list;
    }
}

package com.ss.gallerypro.data.utils;

import android.os.Build;

import java.text.DecimalFormat;
import java.util.Arrays;

public class DataUtils {

    // Array Object -> Array String
    public static String[] getStringArgs(Object[] args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Arrays.stream(args).map(o -> o.toString()).toArray(String[]::new);

        String[] list = new String[args.length];
        for (int i = 0; i < args.length; i++) list[i] = String.valueOf(args[i]);
        return list;
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}

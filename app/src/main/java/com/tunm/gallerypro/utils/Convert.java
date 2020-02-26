package com.tunm.gallerypro.utils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Convert {
    /**
     * provides a String representation of the given time
     * @return {@code millis} in hh:mm:ss format
     */
    public static String convertSecondsToHMmSs(long millis) {
        long seconds = Math.round((double) millis / 1000);
        long hours = TimeUnit.SECONDS.toHours(seconds);
        if (hours > 0)
            seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = seconds > 0 ? TimeUnit.SECONDS.toMinutes(seconds) : 0;
        if (minutes > 0)
            seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static String Epoch2DateString(long epochSeconds) {
        return new SimpleDateFormat("MMM dd, yyyy").format(new java.util.Date (epochSeconds));
    }

    public static String formatEnumStringDialog(String inputString) {
        String str = inputString.replace('_',' ');
        str = str.toLowerCase();
        str = str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
        return str;
    }
}

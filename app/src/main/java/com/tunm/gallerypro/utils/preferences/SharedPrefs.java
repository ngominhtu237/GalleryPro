package com.tunm.gallerypro.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Set;

final class SharedPrefs {
    private static final String PREFERENCES_NAME = "com.gallerypro.SHARED_RPES";
    private static final int PREFERENCES_MODE = Context.MODE_PRIVATE;
    private final SharedPreferences sharedPrefs;

    SharedPrefs(@NonNull Context context) {
        sharedPrefs = context.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE);
    }

    private SharedPreferences.Editor getEditer() {
        return sharedPrefs.edit();
    }

    int get(@NonNull String key, int defaultValue) {
        return sharedPrefs.getInt(key, defaultValue);
    }

    boolean get(@NonNull String key, Boolean defaultValue) {
        return sharedPrefs.getBoolean(key, defaultValue);
    }

    void put(@NonNull String key, int value) {
        getEditer().putInt(key, value).commit();
    }

    void putSetString(@NonNull String key, Set<String> arrPosition) {
        getEditer().putStringSet(key, arrPosition).commit();
    }

    Set<String> getSetString(@NonNull String key, Set<String> defaultValue) {
        return sharedPrefs.getStringSet(key, defaultValue);
    }

    void put(@NonNull String key, boolean value) {
        getEditer().putBoolean(key, value).commit();
    }
}

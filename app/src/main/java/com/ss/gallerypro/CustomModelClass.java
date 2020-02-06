package com.ss.gallerypro;

import com.ss.gallerypro.setting.callback.ColumnChangeObserver;
import com.ss.gallerypro.setting.callback.ThemeChangeObserver;

import java.util.ArrayList;

// purpose: notify between two activity
public class CustomModelClass {
    private static CustomModelClass mInstance;
    private ArrayList<ThemeChangeObserver> themeChangeObservers = new ArrayList<>();
    private ArrayList<ColumnChangeObserver> columnChangeObservers = new ArrayList<>();

    private CustomModelClass() {}

    public static CustomModelClass getInstance() {
        if(mInstance == null) {
            mInstance = new CustomModelClass();
        }
        return mInstance;
    }

    public void addThemeChangeObserver(ThemeChangeObserver observer) {
        themeChangeObservers.add(observer);
    }

    public void addColumnChangeObserver(ColumnChangeObserver observer) {
        columnChangeObservers.add(observer);
    }

    public void applyThemeGlobal() {
        for(ThemeChangeObserver observer: themeChangeObservers) {
            observer.requestUpdateTheme();
        }
    }

    public void updateColumnGlobal() {
        for(ColumnChangeObserver observer: columnChangeObservers) {
            observer.requestUpdateColumn();
        }
    }
}

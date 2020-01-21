package com.ss.gallerypro;

import java.util.ArrayList;

// purpose: notify between two activity
public class CustomModelClass {
    private static CustomModelClass mInstance;
    private ArrayList<OnChooseColorListener> mListenerArr = new ArrayList<>();

    private CustomModelClass() {}

    public static CustomModelClass getInstance() {
        if(mInstance == null) {
            mInstance = new CustomModelClass();
        }
        return mInstance;
    }

    public void setListener(OnChooseColorListener listener) {
        mListenerArr.add(listener);
    }

    public void changeState() {
        for(OnChooseColorListener listener: mListenerArr) {
            listener.requestUpdateTheme();
        }
    }
}

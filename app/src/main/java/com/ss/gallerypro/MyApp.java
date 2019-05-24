package com.ss.gallerypro;

import android.app.Application;
import android.content.res.Configuration;

import com.ss.gallerypro.utils.preferences.Prefs;

public class MyApp extends Application {
    private static MyApp mInstance;

    public static MyApp getInstance() {
        return mInstance;
    }

    // Called when the application is starting, before any other application objects have been created.
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initialiseStorage();
    }

    private void initialiseStorage() {
        Prefs.init(this);
    }

    // Called by the system when the device configuration changes while your component is running.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

package com.tunm.gallerypro.data.file;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class FileObserverService extends Service {

    private static final String TAG = "FileObserverService";

    //Warning: If a FileObserver is garbage collected, it will stop sending events.
    //To ensure you keep receiving events, you must keep a reference to the FileObserver instance from some other live object.
    public FileObserver observer;

    public final static String UPDATE_ACTIVITY = "UPDATE_ACTIVITY";
    private boolean isFileObserver;

    public FileObserverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        watchPathAndWriteFile();
        Log.v(TAG, "onStartCommand");
        return START_STICKY;
    }

    // 1) watch path for files changes using "RecursiveFileObserver"
    public void watchPathAndWriteFile() {
        // the following path depends on your Android device. On my system, it is: "/storage/emulated/0/"
        String path = Environment.getExternalStorageDirectory().getPath() + "/";
        observer = new RecursiveFileObserver(path, type -> {
            isFileObserver = true;
            Intent intentUpdateUI = new Intent(UPDATE_ACTIVITY);
            intentUpdateUI.putExtra("isFileObserver", isFileObserver);
            sendBroadcast(intentUpdateUI);
        });
        Log.v("tunm1_time", "startWatching: " + System.currentTimeMillis());

        // start watching the path
        new Thread(new Runnable() {
            @Override
            public void run() {
                observer.startWatching();
            }
        }).start();

        Log.v("tunm1_time", "startWatchingDone: " + System.currentTimeMillis());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
}

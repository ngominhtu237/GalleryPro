package com.tunm.gallerypro.data.file;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
            Log.v(TAG, "startMyOwnForeground");
        } else {
            startForeground(1, new Notification());
            Log.v(TAG, "startForeground");
        }
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
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

        // fix while screen when start App because service run in UIThread -> move to background thread
        new Thread(() -> observer.startWatching()).start();

        Log.v("tunm1_time", "startWatchingDone: " + System.currentTimeMillis());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
}

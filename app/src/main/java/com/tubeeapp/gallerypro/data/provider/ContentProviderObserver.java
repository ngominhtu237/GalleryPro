package com.tubeeapp.gallerypro.data.provider;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class ContentProviderObserver extends ContentObserver {

    private ProviderChangeListener changeListener;

    public ContentProviderObserver() {
        super(new Handler());
    }

    public void setChangeListener(ProviderChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.v("ContentProviderObserver", "content provider change");
        changeListener.onChange();
    }
}

package com.tunm.gallerypro;

import java.util.ArrayList;

public class DeleteMediaItemSubject {
    private static volatile DeleteMediaItemSubject mInstance;
    private ArrayList<DeleteMediaItemObserver> observers = new ArrayList<>();

    public static DeleteMediaItemSubject getInstance() {
        if (mInstance == null) {
            synchronized (DeleteMediaItemSubject.class) {
                if (mInstance == null) {
                    mInstance = new DeleteMediaItemSubject();
                }
            }
        }
        return mInstance;
    }

    public void registerObserver(DeleteMediaItemObserver observer) {
        observers.add(observer);
    }

    public void unRegisterObserver(DeleteMediaItemObserver observer) {
        observers.remove(observer);
    }

    public void notifyDataChange() {
        for(DeleteMediaItemObserver o: observers) {
            o.onDataChanged();
        }
    }
}

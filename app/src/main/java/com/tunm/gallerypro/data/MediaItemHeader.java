package com.tunm.gallerypro.data;

import java.util.ArrayList;

public class MediaItemHeader {

    private String mTitle;
    private ArrayList<MediaItem> mListItem;

    public MediaItemHeader() {
    }

    public MediaItemHeader(String mTitle, ArrayList<MediaItem> mListItem) {
        this.mTitle = mTitle;
        this.mListItem = mListItem;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public ArrayList<MediaItem> getListItem() {
        return mListItem;
    }

    public void setListItem(ArrayList<MediaItem> mListItem) {
        this.mListItem = mListItem;
    }
}

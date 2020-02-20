package com.tubeeapp.gallerypro.fragments.list.section.abstraction.model;

public class HeaderModel implements IItem {
    public String title;

    public HeaderModel(String title) {
        this.title = title;
    }

    @Override
    public boolean isHeader() {
        return true;
    }
}

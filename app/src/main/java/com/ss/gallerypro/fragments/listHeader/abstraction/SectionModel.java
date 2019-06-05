package com.ss.gallerypro.fragments.listHeader.abstraction;

public class SectionModel implements ItemInterface {
    public String title;

    public SectionModel(String title) {
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}

package com.tubeeapp.gallerypro.data.sort;

import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.Comparator;

public class PhotoComparators {

    static Comparator<MediaItem> comparator = null;

    public static Comparator<MediaItem> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {
        switch(sortingMode) {
            case NAME:
                comparator = getNameComparator();
                break;
            case SIZE:
                comparator = getSizeComparator();
                break;
            case LAST_MODIFIED:
                comparator = getDateModifiedComparator();
                break;
            case DATE_TAKEN: default:
                comparator = getDateTakenComparator();
                break;
        }
        return sortingOrder == SortingOrder.ASCENDING ? comparator : reverse(comparator);
    }

    private static Comparator<MediaItem> reverse(final Comparator<MediaItem> comparator) {
        return (o1, o2) -> comparator.compare(o2, o1);
    }

    private static Comparator<MediaItem> getDateTakenComparator() {
        return (i1, i2) -> {
            if(i1.getDateTaken() == null && i2.getDateTaken() == null) {
                return 0;
            } else if(i1.getDateTaken() != null && i2.getDateTaken() == null) {
                return 1;
            } else if(i1.getDateTaken() == null && i2.getDateTaken() != null) {
                return -1;
            }
            else return Long.valueOf(i1.getDateTaken()).compareTo(Long.valueOf(i2.getDateTaken()));
        };
    }

    private static Comparator<MediaItem> getDateModifiedComparator() {
        return (i1, i2) -> {
            if(i1.getDateModified() == null && i2.getDateModified() == null) {
                return 0;
            } else if(i1.getDateModified() != null && i2.getDateModified() == null) {
                return 1;
            } else if(i1.getDateModified() == null && i2.getDateModified() != null) {
                return -1;
            }
            else return Long.valueOf(i1.getDateModified()).compareTo(Long.valueOf(i2.getDateModified()));
        };
    }

    private static Comparator<MediaItem> getNameComparator() {
        return new Comparator<MediaItem>() {
            @Override
            public int compare(MediaItem s1, MediaItem s2) {
                return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
            }
        };
    }

    private static Comparator<MediaItem> getSizeComparator() {
        return new Comparator<MediaItem>() {
            @Override
            public int compare(MediaItem a1, MediaItem a2) {
                return Integer.parseInt(a1.getSize()) - Integer.parseInt(a2.getSize());
            }
        };
    }
}

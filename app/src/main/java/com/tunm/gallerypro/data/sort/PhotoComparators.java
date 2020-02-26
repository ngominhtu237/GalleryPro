package com.tunm.gallerypro.data.sort;

import com.tunm.gallerypro.data.MediaItem;

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
            String d1 = i1.getDateTaken(), d2 = i2.getDateTaken();
            if(d1 == null && d2 == null) {
                return 0;
            } else if(d1 != null && d2 == null) {
                return 1;
            } else if(d1 == null && d2 != null) {
                return -1;
            }
            return Long.valueOf(d1).compareTo(Long.valueOf(d2));
        };
    }

    private static Comparator<MediaItem> getDateModifiedComparator() {
        return (i1, i2) -> {
            String d1 = i1.getDateModified(), d2 = i2.getDateModified();
            if(d1 == null && d2 == null) {
                return 0;
            } else if(d1 != null && d2 == null) {
                return 1;
            } else if(d1 == null && d2 != null) {
                return -1;
            }
            return Long.valueOf(d1).compareTo(Long.valueOf(d2));
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

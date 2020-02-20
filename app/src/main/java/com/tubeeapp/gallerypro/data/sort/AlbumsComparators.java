package com.tubeeapp.gallerypro.data.sort;

import android.util.Log;

import com.tubeeapp.gallerypro.data.Bucket;

import java.util.Comparator;

public class AlbumsComparators {

    static Comparator<Bucket> comparator = null;

    public static Comparator<Bucket> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {

        switch(sortingMode) {
            case NAME:
                comparator = getNameComparator();
                break;
            case SIZE:
                comparator = getSizeComparator();
                break;
            case DATE_TAKEN: default:
                comparator = getDateTakenComparator();
                break;
        }

        return sortingOrder == SortingOrder.ASCENDING ? comparator : reverse(comparator);
    }

    private static Comparator<Bucket> reverse(final Comparator<Bucket> comparator) {
        return new Comparator<Bucket>() {
            @Override
            public int compare(Bucket o1, Bucket o2) {
                return comparator.compare(o2, o1);
            }
        };
    }

    private static Comparator<Bucket> getNameComparator() {
        return new Comparator<Bucket>() {
            @Override
            public int compare(Bucket s1, Bucket s2) {
                return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
            }
        };
    }

    private static Comparator<Bucket> getSizeComparator() {
        return new Comparator<Bucket>() {
            @Override
            public int compare(Bucket a1, Bucket a2) {
                return a1.getCount() - a2.getCount();
            }
        };
    }

    private static Comparator<Bucket> getDateTakenComparator(){
        return (a1, a2) -> {
            Log.v("AlbumsComparators", "compare, a1.dateTaken: " + a1.getDateTaken() + " - a2.dateTaken: " + a2.getDateTaken());
            String d1 = a1.getDateTaken(), d2 = a2.getDateTaken();
            if(d1 == null && d2 == null) {
                return 0;
            } else if (d1 == null) {
                return -1;
            } else if(d2 == null) {
                return 1;
            }
            return Long.valueOf(d1).compareTo(Long.valueOf(d2));
        };
    }
}

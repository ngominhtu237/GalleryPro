package com.tubeeapp.gallerypro.data.sort;

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
        return new Comparator<Bucket>() {
            @Override
            public int compare(Bucket a1, Bucket a2) {
                if(a1.getDateTaken() == null || a2.getDateTaken() == null) {
                    return 0;
                }
                else return Long.valueOf(a1.getDateTaken()).compareTo(Long.valueOf(a2.getDateTaken()));
            }
        };
    }
}

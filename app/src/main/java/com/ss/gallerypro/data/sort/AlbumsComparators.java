package com.ss.gallerypro.data.sort;

import com.ss.gallerypro.data.Bucket;

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
                return Long.compare(Long.parseLong(a1.getDateTaken()), Long.parseLong(a2.getDateTaken()));
            }

        };
    }
}

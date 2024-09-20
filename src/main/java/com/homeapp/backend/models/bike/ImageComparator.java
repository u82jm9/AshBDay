package com.homeapp.backend.models.bike;

import java.util.Comparator;

public class ImageComparator implements Comparator<Image> {

    @Override
    public int compare(Image o1, Image o2) {
        return o1.getComponent().compareTo(o2.getComponent());
    }
}

package org.paulsen.pilgrimage.model;

import java.io.Serializable;
import java.util.Comparator;

public interface BasicModel<T> extends Serializable {
    String getId();
    Class<T> type();
    //Comparator<T> defaultSorter();
}

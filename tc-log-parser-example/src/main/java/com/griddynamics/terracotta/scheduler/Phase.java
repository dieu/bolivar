package com.griddynamics.terracotta.scheduler;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:23:12
 */
public enum Phase {
    //COUNTING,
    REMOVING,
    DOWNLOADING,
    PARSING,
    AGGREGATING;
    public String toString() {
        return nameInLowerCase();
    }
    public String shortName() {
        return nameInLowerCase().substring(0, 2);
    }
    private String nameInLowerCase() {
        return name().toLowerCase();
    }
}

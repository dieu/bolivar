package com.griddynamics.terracotta.parser.separate;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:18:19
 */
public enum Phase {
    REMOVING("rem"),
    DOWNLOADING("dow"),
    PARSING("par"),
    RETURNING("ret"),
    DONE("fin"),
    ERROR("err");
    public final String tag;
    Phase(String tag) {
        this.tag = tag;
    }
}

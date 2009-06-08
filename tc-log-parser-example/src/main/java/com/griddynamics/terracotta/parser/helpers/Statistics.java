package com.griddynamics.terracotta.parser.helpers;

import java.util.Map;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:20:10
 */
public interface Statistics {
    void add(Map<String, Long> part);
    Map<String, Long> merge();
}


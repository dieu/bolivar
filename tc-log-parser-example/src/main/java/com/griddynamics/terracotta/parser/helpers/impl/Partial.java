package com.griddynamics.terracotta.parser.helpers.impl;

import com.griddynamics.terracotta.parser.helpers.Statistics;
import com.griddynamics.terracotta.util.NetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:21:37
 */
public class Partial implements Statistics {
    private ConcurrentMap<String, Map<String, Long>> parts = new ConcurrentStringMap<Map<String, Long>>();

    public void add(Map<String, Long> part) {
        parts.put(NetUtil.worker(), part);
    }

    public Map<String, Long> merge() {
        Statistics whole = new Monolitical();
        for (Map<String, Long> part : parts.values())
            whole.add(part);
        return whole.merge();
    }
}

package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.*;

import com.griddynamics.terracotta.parser.separate.Performance;
import com.griddynamics.terracotta.parser.separate.AveragePerformance;
import com.griddynamics.terracotta.parser.helpers.Statistics;
import com.griddynamics.terracotta.parser.helpers.impl.Partial;
import com.griddynamics.terracotta.util.NetUtil;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

/**
 * @author agorbunov @ 08.05.2009 15:10:15
 */
public class Aggregator {
    // Here we can see another Terracotta issue.
    // The classes below are actually private, but marked as public to allow Terracotta to instrument them.

    private ConcurrentMap<String, Performance> workers = new ConcurrentStringMap<Performance>();
//    private ConcurrentMap<String, Boolean> logIsParsed = new ConcurrentStringMap<Boolean>();
    private Statistics parts = new Partial();
    private transient Map<String, Long> whole;

    public void add(Map<String, Long> part) {
        parts.add(part);
    }

    public synchronized String ipWithMaxTraffic() {
        whole = parts.merge();
        return keyWithMaxValue(whole);
    }

    private String keyWithMaxValue(Map<String, Long> map) {
        Long maxValue = Long.MIN_VALUE;
        String maxKey = "?";
        for (String k : map.keySet()) {
            Long v = map.get(k);
            if (v > maxValue) {
                maxValue = v;
                maxKey = k;
            }
        }
        return maxKey;
    }

    public Long traffic(String ip) {
        return whole.get(ip);
    }

//    public void markAsParsed(String log) {
//        logIsParsed.put(log, Boolean.TRUE);
//    }
//
//    public Boolean isParsed(String log) {
//        return logIsParsed.containsKey(log);
//    }

    public void reportPerformance(Performance performance) {
        workers.put(NetUtil.worker(), performance);
    }

    public AveragePerformance averagePerformance() {
        return new AveragePerformance(workers.values());
    }

    public void countWorker() {
        //To change body of created methods use File | Settings | File Templates.
    }
}

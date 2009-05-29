package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.*;

import com.griddynamics.terracotta.parser.separate.ParseLogs.Performance;
import com.griddynamics.terracotta.parser.separate.ParseLogs.AveragePerformance;
import com.griddynamics.terracotta.util.NetUtil;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

/**
 * @author agorbunov @ 08.05.2009 15:10:15
 */
public class Aggregator {
    // Here we can see another Terracotta issue.
    // The classes below are actually private, but marked as public to allow Terracotta to instrument them.

    public static interface Statistics {
        void add(Map<String, Long> part);
        Map<String, Long> merge();
    }

    public static class Monolitical implements Statistics {
        private ConcurrentMap<String, Long> whole = new ConcurrentStringMap<Long>();

        public synchronized void add(Map<String, Long> part) {
            for (String ip : part.keySet()) {
                Long usage = part.get(ip);
                increaseTraffic(ip, usage);
            }
        }

        private void increaseTraffic(String ip, Long usage) {
            whole.putIfAbsent(ip, 0L);
            whole.put(ip, whole.get(ip) + usage);
        }

        public Map<String, Long> merge() {
            return whole;
        }
    }

    public static class Partial implements Statistics {
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

    private ConcurrentMap<String, Performance> workers = new ConcurrentStringMap<Performance>();
    private ConcurrentMap<String, Boolean> logIsParsed = new ConcurrentStringMap<Boolean>();
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

    public void markAsParsed(String log) {
        logIsParsed.put(log, Boolean.TRUE);
    }

    public Boolean isParsed(String log) {
        return logIsParsed.containsKey(log);
    }

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

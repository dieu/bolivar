package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
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
        private final ConcurrentMap<String, Long> whole = new ConcurrentHashMap<String, Long>();

        public synchronized void add(Map<String, Long> part) {
            for (String ip : part.keySet()) {
                Long traffic = part.get(ip);
                increaseTrafficUsage(ip, traffic);
            }
        }

        private void increaseTrafficUsage(String ip, Long traffic) {
            whole.putIfAbsent(ip, 0L);
            whole.put(ip, whole.get(ip) + traffic);
        }

        public Map<String, Long> merge() {
            return whole;
        }
    }

    public static class Partial implements Statistics {
        private ConcurrentMap<String, Map<String, Long>> parts = new ConcurrentStringMap<Map<String, Long>>();

        public void add(Map<String, Long> part) {
            parts.put(worker(), part);
        }

        private String worker() {
            return NetUtil.host();
        }

        public Map<String, Long> merge() {
            Statistics whole = new Monolitical();
            for (Map<String, Long> part : parts.values())
                whole.add(part);
            return whole.merge();
        }
    }

    private ConcurrentMap<String, Boolean> logIsParsed = new ConcurrentStringMap<Boolean>();
    private List<Performance> performance = Collections.synchronizedList(new LinkedList<Performance>());
    private Statistics parts = new Partial();
    private transient Map<String, Long> whole;

    public void add(Map<String, Long> part) {
        parts.add(part);
    }

    public synchronized String getIpWithMaxTraffic() {
        whole = parts.merge();
        Long maxTraffic = Long.MIN_VALUE;
        String maxIp = null;
        for (String ip : whole.keySet()) {
            Long traffic = whole.get(ip);
            if (maxTraffic < traffic) {
                maxTraffic = traffic;
                maxIp = ip;
            }
        }
        return maxIp;
    }

    public Long getTraffic(String ip) {
        return whole.get(ip);
    }

    public void markAsParsed(String log) {
        logIsParsed.put(log, Boolean.TRUE);
    }

    public Boolean isParsed(String log) {
        return logIsParsed.containsKey(log);
    }

    public void reportParsingPerformance(Performance p) {
        performance.add(p);
    }

    public AveragePerformance averageParsingPerformance() {
        return new AveragePerformance(performance);
    }
}

package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.*;

import com.griddynamics.terracotta.parser.separate_downloading.ParseLogs.Performance;
import com.griddynamics.terracotta.parser.separate_downloading.ParseLogs;


public class Aggregator {
    // These inner classes and interfaces are actually private,
    // but marked as public to allow Terracotta to instrument them.
    public static interface Statistics {
        void add(Map<String, Long> part);
        Map<String, Long> merge();
    }

    public static class Monolitical implements Statistics {
        private final ConcurrentMap<String, Long> whole = new ConcurrentHashMap<String, Long>();

        public synchronized void add(Map<String, Long> part) {
            for (String ip : part.keySet())
                increaseTrafficUsage(ip, part.get(ip));
        }

        private void increaseTrafficUsage(String ip, Long delta) {
            whole.putIfAbsent(ip, 0L);
            whole.put(ip, whole.get(ip) + delta);
        }

        public Map<String, Long> merge() {
            return whole;
        }
    }

    public static class Partial implements Statistics {
        private List<Map<String, Long>> parts = Collections.synchronizedList(new ArrayList<Map<String, Long>>());

        public void add(Map<String, Long> part) {
            parts.add(part);
        }

        public Map<String, Long> merge() {
            Statistics whole = new Monolitical();
            for (Map<String, Long> part : parts)
                whole.add(part);
            return whole.merge();
        }
    }

    private final List<Performance> parsingPerformance = Collections.synchronizedList(new LinkedList<Performance>());
    //private final ConcurrentMap<String, AtomicBoolean> logIsParsed = new ConcurrentHashMap<String, AtomicBoolean>();
    private final ConcurrentMap<String, Boolean> logIsParsed = new ConcurrentHashMap<String, Boolean>();
    private Statistics parts = new Partial();
    private transient Map<String, Long> whole;

    public void add(Map<String, Long> part) {
        parts.add(part);
    }

    public synchronized String getIpWithMaxTraffic() {
        whole = parts.merge();
        Long maxTraf = Long.MIN_VALUE;
        String maxIp = null;
        for (String ip : whole.keySet()) {
            Long traf = whole.get(ip);
            if (maxTraf < traf) {
                maxTraf = traf;
                maxIp = ip;
            }
        }
        return maxIp;
    }

    public Long getTraffic(String ip) {
        return whole.get(ip);
    }

    public synchronized void markAsParsed(String log) {
        //logIsParsed.put(log, TRUE);
        logIsParsed.put(log, Boolean.TRUE);
    }

    public synchronized Boolean isParsed(String log) {
        if (!logIsParsed.containsKey(log))
            logIsParsed.put(log, Boolean.FALSE);
        return logIsParsed.get(log);
        //AtomicBoolean yes = logIsParsed.putIfAbsent(log, FALSE);
        //return yes != null && yes.get();
    }

    public void reportParsingPerformance(ParseLogs.Performance p) {
        parsingPerformance.add(p);
    }

    public ParseLogs.Performance averageParsingPerformance() {
        return ParseLogs.Performance.average(parsingPerformance);
    }
}

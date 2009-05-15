package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;


public class Aggregator {
    private static final AtomicBoolean TRUE = new AtomicBoolean(true);
    private static final AtomicBoolean FALSE = new AtomicBoolean(false);
    private final ConcurrentMap<String, Long> ipTraffic = new ConcurrentHashMap<String, Long>();
    private final ConcurrentMap<String, AtomicBoolean> logIsParsed = new ConcurrentHashMap<String, AtomicBoolean>();

    public synchronized void addStatistics(Map<String, Long> statistics) {
        for (String ip : statistics.keySet())
            addStatistics(ip, statistics.get(ip));
    }

    private void addStatistics(String ip, Long traffic) {
        ipTraffic.putIfAbsent(ip, 0L);
        ipTraffic.put(ip, ipTraffic.get(ip) + traffic);
    }

    public Long getUserStat(String ip) {
        return ipTraffic.get(ip);
    }

    public String getIpWithMaxTraffic() {
        String maxIp = null;
        Long sum = Long.MIN_VALUE;
        for (String ip : ipTraffic.keySet()) {
            long traf = ipTraffic.get(ip);
            if (sum < traf) {
                sum = traf;
                maxIp = ip;
            }
        }
        return maxIp;
    }

    public void markAsParsed(String log) {
        logIsParsed.put(log, TRUE);
    }

    public Boolean isParsed(String log) {
        AtomicBoolean isParsed = logIsParsed.putIfAbsent(log, FALSE);
        return isParsed != null && isParsed.get();
    }

    @Override
    public String toString() {
        return ipTraffic.toString();
    }
}

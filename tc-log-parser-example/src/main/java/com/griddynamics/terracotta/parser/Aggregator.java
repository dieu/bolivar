package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import com.griddynamics.terracotta.parser.separate_downloading.ParseLogs;


public class Aggregator {
    private static final AtomicBoolean TRUE = new AtomicBoolean(true);
    private static final AtomicBoolean FALSE = new AtomicBoolean(false);
    private final ConcurrentMap<String, Long> ipTraffic = new ConcurrentHashMap<String, Long>();
    private final List<ParseLogs.Performance> parsingPerformance = Collections.synchronizedList(new LinkedList<ParseLogs.Performance>());
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
        AtomicBoolean yes = logIsParsed.putIfAbsent(log, FALSE);
        return yes != null && yes.get();
    }

    public void reportParsingPerformance(ParseLogs.Performance p) {
        parsingPerformance.add(p);
    }

    public ParseLogs.Performance averageParsingPerformance() {
        return ParseLogs.Performance.average(parsingPerformance);
    }

    @Override
    public String toString() {
        return ipTraffic.toString();
    }
}

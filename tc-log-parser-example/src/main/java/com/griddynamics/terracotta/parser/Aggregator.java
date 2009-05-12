package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class Aggregator {
    private ConcurrentMap<String, AtomicLong> statistics = new ConcurrentHashMap<String, AtomicLong>();

    /**
     * Adds or updates ip->traf pair.
     *
     * @param ip    - ip for which traffic is calculated.
     * @param count - traffic for the ip.
     */
    public void addStatitics(String ip, Long count) {
        AtomicLong oldSum = statistics.get(ip);
        if (oldSum == null) {
            oldSum = new AtomicLong(0L);
            statistics.putIfAbsent(ip, oldSum);
            oldSum = statistics.get(ip);
        }
        oldSum.addAndGet(count);
    }

    @Override
    public String toString() {
        return statistics.toString();
    }

    /**
     * Returns traffic of given ip.
     *
     * @param ip - ip.
     * @return traffic of given ip.
     */
    public Long getUserStat(String ip) {
        return statistics.get(ip).longValue();
    }

    /**
     * Finds ip with max traffic value.
     *
     * @return ip with max traffic value.
     */
    public String getIpWithMaxTraffic() {
        String maxIp = null;
        Long sum = Long.MIN_VALUE;
        for (String ip : statistics.keySet()) {
            long traf = statistics.get(ip).longValue();
            if (sum < traf) {
                sum = traf;
                maxIp = ip;
            }
        }
        return maxIp;
    }
}

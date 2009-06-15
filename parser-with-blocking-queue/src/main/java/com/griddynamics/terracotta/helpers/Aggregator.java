package com.griddynamics.terracotta.helpers;

import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import java.util.HashMap;

import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

/**
 * @author agorbunov @ 08.05.2009 15:10:15
 */
public class Aggregator {
    private ConcurrentMap<String, ConcurrentStringMap<Long>> traffic;
    private ConcurrentStringMap<Long> times;
    private transient long maxTraffic = Integer.MIN_VALUE;
    private transient String ipMaxTraffic = null;

    public Aggregator() {
        traffic = new ConcurrentStringMap<ConcurrentStringMap<Long>>();
    }

    public void add(String hostWorker, ConcurrentStringMap<Long> part, long time) {
        traffic.putIfAbsent(hostWorker, part);
        times.putIfAbsent(hostWorker, time);
    }

    public synchronized String ipWithMaxTraffic() {
        if(ipMaxTraffic == null) {
            Map<String, Long> total = new HashMap<String, Long>();
            for(String key: traffic.keySet()) {
                Map<String, Long> value = traffic.get(key);
                for(String valueKey: value.keySet()) {
                    if(total.containsKey(valueKey)) {
                        long traff = total.get(valueKey);
                        traff += value.get(valueKey);
                        total.put(valueKey, traff);
                    } else {
                        total.put(valueKey, value.get(valueKey));
                    }
                }
            }
            for(String key: total.keySet()) {
                long traff = total.get(key);
                if(traff > maxTraffic) {
                    maxTraffic = traff;
                    ipMaxTraffic = key;
                }
            }
            return ipMaxTraffic;
        } else {
            return ipMaxTraffic;
        }
    }

    public long getTraffic() {
        return maxTraffic;
    }

    public long getAvgTimeParsing() {
        long sum = 0;
        int i = 0;
        for(Long time: times.values()) {
            sum += time;
            i++;
        }
        sum /= i;
        return sum;
    }
}

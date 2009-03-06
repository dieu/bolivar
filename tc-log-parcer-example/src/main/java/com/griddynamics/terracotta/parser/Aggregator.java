package com.griddynamics.terracotta.parser;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class Aggregator {
    private ConcurrentMap<String, AtomicLong> statistics;

    public Aggregator() {
        statistics = new ConcurrentHashMap<String,AtomicLong>();
    }

    public void addStatitics(String ip,Long count){
        AtomicLong oldSum = statistics.get(ip);
        if(oldSum == null){
            oldSum = new AtomicLong(0L);
            statistics.putIfAbsent(ip,oldSum);
            oldSum = statistics.get(ip);
        }
        oldSum.addAndGet(count);           
    }

    @Override
    public String toString() {
        return statistics.toString();
    }


    public Long getUserStat(String ip){
        return statistics.get(ip).longValue();
    }

    public String getMaxIp() {
        String maxIp = null;
        Long sum = Long.MIN_VALUE;
        for(String ip:statistics.keySet()){
            if (sum < statistics.get(ip).longValue()){
                sum = statistics.get(ip).longValue();
                maxIp = ip;
            }
        }
        return maxIp;
    }
}

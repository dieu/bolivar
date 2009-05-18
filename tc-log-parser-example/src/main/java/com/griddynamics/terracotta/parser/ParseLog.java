package com.griddynamics.terracotta.parser;

import commonj.work.Work;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ParseLog implements Work {
    private static final String PROTOCOL = "http";
    private static final String DATA_SEPARATOR = "\t";
    private static final int IP_INDEX = 1;
    private static final int TRAF_INDEX = 4;
    private String log;
    private Aggregator aggregator;

    public ParseLog(File log, Aggregator aggregator) {
        this(log.getPath(), aggregator);
    }

    public ParseLog(String log, Aggregator aggregator) {
        this.log = log;
        this.aggregator = aggregator;
    }

    /**
     * Fires up statistics calculations for one log file.
     */
    public void run() {
        Map<String, Long> sum = parseLog();
        reportToAggregator(sum);
    }

    private Map<String, Long> parseLog() {
        try {
            return calculateSummForAllRecods();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Long> calculateSummForAllRecods() throws IOException {
        Map<String, Long> sum = new HashMap<String, Long>();
        BufferedReader bufferedReader = getReader();

        String s = bufferedReader.readLine();
        while (s != null) {
            String[] strings = s.split(DATA_SEPARATOR);
            String ip = strings[IP_INDEX];
            long traf = Long.parseLong(strings[TRAF_INDEX]);
            putOrUpdateSumEntry(sum, ip, traf);
            s = bufferedReader.readLine();
        }
        bufferedReader.close();
        return sum;
    }

    private BufferedReader getReader() throws IOException {
        InputStreamReader inputStreamReader;
        if (log.startsWith(PROTOCOL)) {
            inputStreamReader = new InputStreamReader(new URL(log).openStream());
        } else {
            inputStreamReader = new FileReader(log);
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader;
    }

    private void putOrUpdateSumEntry(Map<String, Long> sum, String ip, long n) {
        Long ipSum = sum.get(ip);
        if (ipSum == null) {
            ipSum = n;
        } else {
            ipSum += n;
        }
        sum.put(ip, ipSum);
    }

    private void reportToAggregator(Map<String, Long> sum) {
        aggregator.addStatistics(sum);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}

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

    public void run() {
        parseAndReport();
    }

    public void parseAndReport() {
        Map<String, Long> trafficByIp = new HashMap<String, Long>();
        parseTo(trafficByIp);
        report(trafficByIp);
    }

    public void parseTo(Map<String, Long> ipTraffic) {
        try {
            analyzeLineByLineTo(ipTraffic);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void analyzeLineByLineTo(Map<String, Long> ipTraffic) throws IOException {
        BufferedReader log = getReader();
        String record = log.readLine();
        while (record != null) {
            String[] parsedRecord = record.split(DATA_SEPARATOR);
            String ip = parsedRecord[IP_INDEX];
            Long traffic = Long.parseLong(parsedRecord[TRAF_INDEX]);
            putOrUpdateSumEntry(ipTraffic, ip, traffic);
            record = log.readLine();
        }
        log.close();
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

    private void report(Map<String, Long> sum) {
        aggregator.addStatistics(sum);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}

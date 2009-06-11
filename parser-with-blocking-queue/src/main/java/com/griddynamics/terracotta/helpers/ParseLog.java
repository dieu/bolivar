package com.griddynamics.terracotta.helpers;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ParseLog {
    private static final String PROTOCOL = "http";
    private static final String DATA_SEPARATOR = "\t";
    private static final int IP_INDEX = 1;
    private static final int TRAF_INDEX = 4;
    private String log;

    public ParseLog(File log) {
        this(log.getPath());
    }

    public ParseLog(String log) {
        this.log = log;
    }

    public void parseTo(Map<String, Long> ipTraffic) {
        BufferedReader log;
        try {
            log = getReader();
            String record = log.readLine();
            while (record != null) {
                String[] parsedRecord = record.split(DATA_SEPARATOR);
                String ip = parsedRecord[IP_INDEX];
                Long traffic = Long.parseLong(parsedRecord[TRAF_INDEX]);
                putOrUpdateSumEntry(ipTraffic, ip, traffic);
                record = log.readLine();
            }
            log.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader getReader() throws IOException {
        InputStreamReader inputStreamReader;
        if (log.startsWith(PROTOCOL)) {
            inputStreamReader = new InputStreamReader(new URL(log).openStream());
        } else {
            inputStreamReader = new FileReader(log);
        }
        return new BufferedReader(inputStreamReader);
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
}

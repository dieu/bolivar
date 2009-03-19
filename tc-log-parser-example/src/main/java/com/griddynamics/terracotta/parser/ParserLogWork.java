package com.griddynamics.terracotta.parser;

import commonj.work.Work;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

import com.griddynamics.terracotta.parser.Aggregator;


public class ParserLogWork implements Work {

    private final String pathToFile;
    private final Aggregator aggregator;

    private static final String PROTOCOL = "http";
    private static final String DATA_SEPARATOR = "\t";
    private static final int IP_INDEX = 1;
    private static final int TRAF_INDEX = 4;

    /**
     * Just a parametrized constructor.
     * @param pathToFile - path to log file.
     * @param aggregator - Arrgegator istance.
     */
    public ParserLogWork(String pathToFile, Aggregator aggregator) {
        this.pathToFile = pathToFile;
        this.aggregator = aggregator;
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {

    }

    /**
     * Fires up statistics calculations for one log file.
     */
    public void run() {
        try {
            HashMap<String, Long> sum = calculateSummForAllRecods();
            for (String ip : sum.keySet()) {
                aggregator.addStatitics(ip, sum.get(ip));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Long> calculateSummForAllRecods() throws IOException {
        HashMap<String, Long> sum = new HashMap<String, Long>();
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
        if (pathToFile.startsWith(PROTOCOL)) {
            inputStreamReader = new InputStreamReader(new URL(pathToFile).openStream());
        } else {
            inputStreamReader = new FileReader(pathToFile);
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader;
    }

    private void putOrUpdateSumEntry(HashMap<String, Long> sum, String ip, long n) {
        Long ipSum = sum.get(ip);
        if (ipSum == null) {
            ipSum = n;
        } else {
            ipSum += n;
        }
        sum.put(ip, ipSum);
    }
}

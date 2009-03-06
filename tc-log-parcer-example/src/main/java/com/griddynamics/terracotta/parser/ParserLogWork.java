package com.griddynamics.terracotta.parser;

import commonj.work.Work;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

import com.griddynamics.terracotta.parser.Aggregator;


public class ParserLogWork implements Work {
    final private String pathToFile;
    final Aggregator aggregator;

    public ParserLogWork(String pathToFile, Aggregator aggregator) {
        this.pathToFile = pathToFile;
        this.aggregator = aggregator;
    }


    public boolean isDaemon() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void release() {

    }

    public void run() {
        try {
            HashMap<String, Long> sum = new HashMap<String, Long>();
            InputStreamReader inputStreamReader = null;
            if (pathToFile.startsWith("http")) {
                inputStreamReader = new InputStreamReader(new URL(pathToFile).openStream());
            } else {
                inputStreamReader = new FileReader(pathToFile);
            }
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = bufferedReader.readLine();
            while (s != null) {
                String[] strings = s.split("\t");
                addSum(sum,strings[1], Long.parseLong(strings[4]));
                s = bufferedReader.readLine();                
            }
            bufferedReader.close();

            for(String ip:sum.keySet()){
               aggregator.addStatitics(ip,sum.get(ip)); 
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void addSum(HashMap<String, Long> sum, String ip, long n) {
        Long ipSum = sum.get(ip);
        if (ipSum == null){
            ipSum = n;
        }else{
            ipSum += n;
        }
        sum.put(ip,ipSum);
    }
}

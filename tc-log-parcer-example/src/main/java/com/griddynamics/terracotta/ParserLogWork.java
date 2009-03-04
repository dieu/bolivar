package com.griddynamics.terracotta;

import commonj.work.Work;

import java.io.*;
import java.net.URL;


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
                aggregator.addStatitics(strings[1], Long.parseLong(strings[4]));
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}

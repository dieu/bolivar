package com.griddynamics.terracotta;

import java.util.Properties;
import java.io.IOException;


public class StartSheduler {

    /**
     * Launches sheduler.
     *
     * @param args - input params.
     * @throws IOException - (new JobService()).lunchJob()'s exception
     * @throws InterruptedException - (new JobService()).lunchJob()'s exception
     */

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("sheduler.properties"));
        } catch (Exception e) {
            System.exit(1);
        }
        (new JobService()).lunchJob(properties.getProperty("localDir"), properties.getProperty("httpUrl"));
    }


}

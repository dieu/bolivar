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
        (new JobService()).lunchJob(System.getProperty("localDir"), System.getProperty("httpUrl"), System.getProperty("downloadedDir"));
        System.exit(0);
    }
}

package com.griddynamics.terracotta;

import com.griddynamics.terracotta.scheduler.Scheduler;

import java.io.IOException;


public class StartSheduler {

    public static void main(String[] args) throws IOException, InterruptedException {
        String masterDir = System.getProperty("localDir");
        String masterUrl = System.getProperty("httpUrl");
        String workerDir = System.getProperty("downloadedDir");
        String workerCount = System.getProperty("countWorkers");
        Scheduler scheduler = new Scheduler(workerCount, masterDir, masterUrl, workerDir);
        scheduler.run();
        System.exit(0);
    }
}

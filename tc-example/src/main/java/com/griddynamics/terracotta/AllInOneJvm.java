package com.griddynamics.terracotta;

import java.util.Properties;


public class AllInOneJvm {

    /**
     * Launches 3 workers and 1 sheduler in one JVM.
     * @param args - input args.
     * @throws Exception - jobService.startWorker()'s exception.
     */
    public static void main(String[] args) throws Exception {
        JobService jobService = new JobService();
        jobService.startWorker(System.getProperty("typeOfWork"));
        jobService.startWorker(System.getProperty("typeOfWork"));
        jobService.startWorker(System.getProperty("typeOfWork"));
        jobService.lunchJob(System.getProperty("localDir"), System.getProperty("httpUrl"),
                System.getProperty("downloadedDir"), System.getProperty("countWorkers"));
    }
}

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
        jobService.startWorker();
        jobService.startWorker();
        jobService.startWorker();
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("sheduler.properties"));
        } catch (Exception e) {
            System.out.println("no property file found");
            System.exit(1);
        }

        jobService.lunchJob(System.getProperty("localDir"), System.getProperty("httpUrl"));
    }
}

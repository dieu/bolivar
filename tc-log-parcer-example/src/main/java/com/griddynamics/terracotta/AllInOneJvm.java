package com.griddynamics.terracotta;

import java.util.Properties;


public class AllInOneJvm {

    public static void main(String[] args) throws Exception {
        Example example = new Example();
        example.startWorker();
        example.startWorker();
        example.startWorker();
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("sheduler.properties"));
        }catch (Exception e){
            System.out.println("no property file found");
            System.exit(1);
        }
        example.lunchJob(properties.getProperty("localDir"),properties.getProperty("httpUrl")); 
    }
}

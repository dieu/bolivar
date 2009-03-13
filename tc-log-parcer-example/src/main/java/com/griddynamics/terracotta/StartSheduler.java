package com.griddynamics.terracotta;

import java.util.Properties;


public class StartSheduler {


    public static void main(String[] args) throws Exception {
        Example example = new Example();
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("sheduler.properties"));
        } catch (Exception e) {
            System.exit(1);
        }
        example.lunchJob(properties.getProperty("localDir"), properties.getProperty("httpUrl"));
    }


}

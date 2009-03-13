package com.griddynamics.terracotta;

import java.util.Properties;


public class StartSheduler {



    public static void main(String[] args) throws Exception {
        Example example = new Example();     
        example.lunchJob(System.getProperty("localDir"),System.getProperty("httpUrl"));
    }

    
}
